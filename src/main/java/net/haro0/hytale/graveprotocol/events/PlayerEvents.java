package net.haro0.hytale.graveprotocol.events;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import net.haro0.hytale.graveprotocol.codecs.components.GPDeathComponent;

import java.util.Arrays;

public class PlayerEvents {

    public static void registerEvents(EventRegistry registry) {

        registry.registerGlobal(EventPriority.LATE, PlayerReadyEvent.class, PlayerEvents::onPlayerJoin);
    }

    private static void onPlayerJoin(PlayerReadyEvent event) {

        var player = event.getPlayer();
        var world = player.getWorld();
        if (world == null || world.getWorldConfig().isDeleteOnRemove()) return;
        var ref = event.getPlayerRef();
        var store = ref.getStore();

        world.execute(() -> {

            var protocolComponent = store.getComponent(ref, GPDeathComponent.getComponentType());

            if(protocolComponent == null) return;

            var items = protocolComponent.getItems();

            if (items == null) return;

            player.getInventory().clear();

            player.getInventory().getCombinedHotbarUtilityConsumableStorage().addItemStacks(Arrays.stream(items).toList());
            protocolComponent.setItems(null);
            if (protocolComponent.getOriginal() == null) return;

            var original = protocolComponent.getOriginal();
            original.setShowDeathMenu(true);
            store.putComponent(ref, DeathComponent.getComponentType(), original);
        });
    }
}
