package net.haro0.hytale.graveprotocol.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.Getter;

@Getter
public class Level implements JsonAssetWithMap<String, AssetMap<String, Level>> {

    public static final AssetBuilderCodec<String, Level> CODEC = AssetBuilderCodec.builder(Level.class, Level::new, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .append(new KeyedCodec<>("Waves", new ArrayCodec<>(Wave.CODEC, Wave[]::new)), (w, e) -> w.waves = e, w -> w.waves)
        .addValidator(Validators.nonEmptyArray())
        .add()
        .append(new KeyedCodec<>("Order", Codec.INTEGER), (w, o) -> w.order = o, w -> w.order)
        .addValidator(Validators.min(0))
        .add()
        .append(new KeyedCodec<>("MinPrestige", Codec.INTEGER), (w, m) -> w.minPrestige = m, w -> w.minPrestige)
        .addValidator(Validators.min(0))
        .add()
        .append(new KeyedCodec<>("MaxPrestige", Codec.INTEGER), (w, m) -> w.maxPrestige = m, w -> w.maxPrestige)
        .addValidator(Validators.min(-1))
        .add()
        .append(new KeyedCodec<>("HealthMultiplier", Codec.FLOAT), (w, h) -> w.healthMultiplier = h, w -> w.healthMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("AttackMultiplier", Codec.FLOAT), (w, a) -> w.attackMultiplier = a, w -> w.attackMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .build();

    private static AssetStore<String, Level, AssetMap<String, Level>> ASSET_STORE;

    private String id;

    private AssetExtraInfo.Data data;

    private Wave[] waves;

    private int order;

    private int minPrestige;

    private int maxPrestige;

    private float healthMultiplier = 1.0f;

    private float attackMultiplier = 1.0f;

    public static AssetStore<String, Level, AssetMap<String, Level>> getAssetStore() {

        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Level.class);
        }

        return ASSET_STORE;
    }

    public static AssetMap<String, Level> getAssetMap() {

        return getAssetStore().getAssetMap();
    }

    public static class Wave {

        public static final BuilderCodec<Wave> CODEC = BuilderCodec.builder(Wave.class, Wave::new)
            .append(new KeyedCodec<>("Entity", Codec.STRING), (w, e) -> w.entity = e, w -> w.entity)
            .addValidator(Validators.nonEmptyString())
            .metadata(new UIEditor(new UIEditor.Dropdown("GraveProtocolWaveEntities")))
            .add()
            .append(new KeyedCodec<>("Count", Codec.INTEGER), (w, c) -> w.count = c, w -> w.count)
            .addValidator(Validators.min(1))
            .add()
            .append(new KeyedCodec<>("HealthMultiplier", Codec.FLOAT), (w, h) -> w.healthMultiplier = h, w -> w.healthMultiplier)
            .addValidator(Validators.min(0.1f))
            .add()
            .append(new KeyedCodec<>("AttackMultiplier", Codec.FLOAT), (w, a) -> w.attackMultiplier = a, w -> w.attackMultiplier)
            .addValidator(Validators.min(0.1f))
            .add()
            .append(new KeyedCodec<>("WaveDelay", Codec.INTEGER), (w, d) -> w.waveDelay = d, w -> w.waveDelay)
            .addValidator(Validators.min(0))
            .add()
            .append(new KeyedCodec<>("DelayAfterKill", Codec.BOOLEAN), (w, d) -> w.delayAfterKill = d, w -> w.delayAfterKill)
            .add()
            .append(new KeyedCodec<>("SpawnPositionStart", Codec.INTEGER), (w, s) -> w.spawnPositionIndex = s, w -> w.spawnPositionIndex)
            .addValidator(Validators.min(0))
            .add()
            .append(new KeyedCodec<>("SpawnPositionEnd", Codec.INTEGER), (w, s) -> w.spawnPositionIndex = s, w -> w.spawnPositionIndex)
            .addValidator(Validators.min(0))
            .add()
            .build();

        private String entity;

        private int count = 1;

        private float healthMultiplier = 1.0f;

        private float attackMultiplier = 1.0f;

        private int waveDelay = 0;

        private boolean delayAfterKill = false;

        private int spawnPositionIndex = 0;
    }
}
