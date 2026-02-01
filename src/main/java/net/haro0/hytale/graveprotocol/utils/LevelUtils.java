package net.haro0.hytale.graveprotocol.utils;

import net.haro0.hytale.graveprotocol.assets.Level;
import net.haro0.hytale.graveprotocol.assets.Prestige;

import java.util.Comparator;

public class LevelUtils {

    private LevelUtils() { }

    public static Level[] getPrestigeLevels(Prestige prestige) {

        return Level.getAssetMap().getAssetMap().values().stream()
            .filter(l -> l.getMinPrestige() >= prestige.getOrder() && (l.getMaxPrestige() == -1 || l.getMaxPrestige() <= prestige.getOrder()))
            .sorted(Comparator.comparingInt(Level::getOrder)).toArray(Level[]::new);
    }
}
