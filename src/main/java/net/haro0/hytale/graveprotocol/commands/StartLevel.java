package net.haro0.hytale.graveprotocol.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class StartLevel extends AbstractPlayerCommand {

    public StartLevel() {

        super("start", "description");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext var1, @NonNullDecl Store<EntityStore> var2, @NonNullDecl Ref<EntityStore> var3, @NonNullDecl PlayerRef var4, @NonNullDecl World var5) {

        LevelStartService.startLevel(var3, var2);
    }
}
