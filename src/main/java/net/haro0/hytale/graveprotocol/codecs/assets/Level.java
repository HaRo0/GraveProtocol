package net.haro0.hytale.graveprotocol.codecs.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.AccessLevel;
import lombok.Getter;
import net.haro0.hytale.graveprotocol.codecs.data.Defender;
import net.haro0.hytale.graveprotocol.codecs.data.MultiplierCollection;

import java.util.Arrays;

@Getter
public class Level implements JsonAssetWithMap<String, AssetMap<String, Level>> {

    public static final AssetBuilderCodec<String, Level> CODEC = AssetBuilderCodec.builder(Level.class, Level::new, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .append(new KeyedCodec<>("Waves", new ArrayCodec<>(new ContainedAssetCodec<>(Wave.class, Wave.CODEC), String[]::new)), (w, e) -> w.waves = e, w -> w.waves)
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
        .append(new KeyedCodec<>("ShopStats", Defender.CODEC), (w, s) -> w.shopStats = s, w -> w.shopStats)
        .add()
        .append(new KeyedCodec<>("Multipliers", MultiplierCollection.CODEC), (w, v) -> w.multipliers = v, w -> w.multipliers)
        .add()
        .append(new KeyedCodec<>("MaterialStartAmount", Codec.INTEGER), (w, v) -> w.materialStartAmount = v, w -> w.materialStartAmount)
        .add()
        .append(new KeyedCodec<>("CurrencyReward", Codec.INTEGER), (w, v) -> w.currencyReward = v, w -> w.currencyReward)
        .add()
        .build();

    private static AssetStore<String, Level, AssetMap<String, Level>> ASSET_STORE;

    private String id;

    private AssetExtraInfo.Data data;

    @Getter(AccessLevel.NONE)
    private String[] waves;

    private int order;

    private int minPrestige;

    private int maxPrestige;

    private Defender shopStats;

    private MultiplierCollection multipliers;
    private int materialStartAmount = 20;

    private int currencyReward = 3;

    public Wave[] getWaves(){
        return Arrays.stream(waves).map(Wave.getAssetMap()::getAsset).toArray(Wave[]::new);
    }

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
