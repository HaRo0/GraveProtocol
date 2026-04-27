package net.haro0.hytale.graveprotocol.codecs.components.player;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
@Setter
public class GPInstanceComponent implements Component<EntityStore> {

    public static final BuilderCodec<GPInstanceComponent> CODEC = BuilderCodec.builder(GPInstanceComponent.class, GPInstanceComponent::new)
        .append(new KeyedCodec<>("Inventory", new ArrayCodec<>(ItemStack.CODEC, ItemStack[]::new)), (c, v) -> c.items = v, c -> c.items).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, GPInstanceComponent> componentType;

    private ItemStack[] items;

    public GPInstanceComponent() { }

    public static void register(ComponentRegistryProxy<EntityStore> registry) {

        componentType = registry.registerComponent(GPInstanceComponent.class, "GPInstanceComponent", CODEC);
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public GPInstanceComponent clone() {

        return (GPInstanceComponent) super.clone();
    }
}
