package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import net.haro0.hytale.graveprotocol.components.GraveProtocolComponent;

import java.util.concurrent.CompletableFuture;

public final class DeathUtils {

    private DeathUtils() { }

    public static void moveToPrestigeInstance(Ref<EntityStore> ref, Store<EntityStore> store) {

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        var component = store.ensureAndGetComponent(ref, GraveProtocolComponent.getComponentType());

        var transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        assert transformComponent != null;
        var world = player.getWorld();
        assert world != null;
        var prestige = PrestigeUtils.getPrestige(component);
        world.execute(() -> {
            component.setItems(player.getInventory().getCombinedEverything().removeAllItemStacks().toArray(ItemStack[]::new));
            player.markNeedsSave();
            CompletableFuture<World> worldFuture = InstancesPlugin.get().spawnInstance(prestige.getInstance(), world, new Transform(transformComponent.getPosition().clone(), Vector3f.FORWARD));
            InstancesPlugin.teleportPlayerToLoadingInstance(ref, store, worldFuture, null);

            worldFuture.handle((w, t) -> {
                if (t != null) throw new RuntimeException(t);
                NPCPlugin.get().spawnNPC(w.getEntityStore().getStore(), "Fox", null, transformComponent.getPosition(), Vector3f.FORWARD);
                return w;
            });
        });
    }
}
