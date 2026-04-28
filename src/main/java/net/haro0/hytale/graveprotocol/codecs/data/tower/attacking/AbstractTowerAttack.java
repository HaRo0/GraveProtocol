package net.haro0.hytale.graveprotocol.codecs.data.tower.attacking;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.ObjectCodecMapCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;

import java.util.Collection;
import java.util.List;

public abstract class AbstractTowerAttack {
    public static final ObjectCodecMapCodec<String, AbstractTowerAttack> CODEC = new ObjectCodecMapCodec<>("Type", Codec.STRING);
    public static final BuilderCodec<AbstractTowerAttack> BASE_CODEC = BuilderCodec.abstractBuilder(AbstractTowerAttack.class).build();

    public abstract boolean shouldAttack(Collection<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor, World world);

    public boolean clearTicksWithoutAttack(){
        return false;
    }

    public abstract void handleAttacking(Vector3i towerPos, TowerComponent component, List<Ref<EntityStore>> targets, Store<EntityStore> accessor, World world);

    protected void damage(TowerComponent tower, Ref<EntityStore> target, ComponentAccessor<EntityStore> accessor){
        damage(tower,target,accessor,1.0f);
    }
    protected void damage(TowerComponent tower, Ref<EntityStore> target, ComponentAccessor<EntityStore> accessor, float damageMultiplier){
        var attackerComponent = accessor.getComponent(target, LynnAttackerComponent.getComponentType());
        var damage = tower.getAttackData().calcDamage(attackerComponent.getAttackerData());
        target.getStore().getExternalData().getWorld().execute(() -> DamageSystems.executeDamage(target,accessor,new Damage(Damage.NULL_SOURCE, DamageCause.PHYSICAL,damage * damageMultiplier)));
    }

    protected void damage(TowerComponent tower, List<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor){
        damage(tower,targets,accessor,1.0f);
    }
    protected void damage(TowerComponent tower, List<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor,float damageMultiplier){
        for (var target:targets){
            damage(tower,target,accessor,damageMultiplier);
        }
    }

}
