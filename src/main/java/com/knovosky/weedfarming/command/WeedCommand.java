package com.knovosky.weedfarming.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class WeedCommand extends Command {

    public WeedCommand() {
        this.guildOnly = true;
        this.category = new Category("Farming Commands");
    }

    @Override
    protected void execute(CommandEvent e) {
        doCommand(e);
    }

    public abstract void doCommand(CommandEvent event);
}
