package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LevelSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float var1, int var2, @NonNullDecl ArchetypeChunk<EntityStore> var3, @NonNullDecl Store<EntityStore> var4, @NonNullDecl CommandBuffer<EntityStore> var5) {

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {

        return LynnComponent.getComponentType();
    }
}
