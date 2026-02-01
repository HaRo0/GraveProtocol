package net.haro0.hytale.graveprotocol.assets;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorRequestDataSetEvent;
import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.npc.NPCPlugin;

public class GPAssets {

    public static void registerAll(EventRegistry registry) {

        registerWave(registry);
        registerPrestige(registry);
    }

    private static void registerWave(EventRegistry registry) {

        AssetRegistry.register(HytaleAssetStore.builder(
                    Level.class, new DefaultAssetMap<>()
                )
                .setPath("GraveProtocol/Waves")
                .setCodec(Level.CODEC)
                .setKeyFunction(Level::getId)
                .build()
        );

        registry.register(AssetEditorRequestDataSetEvent.class, "GraveProtocolWaveEntities", e -> {
            e.setResults(NPCPlugin.get().getRoleTemplateNames(true).toArray(new String[0]));
        });
    }

    private static void registerPrestige(EventRegistry registry) {

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
}
