package net.haro0.hytale.graveprotocol.events;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import net.haro0.hytale.graveprotocol.components.GraveProtocolComponent;

import java.util.Arrays;

public class PlayerEvents {

    public static void registerEvents(EventRegistry registry) {

        registry.registerGlobal(EventPriority.LATE, PlayerReadyEvent.class, PlayerEvents::onPlayerJoin);
    }

    private static void onPlayerJoin(PlayerReadyEvent event) {

        var player = event.getPlayer();
        if (event.getPlayer().getWorld().getWorldConfig().isDeleteOnRemove()) return;
        var protocolComponent = event.getPlayerRef().getStore().getComponent(event.getPlayerRef(), GraveProtocolComponent.getComponentType());

        if (protocolComponent == null) return;

        var items = protocolComponent.getItems();

        player.getInventory().clear();
        player.getInventory().getCombinedEverything().addItemStacks(Arrays.stream(items).toList());
        var original = protocolComponent.getOriginal();
        original.setShowDeathMenu(true);
        event.getPlayerRef().getStore().putComponent(event.getPlayerRef(), DeathComponent.getComponentType(), original);
    }
}
