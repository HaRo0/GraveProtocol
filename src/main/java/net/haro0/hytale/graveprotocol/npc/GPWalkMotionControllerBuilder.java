package net.haro0.hytale.graveprotocol.npc;

import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.movement.controllers.builders.BuilderMotionControllerWalk;

import javax.annotation.Nonnull;

public class GPWalkMotionControllerBuilder extends BuilderMotionControllerWalk {

    @Nonnull
    @Override
    public GPMotionControllerWalk build(@Nonnull BuilderSupport builderSupport) {
        return new GPMotionControllerWalk(this, builderSupport);
    }

    @Nonnull
    @Override
    public String getShortDescription() {
        return "Walk controller with Prestige based walkability";
    }

    @Nonnull
    @Override
    public String getLongDescription() {
        return "Walk controller that changes allowed walkable blocks for Lynn Attackers";
    }

    @Nonnull
    @Override
    public Class<? extends MotionController> getClassType() {
        return GPMotionControllerWalk.class;
    }
}

