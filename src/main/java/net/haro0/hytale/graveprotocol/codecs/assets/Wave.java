package net.haro0.hytale.graveprotocol.codecs.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.lookup.MapKeyMapCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.Getter;
import net.haro0.hytale.graveprotocol.codecs.data.Attacker;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Wave implements JsonAssetWithMap<String, AssetMap<String, Wave>> {

    public static final AssetBuilderCodec<String,Wave> CODEC = AssetBuilderCodec.builder(Wave.class, Wave::new, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .append(new KeyedCodec<>("Enemies", new ArrayCodec<>(WaveEnemy.CODEC, WaveEnemy[]::new)), (w, e) -> w.enemies = e, w -> w.enemies)
        .add()
        .build();

    private static AssetStore<String, Wave, AssetMap<String, Wave>> ASSET_STORE;

    private String id;

    private AssetExtraInfo.Data data;

    private WaveEnemy[] enemies;

    public static AssetStore<String, Wave, AssetMap<String, Wave>> getAssetStore() {

        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Wave.class);
        }

        return ASSET_STORE;
    }

    public static AssetMap<String, Wave> getAssetMap() {

        return getAssetStore().getAssetMap();
    }

    public static class WaveEnemy{

        public static final BuilderCodec<WaveEnemy> CODEC = BuilderCodec.builder(WaveEnemy.class,WaveEnemy::new)
            .append(new KeyedCodec<>("Enemy", new ContainedAssetCodec<>(Enemy.class,Enemy.CODEC)), (w, e) -> w.enemy = e, w -> w.enemy)
            .addValidator(Validators.nonEmptyString())
            .add()
            .append(new KeyedCodec<>("Count", Codec.INTEGER), (w, c) -> w.count = c, w -> w.count)
            .addValidator(Validators.min(1))
            .add()
            .build();

        private String enemy;

        @Getter
        private int count;

        public Enemy getEnemy(){
            return Enemy.getAssetMap().getAsset(enemy);
        }
    }
}
