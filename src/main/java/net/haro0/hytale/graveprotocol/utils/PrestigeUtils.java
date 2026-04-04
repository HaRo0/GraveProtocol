package net.haro0.hytale.graveprotocol.utils;

import net.haro0.hytale.graveprotocol.assets.Prestige;
import net.haro0.hytale.graveprotocol.components.GPDeathComponent;
import net.haro0.hytale.graveprotocol.components.GPPlayerDataComponent;

import java.util.Comparator;

public class PrestigeUtils {

    private PrestigeUtils() {

    }

    public static Prestige[] getAllPrestiges() {

        return Prestige.getAssetMap().getAssetMap().values().stream().sorted(Comparator.comparingLong(Prestige::getOrder)).toArray(Prestige[]::new);
    }

    public static Prestige getPrestige(GPPlayerDataComponent component) {

        var prestiges = getAllPrestiges();
        if (component.getPrestigeIndex() >= prestiges.length) {
            component.setPrestigeIndex(prestiges.length - 1);
        }

        if(component.getPrestigeIndex() < 0) {
            component.setPrestigeIndex(0);
        }

        return prestiges[component.getPrestigeIndex()];
    }
}
