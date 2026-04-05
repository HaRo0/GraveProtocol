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
public class LynnComponent implements Component<EntityStore> {

    public static final BuilderCodec<LynnComponent> CODEC = BuilderCodec.builder(LynnComponent.class, LynnComponent::new)
        .append(new KeyedCodec<>("LevelIndex", Codec.INTEGER), (c, v) -> c.levelIndex = v, c -> c.levelIndex).add()
        .append(new KeyedCodec<>("PrestigeIndex", Codec.INTEGER), (c, v) -> c.prestigeIndex = v, c -> c.prestigeIndex).add()
        .append(new KeyedCodec<>("WaitedTime", Codec.FLOAT), (c, v) -> c.waitedTime = v, c -> c.waitedTime).add()
        .append(new KeyedCodec<>("WaveIndex", Codec.INTEGER), (c, v) -> c.waveIndex = v, c -> c.waveIndex).add()
        .append(new KeyedCodec<>("IsWaveDead", Codec.BOOLEAN), (c, v) -> c.isWaveDead = v, c -> c.isWaveDead).add()
        .append(new KeyedCodec<>("Health", Codec.INTEGER), (c, v) -> c.health = v, c -> c.health).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, LynnComponent> componentType;

    private int levelIndex;

    private int prestigeIndex;

    private float waitedTime;

    private int waveIndex;

    private boolean isWaveDead;

    private int health;

    public void damage(LynnAttackerComponent attacker) {
        this.health -= attacker.getAttackDamage();
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
