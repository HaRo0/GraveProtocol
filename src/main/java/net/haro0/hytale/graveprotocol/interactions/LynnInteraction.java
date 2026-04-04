package net.haro0.hytale.graveprotocol.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import net.haro0.hytale.graveprotocol.ui.GraveMenuUi;

import javax.annotation.Nonnull;

public class LynnInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<LynnInteraction> CODEC = BuilderCodec.builder(
        LynnInteraction.class, LynnInteraction::new, SimpleInstantInteraction.CODEC
    ).documentation("Opens the Grave menu for the interacting player.").build();

    public LynnInteraction() {

        super();
    }

    @Override
    protected void firstRun(
        @Nonnull InteractionType type,
        @Nonnull InteractionContext context,
        @Nonnull CooldownHandler cooldownHandler
    ) {

        if (type != InteractionType.Use) {
            return;
        }

        var ref = context.getEntity();
        if (!ref.isValid()) {
            return;
        }

        var store = ref.getStore();
        var player = store.getComponent(ref, Player.getComponentType());
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            return;
        }

        var world = player.getWorld();
        if (world == null) {
            return;
        }

        world.execute(() -> player.getPageManager().openCustomPage(ref, store, new GraveMenuUi(playerRef)));
    }
}

