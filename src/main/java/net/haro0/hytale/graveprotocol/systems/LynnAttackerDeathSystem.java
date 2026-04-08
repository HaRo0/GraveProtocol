package net.haro0.hytale.graveprotocol.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.command.commands.server.auth.AuthPersistenceCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.NonNull;
import net.haro0.hytale.graveprotocol.codecs.assets.Prestige;
import net.haro0.hytale.graveprotocol.codecs.components.GPDeathComponent;
import net.haro0.hytale.graveprotocol.codecs.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import net.haro0.hytale.graveprotocol.ui.DeathDecisionUi;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;
import net.haro0.hytale.graveprotocol.utils.LevelUtils;
import net.haro0.hytale.graveprotocol.utils.PrestigeUtils;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class LynnAttackerDeathSystem extends DeathSystems.OnDeathSystem {

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl DeathComponent component, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        var attackerComponent = store.getComponent(ref, LynnAttackerComponent.getComponentType());
        var lynnId = attackerComponent.getLynnId();
        if(lynnId == null) return;
        var lynnRef = store.getExternalData().getRefFromUUID(lynnId);
        var lynnComponent = store.getComponent(lynnRef, LynnComponent.getComponentType());

        if(!lynnComponent.markAttackerKilled()) return;

        var pRef = store.getExternalData().getRefFromUUID(lynnComponent.getPlayerId());

        if(LevelStartService.startNextWave(pRef,commandBuffer,store.getExternalData().getWorld(), lynnRef)) return;

        var playerRef = store.getComponent(pRef, PlayerRef.getComponentType());
        if(playerRef != null){
            store.getExternalData().getWorld().execute(()->playerRef.sendMessage(Message.raw("Congratulations! You have completed the level!")));
        }
        var dataComponent = store.getComponent(pRef, GPPlayerDataComponent.getComponentType());
        dataComponent.setLevelIndex(dataComponent.getLevelIndex()+1);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {

        return LynnAttackerComponent.getComponentType();
    }
}
