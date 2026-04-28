package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.player.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;
import net.haro0.hytale.graveprotocol.utils.LevelUtils;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LynnAttackerDeathSystem extends DeathSystems.OnDeathSystem {

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl DeathComponent component, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        var attackerComponent = store.getComponent(ref, LynnAttackerComponent.getComponentType());
        var lynnId = attackerComponent.getLynnId();
        if(lynnId == null) return;
        var lynnRef = store.getExternalData().getRefFromUUID(lynnId);
        var lynnComponent = store.getComponent(lynnRef, LynnComponent.getComponentType());

        var killReward = attackerComponent.getMaterialReward();
        if (killReward > 0) {
            lynnComponent.addMaterial(killReward);
        }

        if(!lynnComponent.markAttackerKilled()) return;

        var pRef = store.getExternalData().getRefFromUUID(lynnComponent.getPlayerId());

        var level = LevelUtils.getPlayerLevel(pRef, commandBuffer);
        var dataComponent = store.getComponent(pRef, GPPlayerDataComponent.getComponentType());

        var waves = level.getWaves();
        var waveIndex = lynnComponent.getWaveIndex();
        if (waveIndex >= 0 && waveIndex < waves.length) {
            var wave = waves[waveIndex];
            if (wave != null) {
                lynnComponent.addMaterial(wave.getMaterialReward());
                if (dataComponent != null) {
                    dataComponent.addCurrency(wave.getCurrencyReward());
                }
            }
        }


        if(LevelStartService.startNextWave(pRef,commandBuffer,store.getExternalData().getWorld(), lynnRef)) return;

        var playerRef = store.getComponent(pRef, PlayerRef.getComponentType());


        dataComponent.addCurrency(level.getCurrencyReward());
        if (playerRef != null) {
            var permReward = level.getCurrencyReward();
            store.getExternalData().getWorld().execute(() ->
                playerRef.sendMessage(Message.raw("Congratulations! You have completed the level! +" + permReward + " currency"))
            );
        }
        dataComponent.setLevelIndex(dataComponent.getLevelIndex()+1);
        lynnComponent.setActive(false);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {

        return LynnAttackerComponent.getComponentType();
    }
}
