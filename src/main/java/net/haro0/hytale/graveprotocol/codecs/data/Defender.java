package net.haro0.hytale.graveprotocol.codecs.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Defender implements Cloneable {

    public static final BuilderCodec<Defender> CODEC = BuilderCodec.builder(Defender.class, Defender::new)
        .appendInherited(new KeyedCodec<>("Health", Codec.FLOAT), (c, v) -> c.health = v, c -> c.health,(a,b)-> a.health = b.health)
        .add()
        .appendInherited(new KeyedCodec<>("DefenseTypeMultipliers", new EnumMapCodec<>(DamageType.class, Codec.FLOAT,true)), (c, m) -> c.defenseTypeMultipliers = m, c -> c.defenseTypeMultipliers, (a,b) -> a.defenseTypeMultipliers = new HashMap<>(b.defenseTypeMultipliers))
        .add()
        .appendInherited(new KeyedCodec<>("DefenseMultiplier",Codec.FLOAT),(c,d) -> c.defenseMultiplier = d, c -> c.defenseMultiplier,(a,b) -> a.defenseMultiplier = b.defenseMultiplier)
        .add()
        .appendInherited(new KeyedCodec<>("DefenseFixed", Codec.FLOAT), (c,d) -> c.defenseFixed = d, c -> c.defenseFixed, (a,b) -> a.defenseFixed = b.defenseFixed)
        .add()
        .build();

    @Setter
    protected float health;
    protected Map<DamageType,Float> defenseTypeMultipliers;
    protected float defenseMultiplier = 1;
    protected float defenseFixed = 0;

    @NullableDecl
    @Override
    @SneakyThrows
    public Defender clone() {
        return (Defender) super.clone();
    }
}
