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
        if (player.getWorld().getWorldConfig().isDeleteOnRemove()) return;
        var ref = event.getPlayerRef();
        var store = ref.getStore();
        var protocolComponent = store.ensureAndGetComponent(ref, GraveProtocolComponent.getComponentType());

        var items = protocolComponent.getItems();

        if (items == null) return;

        player.getInventory().clear();
        player.getInventory().getCombinedEverything().addItemStacks(Arrays.stream(items).toList());
        protocolComponent.setItems(null);
        if (protocolComponent.getOriginal() == null) return;

        var original = protocolComponent.getOriginal();
        original.setShowDeathMenu(true);
        store.putComponent(ref, DeathComponent.getComponentType(), original);
    }
}
