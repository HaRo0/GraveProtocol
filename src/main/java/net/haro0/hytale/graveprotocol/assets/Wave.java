package net.haro0.hytale.graveprotocol.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.Getter;

@Getter
public class Wave implements JsonAssetWithMap<String, AssetMap<String, Wave>> {

    public static final AssetBuilderCodec<String,Wave> CODEC = AssetBuilderCodec.builder(Wave.class, Wave::new, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .append(new KeyedCodec<>("Entity", Codec.STRING), (w, e) -> w.entity = e, w -> w.entity)
        .addValidator(Validators.nonEmptyString())
        .metadata(new UIEditor(new UIEditor.Dropdown("GraveProtocolWaveEntities")))
        .add()
        .append(new KeyedCodec<>("Count", Codec.INTEGER), (w, c) -> w.count = c, w -> w.count)
        .addValidator(Validators.min(1))
        .add()
        .append(new KeyedCodec<>("Health", Codec.INTEGER), (w, h) -> w.health = h, w -> w.health)
        .addValidator(Validators.min(1))
        .add()
        .append(new KeyedCodec<>("Attack", Codec.INTEGER), (w, a) -> w.attack = a, w -> w.attack)
        .addValidator(Validators.min(1))
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

    private static AssetStore<String, Level, AssetMap<String, Level>> ASSET_STORE;

    private String id;

    private AssetExtraInfo.Data data;

    private String entity;

    private int count = 1;

    private int health = 100;

    private int attack = 5;

    private int waveDelay = 0;

    private boolean delayAfterKill = false;

    private int spawnPositionIndex = 0;

    public static AssetStore<String, Level, AssetMap<String, Level>> getAssetStore() {

        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Level.class);
        }

        return ASSET_STORE;
    }

    public static AssetMap<String, Level> getAssetMap() {

        return getAssetStore().getAssetMap();
    }
}
