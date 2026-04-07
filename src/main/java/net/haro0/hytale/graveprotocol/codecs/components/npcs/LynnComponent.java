package net.haro0.hytale.graveprotocol.codecs.components.npcs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.codecs.data.Defender;
import net.haro0.hytale.graveprotocol.codecs.data.MultiplierCollection;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
@Setter
public class LynnComponent implements Component<EntityStore> {

    public static final BuilderCodec<LynnComponent> CODEC = BuilderCodec.builder(LynnComponent.class, LynnComponent::new)
        .append(new KeyedCodec<>("LevelIndex", Codec.INTEGER), (c, v) -> c.levelIndex = v, c -> c.levelIndex).add()
        .append(new KeyedCodec<>("PrestigeIndex", Codec.INTEGER), (c, v) -> c.prestigeIndex = v, c -> c.prestigeIndex).add()
        .append(new KeyedCodec<>("WaveIndex", Codec.INTEGER), (c, v) -> c.waveIndex = v, c -> c.waveIndex).add()
        .append(new KeyedCodec<>("DefenderComponent", Defender.CODEC), (c, v) -> c.defender = v, c-> c.defender).add()
        .append(new KeyedCodec<>("Multipliers", MultiplierCollection.CODEC), (w,v) -> w.multipliers = v, w -> w.multipliers).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, LynnComponent> componentType;

    private int levelIndex;

    private int prestigeIndex;

    private int waveIndex;

    private Defender defender;

    private MultiplierCollection multipliers;

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
