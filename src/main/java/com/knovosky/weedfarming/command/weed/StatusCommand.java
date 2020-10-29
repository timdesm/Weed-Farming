package com.knovosky.weedfarming.command.weed;

import com.knovosky.weedfarming.Bot;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.command.WeedCommand;
import com.knovosky.weedfarming.utils.EconomyUtil;
import com.knovosky.weedfarming.utils.FarmUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class StatusCommand extends WeedCommand {

    private final Bot bot;

    public StatusCommand(Bot bot) {
        this.name = "status";
        this.help = "Get the status of your plants";
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e) {
        EmbedBuilder ebuider = null;

        if (FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
            if (FarmUtil.totalPlants(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId()) >= 1) {

                Long ready = FarmUtil.totalFarmable(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());
                Long dead = FarmUtil.totalDead(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());

                ebuider = new EmbedBuilder()
                        .setColor(4046827)
                        .setDescription("Here you have the status of your plants")
                        .addField("Ready", ready + "", true)
                        .addField("Growing", ((FarmUtil.totalPlants(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId()) - ready) - dead) + "", true)
                        .addField("Dead", dead + "", true)
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                        .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");

            } else {
                ebuider = new EmbedBuilder()
                        .setColor(16748861)
                        .setDescription("You don't have plants yet\nbuy them with `weed buy`")
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                        .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
            }
            e.getChannel().sendMessage(ebuider.build()).queue();
        } else {
            ebuider = new EmbedBuilder()
                    .setColor(16727357)
                    .setDescription("You don't have a Weed Planting business!\nOpen one with `weed open`")
                    .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
            e.getChannel().sendMessage(ebuider.build()).queue();
        }
        e.getMessage().delete().queue();
    }
}