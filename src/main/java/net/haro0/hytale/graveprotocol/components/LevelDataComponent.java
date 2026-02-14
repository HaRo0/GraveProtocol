package net.haro0.hytale.graveprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
public class LevelDataComponent implements Component<EntityStore> {

    public static final BuilderCodec<LevelDataComponent> CODEC = BuilderCodec.builder(LevelDataComponent.class, LevelDataComponent::new)
        .append(new KeyedCodec<>("LevelIndex", Codec.INTEGER), (c, v) -> c.levelIndex = v, c -> c.levelIndex).add()
        .append(new KeyedCodec<>("PrestigeIndex", Codec.INTEGER), (c, v) -> c.prestigeIndex = v, c -> c.prestigeIndex).add()
        .append(new KeyedCodec<>("WaitedTime", Codec.FLOAT), (c, v) -> c.waitedTime = v, c -> c.waitedTime).add()
        .append(new KeyedCodec<>("WaveIndex", Codec.INTEGER), (c, v) -> c.waveIndex = v, c -> c.waveIndex).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, LevelDataComponent> componentType;

    private int levelIndex;

    private int prestigeIndex;

    private float waitedTime;

    private int waveIndex;

    public static void register(ComponentRegistryProxy<EntityStore> registry) {

        componentType = registry.registerComponent(LevelDataComponent.class, "LevelDataComponent", CODEC);
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public LevelDataComponent clone() {

        return (LevelDataComponent) super.clone();
    }
}
