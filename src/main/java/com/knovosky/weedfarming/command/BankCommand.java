package com.knovosky.weedfarming.command;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class BankCommand extends Command {

    public BankCommand() {
        this.guildOnly = true;
        this.category = new Category("Economy Commands");
    }

    @Override
    protected void execute(CommandEvent e) {
        doCommand(e);
    }

    public abstract void doCommand(CommandEvent event);
}
