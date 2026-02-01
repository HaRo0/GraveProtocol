package net.haro0.hytale.graveprotocol.assets;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3d;
import lombok.Getter;

@Getter
public class Prestige implements JsonAssetWithMap<String, AssetMap<String, Prestige>> {

    public static final AssetBuilderCodec<String, Prestige> CODEC = AssetBuilderCodec.builder(Prestige.class, Prestige::new, Codec.STRING, (w, i) -> w.id = i, w -> w.id, (w, d) -> w.data = d, w -> w.data)
        .append(new KeyedCodec<>("Instance", Codec.STRING), (w, s) -> w.instance = s, w -> w.instance)
        .metadata(new UIEditor(new UIEditor.Dropdown("GraveProtocolInstances")))
        .addValidator(Validators.nonEmptyString())
        .add()
        .append(new KeyedCodec<>("OrderPosition", Codec.LONG), (w, l) -> w.order = l, w -> w.order)
        .addValidator(Validators.min(0L))
        .add()
        .append(new KeyedCodec<>("SpawnPositions", new ArrayCodec<>(Vector3d.CODEC, Vector3d[]::new)), (w, e) -> w.positions = e, w -> w.positions)
        .addValidator(Validators.nonEmptyArray())
        .add()
        .append(new KeyedCodec<>("ShopPosition", Vector3d.CODEC), (w, v) -> w.shopPosition = v, w -> w.shopPosition)
        .add()
        .append(new KeyedCodec<>("HealthMultiplier", Codec.FLOAT), (w, h) -> w.healthMultiplier = h, w -> w.healthMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("AttackMultiplier", Codec.FLOAT), (w, a) -> w.attackMultiplier = a, w -> w.attackMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("EnemyHealthMultiplier", Codec.FLOAT), (w, h) -> w.enemyHealthMultiplier = h, w -> w.enemyHealthMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("EnemyAttackMultiplier", Codec.FLOAT), (w, a) -> w.enemyAttackMultiplier = a, w -> w.enemyAttackMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .build();

    private static AssetStore<String, Prestige, AssetMap<String, Prestige>> ASSET_STORE;

    private String id;

    private AssetExtraInfo.Data data;

    private Vector3d[] positions;

    private String instance;

    private long order;

    private Vector3d shopPosition;

    private float healthMultiplier = 1.0f;

    private float attackMultiplier = 1.0f;

    private float enemyHealthMultiplier = 1.0f;

    private float enemyAttackMultiplier = 1.0f;

    public static AssetStore<String, Prestige, AssetMap<String, Prestige>> getAssetStore() {

        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(Prestige.class);
        }

        return ASSET_STORE;
    }

    public static AssetMap<String, Prestige> getAssetMap() {

        return getAssetStore().getAssetMap();
    }
}
