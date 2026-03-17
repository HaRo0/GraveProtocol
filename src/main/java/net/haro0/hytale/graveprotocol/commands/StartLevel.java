package net.haro0.hytale.graveprotocol.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.components.GPDeathComponent;
import net.haro0.hytale.graveprotocol.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.utils.LevelUtils;
import net.haro0.hytale.graveprotocol.utils.PrestigeUtils;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class StartLevel extends AbstractPlayerCommand {

    public StartLevel() {

        super("start", "description");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext var1, @NonNullDecl Store<EntityStore> var2, @NonNullDecl Ref<EntityStore> var3, @NonNullDecl PlayerRef var4, @NonNullDecl World var5) {

        var component = var2.ensureAndGetComponent(var3, GPPlayerDataComponent.getComponentType());
        var prestige = PrestigeUtils.getPrestige(component);
        var level = LevelUtils.getPrestigeLevels(prestige);

        var player = var2.getComponent(var3, Player.getComponentType());
        var transformComponent = var2.getComponent(var3, TransformComponent.getComponentType());

        var chunkRef = transformComponent.getChunkRef();

    }
}
