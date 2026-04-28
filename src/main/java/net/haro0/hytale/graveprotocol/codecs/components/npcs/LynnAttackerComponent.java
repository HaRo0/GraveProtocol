package net.haro0.hytale.graveprotocol.codecs.components.npcs;

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
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.codecs.data.Attacker;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LynnAttackerComponent implements Component<EntityStore> {

    public static final BuilderCodec<LynnAttackerComponent> CODEC = BuilderCodec.builder(LynnAttackerComponent.class, LynnAttackerComponent::new)
        .append(new KeyedCodec<>("AttackerData",Attacker.CODEC), (c,a) -> c.attackerData = a, c -> c.attackerData).add()
        .append(new KeyedCodec<>("LynnId", Codec.UUID_STRING), (c, v) -> c.lynnId = v, c -> c.lynnId).add()
        .append(new KeyedCodec<>("MaterialReward", Codec.INTEGER), (c, v) -> c.materialReward = v, c -> c.materialReward).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, LynnAttackerComponent> componentType;

    private Attacker attackerData;

    private UUID lynnId;
    protected int materialReward = 1;


    public static void register(ComponentRegistryProxy<EntityStore> registry) {

        componentType = registry.registerComponent(LynnAttackerComponent.class, "GPLynnAttackerComponent", CODEC);
    }
    @NullableDecl
    @Override
    @SneakyThrows
    public LynnAttackerComponent clone() {
        return (LynnAttackerComponent) super.clone();
    }
}
