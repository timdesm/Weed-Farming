package com.knovosky.weedfarming.command.weed;

import com.knovosky.weedfarming.Bot;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.command.WeedCommand;
import com.knovosky.weedfarming.utils.EconomyUtil;
import com.knovosky.weedfarming.utils.FarmUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class OpenCommand extends WeedCommand {

    private final Bot bot;

    public OpenCommand(Bot bot) {
        this.name = "open";
        this.help = "Open your own weed business";
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e)
    {
        EmbedBuilder ebuider;

        if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
            ebuider = new EmbedBuilder()
                    .setColor(16727357)
                    .setDescription("You already have an open Weed Planting business!")
                    .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                    .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
        }
        else {
            FarmUtil.openBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());
            ebuider = new EmbedBuilder()
                    .setColor(6676048)
                    .setDescription("Opened your new Weed Planting business!")
                    .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
        }

        e.getChannel().sendMessage(ebuider.build()).queue();
        e.getMessage().delete().queue();
    }
}
