package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.NonNull;
import net.haro0.hytale.graveprotocol.components.GPDeathComponent;
import net.haro0.hytale.graveprotocol.ui.DeathDecisionUi;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class DeathDecisionSystem extends DeathSystems.OnDeathSystem {

    private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(
        new SystemDependency<>(Order.BEFORE, DeathSystems.PlayerDeathScreen.class),
        new SystemDependency<>(Order.AFTER, DeathSystems.PlayerDropItemsConfig.class),
        new SystemDependency<>(Order.BEFORE, DeathSystems.DropPlayerDeathItems.class)
    );

    @NonNull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {

        return DEPENDENCIES;
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl DeathComponent component, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        if (store.getComponent(ref, GPDeathComponent.getComponentType()) != null) {
            commandBuffer.removeComponent(ref, GPDeathComponent.getComponentType());
            return;
        }

        Player playerComponent = store.getComponent(ref, Player.getComponentType());

        assert playerComponent != null;

        PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());

        assert playerRefComponent != null;

        var gpComponent = commandBuffer.ensureAndGetComponent(ref, GPDeathComponent.getComponentType());
        gpComponent.setOriginal((DeathComponent) component.clone());
        component.setShowDeathMenu(false);
        component.setItemsLossMode(DeathConfig.ItemsLossMode.NONE);
        component.setItemsDurabilityLossPercentage(0);
        commandBuffer.removeComponent(ref, DeathComponent.getComponentType());

        var world = playerComponent.getWorld();
        assert world != null;

        // Open after respawn cleanup runs; otherwise ClearRespawnUI immediately dismisses this custom page.
        world.execute(() -> {
            if (!ref.isValid() || store.getComponent(ref, Player.getComponentType()) == null) {
                return;
            }
            playerComponent.getPageManager().openCustomPage(ref, store, new DeathDecisionUi(playerRefComponent));
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {

        return Player.getComponentType();
    }
}
