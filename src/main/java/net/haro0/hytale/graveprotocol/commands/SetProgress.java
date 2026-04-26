package net.haro0.hytale.graveprotocol.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SetProgress extends AbstractPlayerCommand {

    private final RequiredArg<Integer> prestige = this.withRequiredArg("Prestige","Prestige to be set to", ArgTypes.INTEGER);
    private final RequiredArg<Integer> level = this.withRequiredArg("Level","Level to be set to", ArgTypes.INTEGER);

    public SetProgress() {

        super("setprogress", "description");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> var2, @NonNullDecl Ref<EntityStore> var3, @NonNullDecl PlayerRef var4, @NonNullDecl World world) {

        var data = var2.ensureAndGetComponent(var4.getReference(), GPPlayerDataComponent.getComponentType());
        data.setLevelIndex(level.get(context));
        data.setPrestigeIndex(prestige.get(context));
    }
}
