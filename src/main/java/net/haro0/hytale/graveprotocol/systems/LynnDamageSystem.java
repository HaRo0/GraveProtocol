package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.systems.NPCDamageSystems;
import lombok.NonNull;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class LynnDamageSystem extends DamageEventSystem {

    private static final Query<EntityStore> ATTACK_FILTER = LynnAttackerComponent.getComponentType();
    private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(
        new SystemDependency<>(Order.AFTER, NPCDamageSystems.FilterDamageSystem.class)
    );

    @Override
    @NonNull
    public Set<Dependency<EntityStore>> getDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> chunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        var target = chunk.getReferenceTo(index);
        if (!(damage.getSource() instanceof Damage.EntitySource entitySource)) {
            damage.setCancelled(true);
            return;
        }

        var source = entitySource.getRef();
        var sourceTarget = store.getArchetype(source);
        if(!ATTACK_FILTER.test(sourceTarget)) {
            damage.setCancelled(true);
            return;
        }

        var sourceComponent = store.getComponent(source, LynnAttackerComponent.getComponentType());
        var targetComponent = store.getComponent(target, LynnComponent.getComponentType());
        var attackerComponent = sourceComponent.getAttackerData();
        var defenseComponent = targetComponent.getDefender();
        var multiplierComponent = targetComponent.getMultipliers();

        if(attackerComponent == null || defenseComponent == null || multiplierComponent == null){
            damage.setCancelled(true);
            return;
        }

        var baseDamage = attackerComponent.calcDamage(defenseComponent);
        baseDamage *= multiplierComponent.getEnemyAttackMultiplier();


        damage.setAmount(baseDamage);

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
