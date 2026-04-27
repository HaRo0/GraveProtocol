package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.assets.Level;
import net.haro0.hytale.graveprotocol.codecs.assets.Prestige;
import net.haro0.hytale.graveprotocol.codecs.components.player.GPPlayerDataComponent;

import java.util.Comparator;

public class LevelUtils {

    private LevelUtils() { }

    public static Level[] getPrestigeLevels(Prestige prestige) {

        return Level.getAssetMap().getAssetMap().values().stream()
            .filter(l -> l.getMinPrestige() <= prestige.getOrder() && (l.getMaxPrestige() == -1 || l.getMaxPrestige() >= prestige.getOrder()))
            .sorted(Comparator.comparingInt(Level::getOrder)).toArray(Level[]::new);
    }

    public static Level getPlayerLevel(Ref<EntityStore> ref) {
        var store = ref.getStore();

        return getPlayerLevel(ref, store);
    }

    public static Level getPlayerLevel(Ref<EntityStore> ref, ComponentAccessor<EntityStore> store) {
        if(store.getComponent(ref, Player.getComponentType()) == null) return null;
        var data = store.ensureAndGetComponent(ref, GPPlayerDataComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(data);
        var levels = getPrestigeLevels(prestige);
        if(levels.length <= data.getLevelIndex()){
            data.setLevelIndex(0);
            data.setPrestigeIndex(data.getPrestigeIndex()+1);
            prestige = PrestigeUtils.getPrestige(data);
            levels = getPrestigeLevels(prestige);
        }
        return levels[data.getLevelIndex()];
    }
}
