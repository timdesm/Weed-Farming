package com.knovosky.weedfarming.command.weed;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.command.WeedCommand;

public class PlantsCommand extends WeedCommand {

    public PlantsCommand() {
        this.name = "plants";
        this.arguments = "<amount>";
        this.help = "View your plants";
    }

    @Override
    public void doCommand(CommandEvent e)
    {

    }

}
