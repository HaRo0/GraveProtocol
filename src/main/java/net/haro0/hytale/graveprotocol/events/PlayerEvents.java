package net.haro0.hytale.graveprotocol.events;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import net.haro0.hytale.graveprotocol.components.GPDeathComponent;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerEvents {

    private static final String[] MENU_NPC_ROLE_CANDIDATES = {"Lynn", "Test_Lynn", "Fox", "Test_Fox"};
    private static final String MENU_INTERACTION_ID = "Open_Lynn_Menu";
    private static final String MENU_INTERACTION_HINT = "server.interactionHints.generic";
    private static final Map<String, com.hypixel.hytale.component.Ref<EntityStore>> MENU_NPC_BY_WORLD = new ConcurrentHashMap<>();

    public static void registerEvents(EventRegistry registry) {

        registry.registerGlobal(EventPriority.LATE, PlayerReadyEvent.class, PlayerEvents::onPlayerJoin);
    }

    private static void onPlayerJoin(PlayerReadyEvent event) {

        var player = event.getPlayer();
        var world = player.getWorld();
        if (world == null) return;
        var ref = event.getPlayerRef();
        var store = ref.getStore();
        if(world.getWorldConfig().isDeleteOnRemove()) {
            world.execute(() -> {
                    var transform = store.getComponent(ref, TransformComponent.getComponentType());
                    if (transform != null) {
                        ensureMenuNpc(world, store, transform.getPosition().add(0,1,0));
                    }
            });
            return;
        }

        world.execute(() -> {
            var transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform != null) {
                ensureMenuNpc(world, store, transform.getPosition());
            }

            var protocolComponent = store.getComponent(ref, GPDeathComponent.getComponentType());

            if(protocolComponent == null) return;

            var items = protocolComponent.getItems();

            if (items == null) return;

            player.getInventory().clear();

            player.getInventory().getCombinedHotbarUtilityConsumableStorage().addItemStacks(Arrays.stream(items).toList());
            protocolComponent.setItems(null);
            if (protocolComponent.getOriginal() == null) return;

            var original = protocolComponent.getOriginal();
            original.setShowDeathMenu(true);
            store.putComponent(ref, DeathComponent.getComponentType(), original);
        });
    }

    private static void ensureMenuNpc(World world, com.hypixel.hytale.component.Store<EntityStore> store, Vector3d spawnNear) {

        var cached = MENU_NPC_BY_WORLD.get(world.getName());
        if (cached != null && cached.isValid()) {
            return;
        }

        var roleName = pickMenuNpcRole();
        if (roleName == null) {
            return;
        }

        var spawnPos = spawnNear.clone().add(2.0, 0.0, 0.0);
        var spawn = NPCPlugin.get().spawnNPC(store, roleName, null, spawnPos, Vector3f.ZERO);
        if (spawn == null) {
            return;
        }

        var npcRef = spawn.first();
        var interactions = store.ensureAndGetComponent(npcRef, Interactions.getComponentType());
        store.ensureComponent(npcRef, Interactable.getComponentType());
        store.ensureComponent(npcRef, Invulnerable.getComponentType());
        store.ensureComponent(npcRef, Frozen.getComponentType());
        interactions.setInteractionId(InteractionType.Use, MENU_INTERACTION_ID);
        interactions.setInteractionHint(MENU_INTERACTION_HINT);
        store.putComponent(npcRef, Interactions.getComponentType(), interactions);
        MENU_NPC_BY_WORLD.put(world.getName(), npcRef);
    }

    private static String pickMenuNpcRole() {

        var npcPlugin = NPCPlugin.get();
        for (String candidate : MENU_NPC_ROLE_CANDIDATES) {
            if (npcPlugin.getIndex(candidate) >= 0) {
                return candidate;
            }
        }
        return null;
    }
}
