package net.haro0.hytale.graveprotocol.npc;

import com.hypixel.hytale.server.core.modules.collision.CollisionConfig;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionControllerWalk;
import com.hypixel.hytale.server.npc.movement.controllers.builders.BuilderMotionControllerWalk;
import net.haro0.hytale.graveprotocol.codecs.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;
import net.haro0.hytale.graveprotocol.utils.PrestigeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

public class GPMotionControllerWalk extends MotionControllerWalk {

    private List<String> blocks = null;

    public GPMotionControllerWalk(@Nonnull BuilderMotionControllerWalk builder, @Nonnull BuilderSupport builderSupport) {
        super(builder, builderSupport);
    }

    @Override
    public void activate() {
        super.activate();
        applyModification();
    }

    private void applyModification() {

        Predicate<CollisionConfig> defaultNonWalkable = this.collisionResult.isNonWalkable;
        if (defaultNonWalkable == null) {
            this.collisionResult.setDefaultNonWalkablePredicate();
            defaultNonWalkable = this.collisionResult.isNonWalkable;
        }

        Predicate<CollisionConfig> basePredicate = defaultNonWalkable;
        this.collisionResult.setNonWalkablePredicate(config -> {

            if (basePredicate.test(config)) {
                return true;
            }

            return disableBlock(config);
        });
    }

    private boolean disableBlock(CollisionConfig config){
        if(config.blockType == null) return true;

        if(blocks != null) return !blocks.contains(config.blockType.getId());

        var world = this.entity.getWorld();
        var store = world.getEntityStore().getStore();
        var lynn = LevelStartService.findLynn(store);
        var lynnComponent = store.getComponent(lynn, LynnComponent.getComponentType());
        var player = world.getEntityRef(lynnComponent.getPlayerId());
        var dataComponent = store.getComponent(player, GPPlayerDataComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(dataComponent);


        blocks = Arrays.stream(prestige.getPathBlocks()).toList();
        return !blocks.contains(config.blockType.getId());
    }
}

