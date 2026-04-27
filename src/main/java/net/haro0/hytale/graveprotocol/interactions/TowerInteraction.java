package net.haro0.hytale.graveprotocol.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.system.System;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;
import net.haro0.hytale.graveprotocol.ui.TowerPurchaseUi;
import net.haro0.hytale.graveprotocol.ui.TowerUpgradeUi;
import net.haro0.hytale.graveprotocol.utils.BlockUtils;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TowerInteraction extends SimpleBlockInteraction {

    public static final BuilderCodec<TowerInteraction> CODEC = BuilderCodec.builder(
        TowerInteraction.class, TowerInteraction::new, SimpleBlockInteraction.CODEC
    ).documentation("Opens the Grave menu for the interacting player.").build();

    public TowerInteraction() {

        super();
    }

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> buffer, @NonNullDecl InteractionType var3, @NonNullDecl InteractionContext context, @NullableDecl ItemStack var5, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler var7) {
        if (var3 != InteractionType.Use) {
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

        var playerWorld = player.getWorld();
        if (playerWorld == null) {
            return;
        }
        var tRef = BlockUtils.getBlockEntity(playerWorld,pos);

        var tower = tRef.getStore().ensureAndGetComponent(tRef,TowerComponent.getComponentType());

        playerWorld.execute(() -> {
            if (tower.hasTower()) {
                player.getPageManager().openCustomPage(ref, store, new TowerUpgradeUi(playerRef, tRef));
                return;
            }

            player.getPageManager().openCustomPage(ref, store, new TowerPurchaseUi(playerRef, pos));
        });

    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType var1, @NonNullDecl InteractionContext var2, @NullableDecl ItemStack var3, @NonNullDecl World var4, @NonNullDecl Vector3i var5) {

    }


}

