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
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import lombok.Getter;
import net.haro0.hytale.graveprotocol.codecs.data.tower.attacking.AbstractTowerAttack;
import net.haro0.hytale.graveprotocol.codecs.data.tower.TowerUpgrade;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Tower extends TowerUpgrade implements JsonAssetWithMap<String, AssetMap<String, Tower>> {

    public static final AssetBuilderCodec<String,Tower> CODEC = AssetBuilderCodec.builder(Tower.class, Tower::new,TowerUpgrade.CODEC, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .appendInherited(new KeyedCodec<>("TowerModel", new ContainedAssetCodec<>(BlockType.class, BlockType.CODEC, ContainedAssetCodec.Mode.INHERIT_ID)), (t, id) -> t.towerModel = id, t -> t.towerModel, (a, b) -> a.towerModel = b.towerModel)
        .add()
        .appendInherited(new KeyedCodec<>("AttackType", AbstractTowerAttack.CODEC), (w, a) -> w.attackType = a, w -> w.attackType, (a, b) -> a.attackType = b.attackType)
        .add()
        .appendInherited(new KeyedCodec<>("Upgrades", new MapCodec<>(new ArrayCodec<>(TowerUpgrade.CODEC, TowerUpgrade[]::new), HashMap::new)), (t, u) -> t.upgrades = u, t -> t.upgrades, (a, b) -> a.upgrades = new HashMap<>(b.upgrades))
        .add()
        .appendInherited(new KeyedCodec<>("ShopUnlockCost", Codec.INTEGER), (t, v) -> t.shopUnlockCost = v, t -> t.shopUnlockCost, (a, b) -> a.shopUnlockCost = b.shopUnlockCost)
        .add()
        .build();

    private static AssetStore<String, Tower, AssetMap<String, Tower>> ASSET_STORE;
    private String id;
    protected String towerModel;
    protected AssetExtraInfo.Data data;
    protected AbstractTowerAttack attackType;
    protected Map<String, TowerUpgrade[]> upgrades = new HashMap<>();
    protected int shopUnlockCost = 0;

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