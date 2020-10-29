package com.knovosky.weedfarming.command.general;

import com.knovosky.weedfarming.Bot;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.command.GeneralCommand;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PingCommand extends GeneralCommand {

    private final Bot bot;

    public PingCommand(Bot bot) {
        this.name = "ping";
        this.help = "Ping to the servers running the Weed Farming bot (jetstax.com)";
        this.guildOnly = false;
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e) {
        MessageChannel channel = e.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong!")
                .queue(response -> {
                    response.editMessageFormat("Pong: %d ms", System.currentTimeMillis() - time).queue();
                });
    }
}
