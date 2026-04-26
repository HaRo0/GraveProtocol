package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LevelSystem extends EntityTickingSystem<EntityStore> {

    private static final String PATH_TARGET_SLOT = "LockedTarget";
    private static final String PATH_TARGET_STATE = "Alerted";

    @Override
    public void tick(float dt, int index, @NonNullDecl ArchetypeChunk<EntityStore> chunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> buffer) {
        var ref = chunk.getReferenceTo(index);

        var attackerComp = chunk.getComponent(index, LynnAttackerComponent.getComponentType());
        if (attackerComp == null) return;

        var lynnId = attackerComp.getLynnId();
        if (lynnId == null) return;

        Ref<EntityStore> lynnRef = store.getExternalData().getRefFromUUID(lynnId);
        if (lynnRef == null || !lynnRef.isValid()) return;

        var npc = store.getComponent(ref, NPCEntity.getComponentType());
        if (npc == null || npc.getRole() == null) return;

        var role = npc.getRole();
        var markedEntitySupport = role.getMarkedEntitySupport();

        var currentTarget = markedEntitySupport.getMarkedEntityRef(PATH_TARGET_SLOT);
        if (currentTarget != null && currentTarget.isValid()) return;

        markedEntitySupport.setMarkedEntity(PATH_TARGET_SLOT, lynnRef);
        role.getStateSupport().setState(ref, PATH_TARGET_STATE, null, store);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return LynnAttackerComponent.getComponentType();
    }
}
