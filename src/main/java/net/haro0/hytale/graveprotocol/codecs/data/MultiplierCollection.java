package net.haro0.hytale.graveprotocol.codecs.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MultiplierCollection {
    public static final BuilderCodec<MultiplierCollection> CODEC = BuilderCodec.builder(MultiplierCollection.class,MultiplierCollection::new)
        .append(new KeyedCodec<>("ShopHealthMultiplier", Codec.FLOAT), (w, h) -> w.shopHealthMultiplier = h, w -> w.shopHealthMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("TowerHealthMultiplier", Codec.FLOAT), (w, h) -> w.healthMultiplier = h, w -> w.healthMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("TowerAttackMultiplier", Codec.FLOAT), (w, a) -> w.attackMultiplier = a, w -> w.attackMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("EnemyHealthMultiplier", Codec.FLOAT), (w, h) -> w.enemyHealthMultiplier = h, w -> w.enemyHealthMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .append(new KeyedCodec<>("EnemyAttackMultiplier", Codec.FLOAT), (w, a) -> w.enemyAttackMultiplier = a, w -> w.enemyAttackMultiplier)
        .addValidator(Validators.min(0.1f))
        .add()
        .build();


    private float shopHealthMultiplier = 1.0f;
    private float healthMultiplier = 1.0f;

    private float attackMultiplier = 1.0f;

    private float enemyHealthMultiplier = 1.0f;

    private float enemyAttackMultiplier = 1.0f;
}
