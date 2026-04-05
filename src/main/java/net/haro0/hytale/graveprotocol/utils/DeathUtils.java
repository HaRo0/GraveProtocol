package net.haro0.hytale.graveprotocol.utils;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import net.haro0.hytale.graveprotocol.components.GPDeathComponent;
import net.haro0.hytale.graveprotocol.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.components.LynnComponent;

import java.util.concurrent.CompletableFuture;

public final class DeathUtils {
    private static final String ROLE_NAME = "Fox";
    private static final String MENU_INTERACTION_ID = "Open_Lynn_Menu";
    private static final String MENU_INTERACTION_HINT = "server.interactionHints.generic";

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
            deathComponent.setItems(player.getInventory().dropAllItemStacks().toArray(ItemStack[]::new));
            player.markNeedsSave();
            CompletableFuture<World> worldFuture = InstancesPlugin.get().spawnInstance(prestige.getInstance(), world, new Transform(transformComponent.getPosition().clone(), Vector3f.FORWARD));
            InstancesPlugin.teleportPlayerToLoadingInstance(ref, store, worldFuture, null);

            worldFuture.handle((w, t) -> {
                if (t != null) throw new RuntimeException(t);
                w.execute(() -> {
                    var entityStore = w.getEntityStore().getStore();

                        ensureMenuNpc(entityStore, prestige.getShopPosition());
                });
                return w;
            });
        });
    }

    private static void ensureMenuNpc(Store<EntityStore> store, Vector3d spawnNear) {

        var spawnPos = spawnNear.clone();
        var spawn = NPCPlugin.get().spawnNPC(store, ROLE_NAME, null, spawnPos, Vector3f.ZERO);
        if (spawn == null) {
            return;
        }

        var npcRef = spawn.first();
        var interactions = store.ensureAndGetComponent(npcRef, Interactions.getComponentType());
        interactions.setInteractionId(InteractionType.Use, MENU_INTERACTION_ID);
        interactions.setInteractionHint(MENU_INTERACTION_HINT);
        store.putComponent(npcRef, Interactions.getComponentType(), interactions);
        store.ensureComponent(npcRef, Interactable.getComponentType());
        store.ensureComponent(npcRef, Frozen.getComponentType());
        store.ensureComponent(npcRef, LynnComponent.getComponentType());
    }

}
