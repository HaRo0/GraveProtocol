package net.haro0.hytale.graveprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LynnAttackerComponent implements Component<EntityStore> {

    public static final BuilderCodec<LynnAttackerComponent> CODEC = BuilderCodec.builder(LynnAttackerComponent.class, LynnAttackerComponent::new)
        .append(new KeyedCodec<>("WaveIndex", Codec.INTEGER), (c, v) -> c.waveIndex = v, c -> c.waveIndex).add()
        .append(new KeyedCodec<>("Health", Codec.INTEGER), (c, v) -> c.health = v, c -> c.health).add()
        .append(new KeyedCodec<>("AttackDamage", Codec.INTEGER), (c, v) -> c.attackDamage = v, c -> c.attackDamage).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, LynnAttackerComponent> componentType;

    private int waveIndex;

    private int health;

    private int attackDamage;

    public static void register(ComponentRegistryProxy<EntityStore> registry) {

        componentType = registry.registerComponent(LynnAttackerComponent.class, "LynnAttackerComponent", CODEC);
    }
    @NullableDecl
    @Override
    public LynnAttackerComponent clone() {
        try {
            return (LynnAttackerComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
