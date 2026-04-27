package net.haro0.hytale.graveprotocol.codecs.assets;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorRequestDataSetEvent;
import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.npc.NPCPlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GPAssets {

    private final EventRegistry registry;

    public void registerAll() {

        registerLevel();
        registerEnemy();
        registerWave();
        registerPrestige();
        registerTower();
    }

    private void registerLevel() {

        AssetRegistry.register(HytaleAssetStore.builder(
                    Level.class, new DefaultAssetMap<>()
                )
                .setPath("GraveProtocol/Levels")
                .setCodec(Level.CODEC)
                .setKeyFunction(Level::getId)
                .build()
        );

    }

    private void registerWave() {

        AssetRegistry.register(HytaleAssetStore.builder(
                    Wave.class, new DefaultAssetMap<>()
                )
                .setPath("GraveProtocol/Waves")
                .setCodec(Wave.CODEC)
                .setKeyFunction(Wave::getId)
                .build()
        );

    }

    private void registerEnemy() {

        AssetRegistry.register(HytaleAssetStore.builder(
                    Enemy.class, new DefaultAssetMap<>()
                )
                .setPath("GraveProtocol/Enemies")
                .setCodec(Enemy.CODEC)
                .setKeyFunction(Enemy::getId)
                .build()
        );
        registry.register(AssetEditorRequestDataSetEvent.class, "GraveProtocolWaveEntities", e -> {
            e.setResults(ModelAsset.getAssetMap().getAssetMap().keySet().toArray(new String[0]));
        });
    }

    private void registerPrestige() {

        AssetRegistry.register(HytaleAssetStore.builder(
                    Prestige.class, new DefaultAssetMap<>()
                )
                .setPath("GraveProtocol/Prestiges")
                .setCodec(Prestige.CODEC)
                .setKeyFunction(Prestige::getId)
                .build()
        );

        registry.register(AssetEditorRequestDataSetEvent.class, "GraveProtocolInstances", e -> {
            e.setResults(InstancesPlugin.get().getInstanceAssets().toArray(new String[0]));
        });
    }

    private void registerTower() {

        AssetRegistry.register(HytaleAssetStore.builder(
                    Tower.class, new DefaultAssetMap<>()
                )
                .setPath("GraveProtocol/Towers")
                .setCodec(Tower.CODEC)
                .setKeyFunction(Tower::getId)
                .build()
        );
    }
}
