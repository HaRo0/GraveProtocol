package net.haro0.hytale.graveprotocol.codecs.data.tower;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import lombok.Getter;
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.codecs.data.Attacker;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
public class TowerUpgrade {
    public static final BuilderCodec<TowerUpgrade> CODEC = BuilderCodec.builder(TowerUpgrade.class, TowerUpgrade::new)
        .append(new KeyedCodec<>("Price", Codec.INTEGER), (c, v) -> c.price = v, c -> c.price)
        .addValidator(Validators.min(0))
        .add()
        .append(new KeyedCodec<>("Description", Codec.STRING), (c, v) -> c.description = v, c -> c.description)
        .add()
        .append(new KeyedCodec<>("AttackData", Attacker.CODEC), (w, a) -> w.attackData = a, w-> w.attackData)
        .add()
        .append(new KeyedCodec<>("AttackSpeed", Codec.FLOAT), (c, v) -> c.attackSpeed = v, c -> c.attackSpeed)
        .addValidator(Validators.min(0f))
        .add()
        .append(new KeyedCodec<>("AttackRange", Codec.FLOAT), (c, v) -> c.attackRange = v, c -> c.attackRange)
        .addValidator(Validators.min(0f))
        .add()
        .append(new KeyedCodec<>("ProjectileSpeed", Codec.FLOAT), (c, v) -> c.projectileSpeed = v, c -> c.projectileSpeed)
        .addValidator(Validators.min(0f))
        .add()
        .build();

    protected int price;
    protected String description;
    protected Attacker attackData;
    protected float attackSpeed;
    protected float attackRange;
    protected float projectileSpeed;

    @NullableDecl
    @Override
    @SneakyThrows
    public TowerUpgrade clone() {
        return (TowerUpgrade) super.clone();
    }
}
