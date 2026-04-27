package net.haro0.hytale.graveprotocol.codecs.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import lombok.Getter;
import net.haro0.hytale.graveprotocol.codecs.data.tower.attacking.AbstractTowerAttack;
import net.haro0.hytale.graveprotocol.codecs.data.tower.TowerUpgrade;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Tower extends TowerUpgrade implements JsonAssetWithMap<String, AssetMap<String, Tower>> {

    public static final AssetBuilderCodec<String,Tower> CODEC = AssetBuilderCodec.builder(Tower.class, Tower::new,TowerUpgrade.CODEC, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .append(new KeyedCodec<>("AttackType", AbstractTowerAttack.CODEC), (w, a) -> w.attackType = a, w -> w.attackType)
        .add()
        .append(new KeyedCodec<>("Upgrades", new MapCodec<>(new ArrayCodec<>(TowerUpgrade.CODEC, TowerUpgrade[]::new),HashMap::new)), (t,u) -> t.upgrades =u, t -> t.upgrades)
        .add()
        .build();

    private static AssetStore<String, Tower, AssetMap<String, Tower>> ASSET_STORE;
    private String id;
    protected AssetExtraInfo.Data data;
    protected AbstractTowerAttack attackType;
    protected Map<String, TowerUpgrade[]> upgrades = new HashMap<>();

    public static AssetStore<String, Tower, AssetMap<String, Tower>> getAssetStore() {

        if (Tower.ASSET_STORE == null) {
            Tower.ASSET_STORE = AssetRegistry.getAssetStore(Tower.class);
        }

        return Tower.ASSET_STORE;
    }

    public static AssetMap<String, Tower> getAssetMap() {

        return getAssetStore().getAssetMap();
    }

    @NullableDecl
    @Override
    public Tower clone() {
        return (Tower) super.clone();
    }
}