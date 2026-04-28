package net.haro0.hytale.graveprotocol.codecs.data.tower.attacking;

import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@Getter
public class MagicTowerAttack extends AbstractTowerAttack {
    private static Random RANDOM = new Random();
    public static final BuilderCodec<MagicTowerAttack> CODEC = BuilderCodec.builder(MagicTowerAttack.class, MagicTowerAttack::new, AbstractTowerAttack.BASE_CODEC)
        .append(new KeyedCodec<>("Explode", Codec.BOOLEAN), (m,a) -> m.explode = a, m -> m.explode)
        .add()
        .append(new KeyedCodec<>("ExplosionRange", Codec.FLOAT), (m,a) -> m.explosionRange = a, m -> m.explosionRange)
        .add()
        .append(new KeyedCodec<>("Particle", new ContainedAssetCodec<>(ParticleSystem.class, ParticleSystem.CODEC)), (m, a) -> m.particle = a, m -> m.particle)
        .add()
        .append(new KeyedCodec<>("ParticleColor", ProtocolCodecs.COLOR), (particle, o) -> particle.color = o, particle -> particle.color)
        .documentation("The colour used if none was specified in the particle settings.")
        .add()
        .append(new KeyedCodec<>("ParticleScale", Codec.FLOAT), (particle, f) -> particle.scale = f, particle -> particle.scale)
        .documentation("The scale of the particle system.")
        .add()
        .append(new KeyedCodec<>("ParticleOffset", Vector3d.CODEC), (particle, o) -> particle.offset = o, particle -> particle.offset)
        .add()
        .build();

    private boolean explode = false;
    private float explosionRange = 1.0f;
    private String particle;
    private Color color;
    private float scale;
    private Vector3d offset = new Vector3d(0.5,-1,0.5);


    @Override
    public boolean shouldAttack(Collection<Ref<EntityStore>> targets, ComponentAccessor<EntityStore> accessor, World world) {
        return !targets.isEmpty();
    }

    @Override
    public void handleAttacking(Vector3i towerPos, TowerComponent component, List<Ref<EntityStore>> targets, Store<EntityStore> accessor, World world) {
        var target = targets.get(RANDOM.nextInt(targets.size()));
        damage(component,target,accessor);
        var mainPos = accessor.getComponent(target, TransformComponent.getComponentType()).getPosition();
        SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = accessor.getResource(EntityModule.get().getPlayerSpatialResourceType());
        List<Ref<EntityStore>> playerRefs = SpatialResource.getThreadLocalReferenceList();
        playerSpatialResource.getSpatialStructure().collect(mainPos, 75.0, playerRefs);
        ParticleUtil.spawnParticleEffect(particle,mainPos.add(offset),0,0,0,scale,color,playerRefs,accessor);
        if(!explode) return;
        world.execute(() ->{
            accessor.forEachEntityParallel(LynnAttackerComponent.getComponentType(),(index,archetype, commandBuffer) -> {
                var pos = archetype.getComponent(index, TransformComponent.getComponentType()).getPosition();
                var distance = pos.distanceTo(mainPos);
                if(distance > explosionRange) return;
                float multiplier =(float) (1f - distance/explosionRange * 0.5f);
                damage(component,target,accessor,multiplier);

            });
        });
    }
}
