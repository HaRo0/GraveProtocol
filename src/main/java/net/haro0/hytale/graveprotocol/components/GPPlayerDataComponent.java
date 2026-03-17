package net.haro0.hytale.graveprotocol.components;

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
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
@Setter
public class GPPlayerDataComponent implements Component<EntityStore> {

    public static final BuilderCodec<GPPlayerDataComponent> CODEC = BuilderCodec.builder(GPPlayerDataComponent.class, GPPlayerDataComponent::new)
        .append(new KeyedCodec<>("LevelIndex", Codec.INTEGER), (c, v) -> c.levelIndex = v, c -> c.levelIndex).add()
        .append(new KeyedCodec<>("PrestigeIndex", Codec.INTEGER), (c, v) -> c.prestigeIndex = v, c -> c.prestigeIndex).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, GPPlayerDataComponent> componentType;

    private int levelIndex = 0;

    private int prestigeIndex = 0;

    public static void register(ComponentRegistryProxy<EntityStore> registry) {

        componentType = registry.registerComponent(GPPlayerDataComponent.class, "GPPlayerDataComponent", CODEC);
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public GPPlayerDataComponent clone() {

        return (GPPlayerDataComponent) super.clone();
    }
}
