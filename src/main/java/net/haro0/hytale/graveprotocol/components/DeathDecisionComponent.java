package net.haro0.hytale.graveprotocol.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@Getter
public class DeathDecisionComponent implements Component<EntityStore> {

    public static final BuilderCodec<DeathDecisionComponent> CODEC = BuilderCodec.builder(DeathDecisionComponent.class, DeathDecisionComponent::new)
        .append(new KeyedCodec<>("Original", DeathComponent.CODEC), (c, v) -> c.original = v, c -> c.original).add()
        .build();

    @Getter
    @Setter
    private static ComponentType<EntityStore, DeathDecisionComponent> componentType;

    private DeathComponent original;

    public DeathDecisionComponent() { }

    public DeathDecisionComponent(DeathComponent original) {

        this.original = original;
    }

    @NullableDecl
    @Override
    @SneakyThrows
    public DeathDecisionComponent clone() {

        return (DeathDecisionComponent) super.clone();
    }
}
