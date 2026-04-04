package net.haro0.hytale.graveprotocol.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.ui.GraveMenuUi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LynnInteraction extends SimpleBlockInteraction {

    public static final BuilderCodec<LynnInteraction> CODEC = BuilderCodec.builder(
        LynnInteraction.class, LynnInteraction::new, SimpleBlockInteraction.CODEC
    ).documentation("Opens a simple 1-slot inventory for the player (block-aware)").build();

    private static final HytaleLogger LOG = HytaleLogger.forEnclosingClass();

    public LynnInteraction() {

        super();
    }

    @Override
    protected void interactWithBlock(
        @Nonnull World world,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull InteractionType type,
        @Nonnull InteractionContext context,
        @Nullable ItemStack itemInHand,
        @Nonnull Vector3i pos,
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
        world.execute(() -> player.getPageManager().openCustomPage(ref, store, new GraveMenuUi(playerRef)));
    }

    @Override
    protected void simulateInteractWithBlock(
        @Nonnull InteractionType type,
        @Nonnull InteractionContext context,
        @Nullable ItemStack itemInHand,
        @Nonnull World world,
        @Nonnull Vector3i targetBlock
    ) {
        // no-op simulation
    }
}

