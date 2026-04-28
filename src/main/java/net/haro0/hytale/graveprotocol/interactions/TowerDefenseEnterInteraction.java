package net.haro0.hytale.graveprotocol.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.utils.InstanceUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TowerDefenseEnterInteraction  extends SimpleBlockInteraction {

    @Nonnull
    public static final BuilderCodec<TowerDefenseEnterInteraction> CODEC = BuilderCodec.builder(
            TowerDefenseEnterInteraction.class, TowerDefenseEnterInteraction::new, SimpleBlockInteraction.CODEC
        )
        .build();

    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void interactWithBlock(
        @Nonnull World world,
        @Nonnull CommandBuffer<EntityStore> commandBuffer,
        @Nonnull InteractionType type,
        @Nonnull InteractionContext context,
        @Nullable ItemStack itemInHand,
        @Nonnull Vector3i targetBlock,
        @Nonnull CooldownHandler cooldownHandler
    ) {
        world.execute(() ->InstanceUtils.moveToPrestigeInstance(context.getEntity(),commandBuffer));
    }

    @Override
    protected void simulateInteractWithBlock(
        @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock
    ) {
    }


}
