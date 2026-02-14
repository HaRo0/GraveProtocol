package net.haro0.hytale.graveprotocol.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
@Setter
public class GraveProtocolComponent implements Component<EntityStore> {

    public static final BuilderCodec<GraveProtocolComponent> CODEC = BuilderCodec.builder(GraveProtocolComponent.class, GraveProtocolComponent::new)
        .append(new KeyedCodec<>("Original", DeathComponent.CODEC), (c, v) -> c.original = v, c -> c.original).add()
        .append(new KeyedCodec<>("Inventory", new ArrayCodec<>(ItemStack.CODEC, ItemStack[]::new)), (c, v) -> c.items = v, c -> c.items).add()
        .append(new KeyedCodec<>("PrestigeIndex", Codec.INTEGER), (c, v) -> c.prestigeIndex = v, c -> c.prestigeIndex).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, GraveProtocolComponent> componentType;

    private DeathComponent original;

    private ItemStack[] items;

    private int prestigeIndex = 0;

    public GraveProtocolComponent() { }

    public static void register(ComponentRegistryProxy<EntityStore> registry) {

        componentType = registry.registerComponent(GraveProtocolComponent.class, "GraveProtocolComponent", CODEC);
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public GraveProtocolComponent clone() {

        return (GraveProtocolComponent) super.clone();
    }
}
