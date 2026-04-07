package net.haro0.hytale.graveprotocol.codecs.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.Getter;
import net.haro0.hytale.graveprotocol.codecs.data.Attacker;

@Getter
public class Enemy implements JsonAssetWithMap<String, AssetMap<String, Enemy>> {

    public static final AssetBuilderCodec<String, Enemy> CODEC = AssetBuilderCodec.builder(Enemy.class, Enemy::new, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .append(new KeyedCodec<>("Entity", Codec.STRING), (w, e) -> w.entity = e, w -> w.entity)
        .addValidator(Validators.nonEmptyString())
        .metadata(new UIEditor(new UIEditor.Dropdown("GraveProtocolWaveEntities")))
        .add()
        .append(new KeyedCodec<>("AttackData", Attacker.CODEC), (w, a) -> w.attackData = a, w-> w.attackData)
        .add()
        .build();

    private static AssetStore<String, Enemy, AssetMap<String, Enemy>> ASSET_STORE;

    private String id;
    private AssetExtraInfo.Data data;

    private String entity;
    private Attacker attackData;

    public static AssetStore<String, Enemy, AssetMap<String, Enemy>> getAssetStore() {

        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Enemy.class);
        }

        return ASSET_STORE;
    }

    public static AssetMap<String, Enemy> getAssetMap() {

        return getAssetStore().getAssetMap();
    }
}