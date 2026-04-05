package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.components.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.components.LynnComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LynnDamageSystem extends DamageEventSystem {

    private static final Query<EntityStore> ATTACK_FILTER = LynnAttackerComponent.getComponentType();

    @Override
    public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> chunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        damage.setCancelled(true);
        var target = chunk.getReferenceTo(index);
        if (!(damage.getSource() instanceof Damage.EntitySource entitySource)) return;

        var source = entitySource.getRef();
        var sourceTarget = store.getArchetype(source);
        if(!ATTACK_FILTER.test(sourceTarget)) return;

        var attackerComponent = store.getComponent(source, LynnAttackerComponent.getComponentType());
        if(attackerComponent == null) return;

        var targetComponent = store.getComponent(target, LynnComponent.getComponentType());
        if(targetComponent == null) return;

        targetComponent.damage(attackerComponent);
        System.out.println("-------------------------------------------------------");

        System.out.println("Lynn damage system:");
        System.out.println("Target: " + target);
        System.out.println("Attacker: " + source);
        System.out.println("Damage: " + damage.getAmount());
        System.out.println("-------------------------------------------------------");
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return LynnComponent.getComponentType();
    }
}
