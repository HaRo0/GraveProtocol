package net.haro0.hytale.graveprotocol;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.assets.GPAssets;
import net.haro0.hytale.graveprotocol.components.GraveProtocolComponent;
import net.haro0.hytale.graveprotocol.components.LevelDataComponent;
import net.haro0.hytale.graveprotocol.events.PlayerEvents;
import net.haro0.hytale.graveprotocol.systems.DeathDecisionSystem;
import net.haro0.hytale.graveprotocol.systems.LevelSystem;

public class GraveProtocol extends JavaPlugin {

    public GraveProtocol(JavaPluginInit init) {

        super(init);
    }

    @Override
    @SneakyThrows
    protected void setup() {

        var eventRegistry = getEventRegistry();
        var entityRegistry = getEntityStoreRegistry();
        PlayerEvents.registerEvents(eventRegistry);
        GPAssets.registerAll(eventRegistry);
        GraveProtocolComponent.register(entityRegistry);
        LevelDataComponent.register(entityRegistry);
        entityRegistry.registerSystem(new DeathDecisionSystem());
        entityRegistry.registerSystem(new LevelSystem());

    }

}