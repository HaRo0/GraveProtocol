package net.haro0.hytale.graveprotocol.events;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import net.haro0.hytale.graveprotocol.components.DeathDecisionComponent;

public class PlayerEvents {

    public static void registerEvents(EventRegistry registry) {

        registry.register(PlayerConnectEvent.class, PlayerEvents::onPlayerJoin);
    }

    private static void onPlayerJoin(PlayerConnectEvent event) {

        var decisionComponent = event.getHolder().getComponent(DeathDecisionComponent.getComponentType());
        if (decisionComponent == null) return;

        event.getHolder().removeComponent(DeathDecisionComponent.getComponentType());
        event.getHolder().putComponent(DeathComponent.getComponentType(), decisionComponent.getOriginal());
    }
}
