package com.knovosky.weedfarming.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class GeneralCommand extends Command {

    public GeneralCommand() {
        this.guildOnly = false;
        this.category = new Command.Category("General Commands");
    }

    @Override
    protected void execute(CommandEvent e) {
        doCommand(e);
    }

    public abstract void doCommand(CommandEvent event);
}
