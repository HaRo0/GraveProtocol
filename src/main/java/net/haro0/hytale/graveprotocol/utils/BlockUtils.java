package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class BlockUtils {
    private BlockUtils() { }

    public static Ref<ChunkStore> getBlockEntity(World world, Vector3i pos){
        return getBlockEntity(world, pos.getX(), pos.getY(), pos.getZ());
    }
    public static Ref<ChunkStore> getBlockEntity(World world, int x, int y, int z){
        var chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
        int blockIndexInColumn = ChunkUtil.indexBlockInColumn(x, y, z);

        BlockComponentChunk blockComp = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());
        if (blockComp == null) return null;

        return blockComp.getEntityReference(blockIndexInColumn);
    }
}
