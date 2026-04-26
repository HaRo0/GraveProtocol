package net.haro0.hytale.graveprotocol.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.assets.Level;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DevMode extends AbstractPlayerCommand {

    public DevMode() {

        super("dev", "description");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> var2, @NonNullDecl Ref<EntityStore> var3, @NonNullDecl PlayerRef var4, @NonNullDecl World world) {

        var lynnRef = LevelStartService.findLynn(var2);
        var2.ensureComponent(lynnRef, Invulnerable.getComponentType());
    }
}
