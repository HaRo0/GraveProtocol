package net.haro0.hytale.graveprotocol.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
public class GraveProtocolComponent implements Component<EntityStore> {

    public static final BuilderCodec<GraveProtocolComponent> CODEC = BuilderCodec.builder(GraveProtocolComponent.class, GraveProtocolComponent::new)
        .append(new KeyedCodec<>("Original", DeathComponent.CODEC), (c, v) -> c.original = v, c -> c.original).add()
        .append(new KeyedCodec<>("Inventory", new ArrayCodec<>(ItemStack.CODEC, ItemStack[]::new)), (c, v) -> c.items = v, c -> c.items).add()
        .build();

    @Getter
    @Setter
    private static ComponentType<EntityStore, GraveProtocolComponent> componentType;

    private DeathComponent original;

    @Setter
    private ItemStack[] items;

    public GraveProtocolComponent() { }

    public GraveProtocolComponent(DeathComponent original) {

        this.original = original;
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public GraveProtocolComponent clone() {

        return (GraveProtocolComponent) super.clone();
    }
}
