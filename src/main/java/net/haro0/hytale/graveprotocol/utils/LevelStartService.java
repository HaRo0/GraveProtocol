package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import net.haro0.hytale.graveprotocol.assets.Wave;
import net.haro0.hytale.graveprotocol.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.components.LynnComponent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class LevelStartService {

    private static final String PATH_TARGET_SLOT = "LockedTarget";
    private static final String PATH_TARGET_STATE = "Alerted";

    private LevelStartService() {
    }

    public static void startLevel(Ref<EntityStore> ref, Store<EntityStore> store) {

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        var playerId = store.getComponent(ref, UUIDComponent.getComponentType());
        if (playerId == null) {
            return;
        }

        var level = LevelUtils.getPlayerLevel(ref, store);
        if (level == null) {
            return;
        }

        var data = store.ensureAndGetComponent(ref, GPPlayerDataComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(data);
        var spawnPositions = prestige.getPositions();
        if (spawnPositions == null || spawnPositions.length == 0) {
            return;
        }

        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null) {
            playerRef.sendMessage(Message.raw("Starting level " + level.getId() + "..."));
        }

        World world = player.getWorld();
        long totalDelaySeconds = 0;
        Wave[] waves = level.getWaves();

        var pathTarget = findTarget(store);

        for (int i = 0; i < waves.length; i++) {
            Wave wave = waves[i];
            long scheduledDelay = totalDelaySeconds;
            runLater(world, scheduledDelay, () -> spawnWave(ref, store, world, wave, spawnPositions, pathTarget));
            totalDelaySeconds += Math.max(0, wave.getWaveDelay());
        }
    }

    private static void spawnWave(
        Ref<EntityStore> ref,
        Store<EntityStore> store,
        World world,
        Wave wave,
        Vector3d[] spawnPositions,
        Ref<EntityStore> pathTarget
    ) {

        if (!ref.isValid()) {
            return;
        }

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null || player.getWorld() != world) {
            return;
        }

        var npcPlugin = NPCPlugin.get();
        int roleIndex = npcPlugin.getIndex(wave.getEntity());
        if (roleIndex < 0) {
            return;
        }

        int startIndex = Math.clamp(wave.getSpawnPositionIndex(), 0, spawnPositions.length - 1);
        for (int i = 0; i < wave.getCount(); i++) {
            Vector3d spawnPos = spawnPositions[(startIndex + i) % spawnPositions.length].clone();
            var spawnedNpc = npcPlugin.spawnNPC(store, wave.getEntity(), null, spawnPos, Vector3f.ZERO);
            if (spawnedNpc != null) {
                var npcRef = spawnedNpc.first();
                store.ensureComponent(npcRef, Invulnerable.getComponentType());

                if (pathTarget != null) {
                    assignPathTarget(npcRef, pathTarget, store);
                }
            }
        }
    }

    private static Ref<EntityStore> findTarget(Store<EntityStore> store) {

        var target = new AtomicReference<Ref<EntityStore>>();
        store.forEachChunk(LynnComponent.getComponentType(), (chunk, ignored) -> {
            if (chunk.size() == 0) {
                return false;
            }

            target.set(chunk.getReferenceTo(0));
            return true;
        });

        var found = target.get();
        if (found == null || !found.isValid()) {
            return null;
        }
        return found;
    }

    private static void runLater(World world, long delaySeconds, Runnable runnable) {

        if (delaySeconds <= 0) {
            world.execute(runnable);
            return;
        }

        CompletableFuture.delayedExecutor(delaySeconds, TimeUnit.SECONDS, world).execute(runnable);
    }

    private static void assignPathTarget(
        com.hypixel.hytale.component.Ref<EntityStore> npcRef,
        com.hypixel.hytale.component.Ref<EntityStore> targetRef,
        com.hypixel.hytale.component.Store<EntityStore> store
    ) {

        var npc = store.getComponent(npcRef, NPCEntity.getComponentType());
        if (npc == null || npc.getRole() == null) {
            return;
        }

        var role = npc.getRole();
        role.getMarkedEntitySupport().setMarkedEntity(PATH_TARGET_SLOT, targetRef);
        role.getStateSupport().setState(npcRef, PATH_TARGET_STATE, null, store);
    }
}

