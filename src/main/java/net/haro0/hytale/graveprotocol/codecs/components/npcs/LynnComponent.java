package net.haro0.hytale.graveprotocol.codecs.components.npcs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.*;
import net.haro0.hytale.graveprotocol.codecs.data.Defender;
import net.haro0.hytale.graveprotocol.codecs.data.MultiplierCollection;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force=true)
public class LynnComponent implements Component<EntityStore> {

    public static final BuilderCodec<LynnComponent> CODEC = BuilderCodec.builder(LynnComponent.class, LynnComponent::new)
        .append(new KeyedCodec<>("PlayerId", Codec.UUID_STRING), (c, v) -> c.playerId = v, c -> c.playerId).add()
        .append(new KeyedCodec<>("WaveIndex", Codec.INTEGER), (c, v) -> c.waveIndex = v, c -> c.waveIndex).add()
        .append(new KeyedCodec<>("DefenderComponent", Defender.CODEC), (c, v) -> c.defender = v, c-> c.defender).add()
        .append(new KeyedCodec<>("Multipliers", MultiplierCollection.CODEC), (w,v) -> w.multipliers = v, w -> w.multipliers).add()
        .append(new KeyedCodec<>("AttackersLeft", Codec.INTEGER), (c, v) -> c.attackersLeft = new AtomicInteger(v), c -> c.attackersLeft.get()).add()
        .append(new KeyedCodec<>("IsActive", Codec.BOOLEAN), (c, v) -> c.isActive = v, c -> c.isActive).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, LynnComponent> componentType;

    @NonNull
    private UUID playerId;

    private int waveIndex;

    @Setter(AccessLevel.NONE)
    private AtomicInteger attackersLeft = new AtomicInteger();

    private boolean isActive;

    private Defender defender;

    @Setter(AccessLevel.NONE)
    private MultiplierCollection multipliers;

    public void setAttackersLeft(int amount){
        attackersLeft = new AtomicInteger(amount);
    }

    public void setMultipliers(MultiplierCollection prestigeMultipliers, MultiplierCollection levelMultipliers){
        multipliers = new MultiplierCollection(
            prestigeMultipliers.getShopHealthMultiplier() * levelMultipliers.getShopHealthMultiplier(),
            prestigeMultipliers.getTowerHealthMultiplier() * levelMultipliers.getTowerHealthMultiplier(),
            prestigeMultipliers.getTowerAttackMultiplier() * levelMultipliers.getTowerAttackMultiplier(),
            prestigeMultipliers.getEnemyHealthMultiplier() * levelMultipliers.getEnemyHealthMultiplier(),
            prestigeMultipliers.getEnemyAttackMultiplier() * levelMultipliers.getEnemyAttackMultiplier()
        );
    }

    public boolean markAttackerKilled(){
        return attackersLeft.decrementAndGet() <=0;
    }

    public static void register(ComponentRegistryProxy<EntityStore> registry) {

        componentType = registry.registerComponent(LynnComponent.class, "LynnComponent", CODEC);
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public LynnComponent clone() {

        return (LynnComponent) super.clone();
    }
}
