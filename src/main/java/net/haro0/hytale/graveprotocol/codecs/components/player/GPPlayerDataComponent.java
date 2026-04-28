package net.haro0.hytale.graveprotocol.codecs.components.player;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class GPPlayerDataComponent implements Component<EntityStore> {

    public static final BuilderCodec<GPPlayerDataComponent> CODEC = BuilderCodec.builder(GPPlayerDataComponent.class, GPPlayerDataComponent::new)
        .append(new KeyedCodec<>("LevelIndex", Codec.INTEGER), (c, v) -> c.levelIndex = v, c -> c.levelIndex).add()
        .append(new KeyedCodec<>("PrestigeIndex", Codec.INTEGER), (c, v) -> c.prestigeIndex = v, c -> c.prestigeIndex).add()
        .append(new KeyedCodec<>("Currencx", Codec.INTEGER), (c, v) -> c.currency = v, c -> c.currency).add()
        .append(new KeyedCodec<>("UnlockedTowers", new SetCodec<>(Codec.STRING, HashSet::new, false)), (c, v) -> c.unlockedTowers = v, c -> c.unlockedTowers).add()
        .build();

    @Getter
    private static ComponentType<EntityStore, GPPlayerDataComponent> componentType;

    private int levelIndex = 0;

    private int prestigeIndex = 0;

    private int currency = 0;

    private Set<String> unlockedTowers = new HashSet<>();

    public void addCurrency(int amount) {
        currency += amount;
    }

    public boolean spendCurrency(int amount) {
        if (currency < amount) return false;
        currency -= amount;
        return true;
    }

    public boolean isTowerUnlocked(String towerId) {
        return unlockedTowers.contains(towerId);
    }

    public void unlockTower(String towerId) {
        unlockedTowers.add(towerId);
    }

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
