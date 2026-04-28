package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import net.haro0.hytale.graveprotocol.ui.TowerDefenseHudUi;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LynnDeathSystem extends DeathSystems.OnDeathSystem {

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl DeathComponent component, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        var lynnComponent = store.getComponent(ref, LynnComponent.getComponentType());

        if(lynnComponent == null) return;

        var playerId = lynnComponent.getPlayerId();
        var world = store.getExternalData().getWorld();
        var pRef = store.getExternalData().getRefFromUUID(playerId);
        var player = store.getComponent(pRef, Player.getComponentType());
        world.execute(()-> {
            TowerDefenseHudUi.closeFor(player);
            world.getPlayerRefs().forEach(p -> {
                p.sendMessage(Message.raw("You failed to Protect Lynn"));
                InstancesPlugin.exitInstance(p.getReference(), store);
            });
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {

        return LynnComponent.getComponentType();
    }
}
