package net.haro0.hytale.graveprotocol.utils;

import net.haro0.hytale.graveprotocol.assets.Prestige;
import net.haro0.hytale.graveprotocol.components.GraveProtocolComponent;

import java.util.Comparator;

public class PrestigeUtils {

    private PrestigeUtils() {

    }

    public static Prestige[] getAllPrestiges() {

        return Prestige.getAssetMap().getAssetMap().values().stream().sorted(Comparator.comparingLong(Prestige::getOrder)).toArray(Prestige[]::new);
    }

    public static Prestige getPrestige(GraveProtocolComponent component) {

        var prestiges = getAllPrestiges();
        if (component.getPrestigeIndex() >= prestiges.length) {
            component.setPrestigeIndex(prestiges.length - 1);
        }
        return prestiges[component.getPrestigeIndex()];
    }
}
