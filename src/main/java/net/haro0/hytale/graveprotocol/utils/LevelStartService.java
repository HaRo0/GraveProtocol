package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import net.haro0.hytale.graveprotocol.codecs.assets.Wave;
import net.haro0.hytale.graveprotocol.codecs.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class LevelStartService {

    private static final String PATH_TARGET_SLOT = "LockedTarget";
    private static final String PATH_TARGET_STATE = "Alerted";
    private static float ATTACKER_BASE_HEALTH = -1;

    private LevelStartService() {
    }

    public static void startLevel(Ref<EntityStore> ref, Store<EntityStore> store) {

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        var data = store.ensureAndGetComponent(ref, GPPlayerDataComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(data);
        var spawnPositions = prestige.getPositions();
        if (spawnPositions == null || spawnPositions.length == 0) {
            return;
        }
        
        World world = player.getWorld();

        var pathTarget = findTarget(store);

        var lynnComponent = store.getComponent(pathTarget, LynnComponent.getComponentType());

        if(lynnComponent == null) return;
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if(playerRef == null) return;
        if(lynnComponent.isActive()){
            playerRef.sendMessage(Message.raw("Level is already running!"));

            return;
        }
        var level = LevelUtils.getPlayerLevel(ref, store);
        if (level == null) {
            return;
        }
        Wave[] waves = level.getWaves();

        playerRef.sendMessage(Message.raw("Starting level " + level.getId() + "..."));


        lynnComponent.setWaveIndex(0);

        lynnComponent.setDefender(level.getShopStats());
        lynnComponent.setMultipliers(prestige.getMultipliers(),level.getMultipliers());
        lynnComponent.setActive(true);


        var statMap = store.getComponent(pathTarget, EntityStatMap.getComponentType());
        var prevHealth = statMap.get(DefaultEntityStatTypes.getHealth()).getMax();
        var additionalHealth = lynnComponent.getDefender().getHealth() * lynnComponent.getMultipliers().getShopHealthMultiplier() - prevHealth;
        statMap.putModifier(DefaultEntityStatTypes.getHealth(), "GraveProtocol", new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE,additionalHealth));
        statMap.maximizeStatValue(DefaultEntityStatTypes.getHealth());
        System.out.println("Set defender health to " + statMap.get(DefaultEntityStatTypes.getHealth()).get() + "/" + statMap.get(DefaultEntityStatTypes.getHealth()).getMax());
        if(waves.length < 1) return;
        spawnWave(ref, store, world, waves[0], spawnPositions, pathTarget,lynnComponent);
    }

    public static boolean startNextWave(
        Ref<EntityStore> playerRef,
        ComponentAccessor<EntityStore> store,
        World world,
        Ref<EntityStore> lynnRef){
        var dataComponent = store.getComponent(playerRef,GPPlayerDataComponent.getComponentType());
        var lynnComponent = store.getComponent(lynnRef, LynnComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(dataComponent);
        var level = LevelUtils.getPlayerLevel(playerRef,store);
        var waves = level.getWaves();
        var wIndex = lynnComponent.getWaveIndex()+1;
        if(waves.length <= wIndex) return false;
        lynnComponent.setWaveIndex(wIndex);

        spawnWave(playerRef,store,world,waves[wIndex],prestige.getPositions(),lynnRef,lynnComponent);
        return true;
    }

    private static void spawnWave(
        Ref<EntityStore> ref,
        ComponentAccessor<EntityStore> store,
        World world,
        Wave wave,
        Vector3d[] spawnPositions,
        Ref<EntityStore> pathTarget,
        LynnComponent lynnComponent
    ) {

        if (!ref.isValid()) {
            return;
        }

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null || player.getWorld() != world) {
            return;
        }

        var uuidComponent = store.getComponent(pathTarget,UUIDComponent.getComponentType());
        if(uuidComponent == null) return;
        var uuid = uuidComponent.getUuid();

        var npcPlugin = NPCPlugin.get();

        var entityId = npcPlugin.getIndex("Shadow_Knight");

        world.execute(() -> {
            var i = 0;
            var wStore = world.getEntityStore().getStore();
            for (var enemyData : wave.getEnemies()) {
                var enemy = enemyData.getEnemy();
                if(enemy == null) continue;
                var model = ModelAsset.getAssetMap().getAsset(enemy.getEntity());
                if (model == null) continue;
                for(var j = 0; j< enemyData.getCount(); j++){

                    Vector3d spawnPos = spawnPositions[i++ % spawnPositions.length];

                    var spawnedNpc = npcPlugin.spawnEntity(wStore, entityId, spawnPos, Vector3f.ZERO, Model.createScaledModel(model,1),null);
                    if (spawnedNpc == null) {
                        i--;
                        continue;
                    }
                    var npcRef = spawnedNpc.first();
                    wStore.addComponent(npcRef, LynnAttackerComponent.getComponentType(), new LynnAttackerComponent(enemy.getAttackData(),uuid));
                    var stats = wStore.getComponent(npcRef, EntityStatMap.getComponentType());
                    if(ATTACKER_BASE_HEALTH < 0){
                        ATTACKER_BASE_HEALTH = stats.get(DefaultEntityStatTypes.getHealth()).getMax();
                    }

                    var additionalHealth = enemy.getAttackData().getHealth() * lynnComponent.getMultipliers().getEnemyHealthMultiplier() - ATTACKER_BASE_HEALTH;
                    stats.putModifier(DefaultEntityStatTypes.getHealth(), "GraveProtocol", new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE,additionalHealth));
                    stats.maximizeStatValue(DefaultEntityStatTypes.getHealth());
                    if (pathTarget != null) {
                        assignPathTarget(npcRef, pathTarget, wStore);
                    }
                }
            }
            lynnComponent.setAttackersLeft(i);
        });


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

    private static void assignPathTarget(
        Ref<EntityStore> npcRef,
        Ref<EntityStore> targetRef,
        ComponentAccessor<EntityStore> store
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

