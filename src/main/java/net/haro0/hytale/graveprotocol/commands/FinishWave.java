package net.haro0.hytale.graveprotocol.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnAttackerComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class FinishWave extends AbstractPlayerCommand {

    public FinishWave() {

        super("finish", "description");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl Store<EntityStore> var2, @NonNullDecl Ref<EntityStore> var3, @NonNullDecl PlayerRef var4, @NonNullDecl World world) {

        Damage.CommandSource damageSource = new Damage.CommandSource(context.sender(), "damage");
        var2.forEachEntityParallel(LynnAttackerComponent.getComponentType(), (n, archetype,buffer) -> {
            DeathComponent.tryAddComponent(buffer,archetype.getReferenceTo(n),new Damage(damageSource, DamageCause.COMMAND,Float.MAX_VALUE));
        });
    }
}
