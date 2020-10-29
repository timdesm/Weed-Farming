package com.knovosky.weedfarming.command.general;

import com.knovosky.weedfarming.Bot;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.command.GeneralCommand;

public class AuthorCommand extends GeneralCommand {

    private final Bot bot;

    public AuthorCommand(Bot bot) {
        this.name = "author";
        this.help = "Get to know more about the author";
        this.guildOnly = false;
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e) {
        e.reply("This bot is coded and made by Timie#0001 \n Checkout my websites at https://knovosky.com and https://timdesmet.be");
    }
}
