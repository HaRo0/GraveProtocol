package net.haro0.hytale.graveprotocol.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class GPCommand extends AbstractCommandCollection {

    public GPCommand() {

        super("gp", "description");
        this.addSubCommand(new StartLevel());
        this.addSubCommand(new FinishWave());
        this.addSubCommand(new SetProgress());
        this.addSubCommand(new ScenarioTp());
        this.addSubCommand(new DevMode());
    }
}
