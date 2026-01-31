package net.haro0.hytale.graveprotocol;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.components.DeathDecisionComponent;
import net.haro0.hytale.graveprotocol.events.PlayerEvents;
import net.haro0.hytale.graveprotocol.systems.DeathDecisionSystem;

public class GraveProtocol extends JavaPlugin {

    public GraveProtocol(JavaPluginInit init) {

        super(init);
    }

    @Override
    @SneakyThrows
    protected void setup() {

        PlayerEvents.registerEvents(getEventRegistry());
        getEntityStoreRegistry().registerSystem(new DeathDecisionSystem());
        DeathDecisionComponent.setComponentType(getEntityStoreRegistry().registerComponent(DeathDecisionComponent.class, "GraveProtocol:DeathDecisionComponent", DeathDecisionComponent.CODEC));
    }
}