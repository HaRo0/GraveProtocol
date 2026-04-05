package net.haro0.hytale.graveprotocol;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.assets.GPAssets;
import net.haro0.hytale.graveprotocol.commands.GPCommand;
import net.haro0.hytale.graveprotocol.components.GPDeathComponent;
import net.haro0.hytale.graveprotocol.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.components.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.components.LynnComponent;
import net.haro0.hytale.graveprotocol.events.PlayerEvents;
import net.haro0.hytale.graveprotocol.interactions.LynnInteraction;
import net.haro0.hytale.graveprotocol.systems.DeathDecisionSystem;
import net.haro0.hytale.graveprotocol.systems.LevelSystem;
import net.haro0.hytale.graveprotocol.systems.LynnDamageSystem;

public class GraveProtocol extends JavaPlugin {

    public GraveProtocol(JavaPluginInit init) {

        super(init);
    }

    @Override
    @SneakyThrows
    protected void setup() {

        var eventRegistry = getEventRegistry();
        var entityRegistry = getEntityStoreRegistry();
        var cmdRegistry = getCommandRegistry();
        PlayerEvents.registerEvents(eventRegistry);
        new GPAssets(eventRegistry).registerAll();
        GPDeathComponent.register(entityRegistry);
        LynnComponent.register(entityRegistry);
        LynnAttackerComponent.register(entityRegistry);
        GPPlayerDataComponent.register(entityRegistry);

        Interaction.CODEC.register("OpenLynnMenu", LynnInteraction.class, LynnInteraction.CODEC);


        entityRegistry.registerSystem(new DeathDecisionSystem());
        entityRegistry.registerSystem(new LevelSystem());
        entityRegistry.registerSystem(new LynnDamageSystem());
        cmdRegistry.registerCommand(new GPCommand());

    }

}