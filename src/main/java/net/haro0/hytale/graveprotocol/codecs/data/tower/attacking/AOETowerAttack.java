package net.haro0.hytale.graveprotocol.codecs.data.tower.attacking;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;

import java.util.Collection;
import java.util.List;

public class AOETowerAttack extends AbstractTowerAttack {
    public static final BuilderCodec<AOETowerAttack> CODEC = BuilderCodec.builder(AOETowerAttack.class, AOETowerAttack::new, AbstractTowerAttack.BASE_CODEC)
        .append(new KeyedCodec<>("TestM", Codec.BOOLEAN), (t, v) -> t.testM = v, t -> t.testM)
        .add()
        .build();

    private boolean testM;

    @Override
    public boolean shouldAttack(Collection<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor, World world) {
        return true;
    }

    @Override
    public void handleAttacking(TowerComponent component, List<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor, World world) {
        damage(component,targets,accessor);
    }
}
