package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

public class TowerAttackSystem extends EntityTickingSystem<ChunkStore> {
    public static final int TICKS_PER_SECOND = 30;
    @Override
    public void tick(float dt, int index, @NonNullDecl ArchetypeChunk<ChunkStore> archetypeChunk, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        var tower = archetypeChunk.getComponent(index,TowerComponent.getComponentType());
        if (!tower.hasTower()) {
            return;
        }

        tower.waitTick();
        if(tower.getTicksToWait() > tower.getTicksWaited()){
            return;
        }

        var blockStateInfo = archetypeChunk.getComponent(index, BlockModule.BlockStateInfo.getComponentType());
        if (blockStateInfo == null) {
            return;
        }

        var chunkRef = blockStateInfo.getChunkRef();
        if (!chunkRef.isValid()) {
            return;
        }

        var chunk = store.getComponent(chunkRef, BlockChunk.getComponentType());
        if (chunk == null) {
            return;
        }

        int blockIndex = blockStateInfo.getIndex();
        int localX = ChunkUtil.xFromBlockInColumn(blockIndex);
        int localZ = ChunkUtil.zFromBlockInColumn(blockIndex);

        var towerPos = new Vector3i(
            ChunkUtil.worldCoordFromLocalCoord(chunk.getX(), localX),
            ChunkUtil.yFromBlockInColumn(blockIndex),
            ChunkUtil.worldCoordFromLocalCoord(chunk.getZ(), localZ)
        );
        var world = store.getExternalData().getWorld();

        var entities = new ConcurrentLinkedQueue<Ref<EntityStore>>();

        world.getEntityStore().getStore().forEachChunk(LynnAttackerComponent.getComponentType(), (eArchetypeChunk, eCommandBuffer) -> {
            var used = false;
            for(var i = 0; i < eArchetypeChunk.size(); i++) {
                var pos = eArchetypeChunk.getComponent(i, TransformComponent.getComponentType()).getPosition();
                if (pos.distanceTo(towerPos) <= tower.getAttackRange()) {
                    entities.add(eArchetypeChunk.getReferenceTo(i));
                    used = true;
                }
            }
            return used;
        });
        var entityList = entities.stream().toList();

        if(!tower.getAttackType().shouldAttack(entityList,world.getEntityStore().getStore(),world)){
            if(tower.getAttackType().clearTicksWithoutAttack()){
                tower.resetWaitedTicks();
            }
            return;
        }
        tower.resetWaitedTicks();
        tower.getAttackType().handleAttacking(tower,entityList, world.getEntityStore().getStore(), world);

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return TowerComponent.getComponentType();
    }
}
