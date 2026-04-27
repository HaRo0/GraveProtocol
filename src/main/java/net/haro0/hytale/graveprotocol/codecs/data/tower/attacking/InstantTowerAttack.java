package net.haro0.hytale.graveprotocol.codecs.data.tower.attacking;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class InstantTowerAttack extends AbstractTowerAttack {
    private static Random RANDOM = new Random();
    public static final BuilderCodec<InstantTowerAttack> CODEC = BuilderCodec.builder(InstantTowerAttack.class, InstantTowerAttack::new, AbstractTowerAttack.BASE_CODEC)
        .build();

    @Override
    public boolean shouldAttack(Collection<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor, World world) {
        return !targets.isEmpty();
    }

    @Override
    public void handleAttacking(TowerComponent component, List<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor, World world) {
        var index = RANDOM.nextInt(targets.size());
        damage(component,targets.get(index),accessor);
    }
}
