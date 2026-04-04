package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import net.haro0.hytale.graveprotocol.assets.Wave;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class LevelStartService {

    private static final Vector3d PATH_TARGET_BLOCK_CENTER = new Vector3d(0.5, 80.0, 0.5);
    private static final String PATH_TARGET_SLOT = "LockedTarget";
    private static final String PATH_TARGET_STATE = "Alerted";
    private static final String[] TARGET_ANCHOR_ROLE_CANDIDATES = {"Sheep", "Test_Sheep", "Cow", "Pig"};

    private static final Set<UUID> ACTIVE_LEVELS = ConcurrentHashMap.newKeySet();
    private static final Map<String, com.hypixel.hytale.component.Ref<EntityStore>> WORLD_PATH_TARGETS = new ConcurrentHashMap<>();

    private LevelStartService() {
    }

    public static void startLevel(com.hypixel.hytale.component.Ref<EntityStore> ref, com.hypixel.hytale.component.Store<EntityStore> store) {

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        var playerId = store.getComponent(ref, UUIDComponent.getComponentType());
        if (playerId == null) {
            return;
        }

        UUID uuid = playerId.getUuid();
        if (!ACTIVE_LEVELS.add(uuid)) {
            var playerRef = store.getComponent(ref, com.hypixel.hytale.server.core.universe.PlayerRef.getComponentType());
            if (playerRef != null) {
                playerRef.sendMessage(Message.raw("A level is already running."));
            }
            return;
        }

        var level = LevelUtils.getPlayerLevel(ref, store);
        if (level == null) {
            ACTIVE_LEVELS.remove(uuid);
            return;
        }

        var data = store.ensureAndGetComponent(ref, net.haro0.hytale.graveprotocol.components.GPPlayerDataComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(data);
        var spawnPositions = prestige.getPositions();
        if (spawnPositions == null || spawnPositions.length == 0) {
            ACTIVE_LEVELS.remove(uuid);
            return;
        }

        var playerRef = store.getComponent(ref, com.hypixel.hytale.server.core.universe.PlayerRef.getComponentType());
        if (playerRef != null) {
            playerRef.sendMessage(Message.raw("Starting level " + level.getId() + "..."));
        }

        World world = player.getWorld();
        long totalDelaySeconds = 0;
        Wave[] waves = level.getWaves();

        for (int i = 0; i < waves.length; i++) {
            Wave wave = waves[i];
            long scheduledDelay = totalDelaySeconds;
            boolean lastWave = i == waves.length - 1;
            runLater(world, scheduledDelay, () -> spawnWave(ref, store, world, wave, spawnPositions, lastWave, uuid));
            totalDelaySeconds += Math.max(0, wave.getWaveDelay());
        }
    }

    private static void spawnWave(
        com.hypixel.hytale.component.Ref<EntityStore> ref,
        com.hypixel.hytale.component.Store<EntityStore> store,
        World world,
        Wave wave,
        Vector3d[] spawnPositions,
        boolean lastWave,
        UUID playerId
    ) {

        if (!ref.isValid()) {
            ACTIVE_LEVELS.remove(playerId);
            return;
        }

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null || player.getWorld() != world) {
            ACTIVE_LEVELS.remove(playerId);
            return;
        }

        var npcPlugin = NPCPlugin.get();
        int roleIndex = npcPlugin.getIndex(wave.getEntity());
        if (roleIndex < 0) {
            if (lastWave) {
                ACTIVE_LEVELS.remove(playerId);
            }
            return;
        }

        int startIndex = Math.max(0, Math.min(wave.getSpawnPositionIndex(), spawnPositions.length - 1));
        for (int i = 0; i < wave.getCount(); i++) {
            Vector3d spawnPos = spawnPositions[(startIndex + i) % spawnPositions.length].clone();
            var spawnedNpc = npcPlugin.spawnNPC(store, wave.getEntity(), null, spawnPos, Vector3f.ZERO);
            if (spawnedNpc != null) {
                var npcRef = spawnedNpc.first();
                store.ensureComponent(npcRef, Invulnerable.getComponentType());

                var pathTarget = ensurePathTarget(world, store);
                if (pathTarget != null) {
                    assignPathTarget(npcRef, pathTarget, store);
                }
            }
        }

        if (lastWave) {
            ACTIVE_LEVELS.remove(playerId);
        }
    }

    private static void runLater(World world, long delaySeconds, Runnable runnable) {

        if (delaySeconds <= 0) {
            world.execute(runnable);
            return;
        }

        CompletableFuture.delayedExecutor(delaySeconds, TimeUnit.SECONDS, world).execute(runnable);
    }

    private static com.hypixel.hytale.component.Ref<EntityStore> ensurePathTarget(World world, com.hypixel.hytale.component.Store<EntityStore> store) {

        var cached = WORLD_PATH_TARGETS.get(world.getName());
        if (cached != null && cached.isValid()) {
            return cached;
        }

        String roleName = pickAnchorRole();
        if (roleName == null) {
            return null;
        }

        var spawn = NPCPlugin.get().spawnNPC(store, roleName, null, PATH_TARGET_BLOCK_CENTER.clone(), Vector3f.ZERO);
        if (spawn == null) {
            return null;
        }

        var anchorRef = spawn.first();
        store.ensureComponent(anchorRef, Invulnerable.getComponentType());
        store.ensureComponent(anchorRef, Frozen.getComponentType());
        WORLD_PATH_TARGETS.put(world.getName(), anchorRef);
        return anchorRef;
    }

    private static String pickAnchorRole() {

        var npcPlugin = NPCPlugin.get();
        for (String candidate : TARGET_ANCHOR_ROLE_CANDIDATES) {
            if (npcPlugin.getIndex(candidate) >= 0) {
                return candidate;
            }
        }
        return null;
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

