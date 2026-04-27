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
public class TowerUpgrade implements Cloneable{
    public static final BuilderCodec<TowerUpgrade> CODEC = BuilderCodec.builder(TowerUpgrade.class, TowerUpgrade::new)
        .appendInherited(new KeyedCodec<>("Price", Codec.INTEGER), (c, v) -> c.price = v, c -> c.price, (a,b) -> a.price = b.price)
        .addValidator(Validators.min(0))
        .add()
        .appendInherited(new KeyedCodec<>("Description", Codec.STRING), (c, v) -> c.description = v, c -> c.description, (a,b) -> a.description = b.description)
        .add()
        .appendInherited(new KeyedCodec<>("AttackData", Attacker.CODEC), (w, a) -> w.attackData = a, w -> w.attackData, (a, b) -> a.attackData = b.attackData.clone())
        .add()
        .appendInherited(new KeyedCodec<>("AttackSpeed", Codec.FLOAT), (c, v) -> c.attackSpeed = v, c -> c.attackSpeed, (a, b) -> a.attackSpeed = b.attackSpeed)
        .addValidator(Validators.min(0f))
        .add()
        .appendInherited(new KeyedCodec<>("AttackRange", Codec.FLOAT), (c, v) -> c.attackRange = v, c -> c.attackRange, (a, b) -> a.attackRange = b.attackRange)
        .addValidator(Validators.min(0f))
        .add()
        .appendInherited(new KeyedCodec<>("ProjectileSpeed", Codec.FLOAT), (c, v) -> c.projectileSpeed = v, c -> c.projectileSpeed, (a, b) -> a.projectileSpeed = b.projectileSpeed)
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
