package net.haro0.hytale.graveprotocol.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;
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

        var holder = world.getBlockComponentHolder(pos.x, pos.y, pos.z);

        var t = holder.ensureAndGetComponent(TowerComponent.getComponentType());
        System.out.println("AEGF");

    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType var1, @NonNullDecl InteractionContext var2, @NullableDecl ItemStack var3, @NonNullDecl World var4, @NonNullDecl Vector3i var5) {

    }


}

