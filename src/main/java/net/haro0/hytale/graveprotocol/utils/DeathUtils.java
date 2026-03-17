package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import net.haro0.hytale.graveprotocol.components.GPDeathComponent;
import net.haro0.hytale.graveprotocol.components.GPPlayerDataComponent;

import java.util.concurrent.CompletableFuture;

public final class DeathUtils {

    private DeathUtils() { }

    public static void moveToPrestigeInstance(Ref<EntityStore> ref, Store<EntityStore> store) {

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        var deathComponent = store.ensureAndGetComponent(ref, GPDeathComponent.getComponentType());

        var transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        assert transformComponent != null;
        var world = player.getWorld();
        assert world != null;
        var dataComponent = store.ensureAndGetComponent(ref, GPPlayerDataComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(dataComponent);
        world.execute(() -> {
            deathComponent.setItems(player.getInventory().getCombinedEverything().removeAllItemStacks().toArray(ItemStack[]::new));
            player.markNeedsSave();
            CompletableFuture<World> worldFuture = InstancesPlugin.get().spawnInstance(prestige.getInstance(), world, new Transform(transformComponent.getPosition().clone(), Vector3f.FORWARD));
            InstancesPlugin.teleportPlayerToLoadingInstance(ref, store, worldFuture, null);

            worldFuture.handle((w, t) -> {
                if (t != null) throw new RuntimeException(t);
                w.execute(() -> {
                    var entityStore = w.getEntityStore().getStore();

                    var name = "Fox";

                    var index = NPCPlugin.get().getBuilderManager().getIndex(name);

                    //var shop = NPCPlugin.get().spawnNPC(w.getEntityStore().getStore(), "Fox", null, transformComponent.getPosition(), Vector3f.ZERO);

                    Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
                    holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(transformComponent.getPosition(), Vector3f.ZERO));
                    holder.addComponent(HeadRotation.getComponentType(), new HeadRotation(Vector3f.ZERO));
                    DisplayNameComponent displayNameComponent = new DisplayNameComponent(Message.raw(name));
                    holder.addComponent(DisplayNameComponent.getComponentType(), displayNameComponent);
                    holder.ensureComponent(UUIDComponent.getComponentType());

                    Ref<EntityStore> npcRef = entityStore.addEntity(holder, AddReason.SPAWN);

                    entityStore.ensureComponent(npcRef, Invulnerable.getComponentType());
                });
                return w;
            });
        });
    }
}
