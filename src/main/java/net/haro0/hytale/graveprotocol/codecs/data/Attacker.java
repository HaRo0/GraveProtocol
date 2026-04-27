package net.haro0.hytale.graveprotocol.codecs.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Attacker extends Defender {

    public static final BuilderCodec<Attacker> CODEC = BuilderCodec.builder(Attacker.class, Attacker::new, Defender.CODEC)
        .append(new KeyedCodec<>("AttackTypes",new SetCodec<>(new EnumCodec<>(DamageType.class), HashSet::new,true)), (c,a) -> c.attackTypes = a, c -> c.attackTypes)
        .add()
        .append(new KeyedCodec<>("AttackDamage", Codec.FLOAT), (c,d) -> c.attackDamage = d, c -> c.attackDamage)
        .add()
        .build();

    protected Set<DamageType> attackTypes = new HashSet<>();
    protected float attackDamage = 0;


    public float calcDamage(Defender defender){

        var damage = attackDamage * defender.defenseMultiplier;

        for(var type : attackTypes){
            damage *= defender.defenseTypeMultipliers.getOrDefault(type,1f);
        }

        damage -= defender.defenseFixed;

        return Math.max(0f,damage);
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public Attacker clone() {
        return (Attacker) super.clone();
    }
}
