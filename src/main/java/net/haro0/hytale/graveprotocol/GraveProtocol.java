package net.haro0.hytale.graveprotocol;

import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.codecs.assets.GPAssets;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;
import net.haro0.hytale.graveprotocol.codecs.data.tower.attacking.AOETowerAttack;
import net.haro0.hytale.graveprotocol.codecs.data.tower.attacking.AbstractTowerAttack;
import net.haro0.hytale.graveprotocol.codecs.data.tower.attacking.InstantTowerAttack;
import net.haro0.hytale.graveprotocol.codecs.data.tower.attacking.ProjectileTowerAttack;
import net.haro0.hytale.graveprotocol.commands.GPCommand;
import net.haro0.hytale.graveprotocol.codecs.components.player.GPInstanceComponent;
import net.haro0.hytale.graveprotocol.codecs.components.player.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import net.haro0.hytale.graveprotocol.events.PlayerEvents;
import net.haro0.hytale.graveprotocol.interactions.LynnInteraction;
import net.haro0.hytale.graveprotocol.interactions.TowerInteraction;
import net.haro0.hytale.graveprotocol.npc.GPWalkMotionControllerBuilder;
import net.haro0.hytale.graveprotocol.systems.LynnAttackerDeathSystem;
import net.haro0.hytale.graveprotocol.systems.LynnDamageSystem;
import net.haro0.hytale.graveprotocol.systems.LynnDeathSystem;
import com.hypixel.hytale.server.npc.NPCPlugin;
import net.haro0.hytale.graveprotocol.systems.TowerAttackSystem;

public class GraveProtocol extends JavaPlugin {

    public GraveProtocol(JavaPluginInit init) {

        super(init);
    }

    @Override
    @SneakyThrows
    protected void setup() {

        var eventRegistry = getEventRegistry();
        var entityRegistry = getEntityStoreRegistry();
        var chunkRegistry = getChunkStoreRegistry();
        var cmdRegistry = getCommandRegistry();
        PlayerEvents.registerEvents(eventRegistry);
        new GPAssets(eventRegistry).registerAll();

        NPCPlugin.get().getBuilderManager().<MotionController>getFactory(MotionController.class).add("GPWalk", GPWalkMotionControllerBuilder::new);

        GPInstanceComponent.register(entityRegistry);
        LynnComponent.register(entityRegistry);
        LynnAttackerComponent.register(entityRegistry);
        GPPlayerDataComponent.register(entityRegistry);
        TowerComponent.register(chunkRegistry);

        Interaction.CODEC.register("OpenLynnMenu", LynnInteraction.class, LynnInteraction.CODEC);
        Interaction.CODEC.register("OpenTowerMenu", TowerInteraction.class, TowerInteraction.CODEC);

        AbstractTowerAttack.CODEC.register("ProjectileAttack", ProjectileTowerAttack.class, ProjectileTowerAttack.CODEC);
        AbstractTowerAttack.CODEC.register("AOEAttack", AOETowerAttack.class, AOETowerAttack.CODEC);
        AbstractTowerAttack.CODEC.register("InstantDamageAttack", InstantTowerAttack.class, InstantTowerAttack.CODEC);


        entityRegistry.registerSystem(new LynnAttackerDeathSystem());
        entityRegistry.registerSystem(new LynnDeathSystem());
        entityRegistry.registerSystem(new LynnDamageSystem());
        chunkRegistry.registerSystem(new TowerAttackSystem());
        cmdRegistry.registerCommand(new GPCommand());

    }

}