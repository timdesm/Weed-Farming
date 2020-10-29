package com.knovosky.weedfarming.command.weed;

import com.knovosky.weedfarming.Bot;
import com.knovosky.weedfarming.utils.UpgradeUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.command.WeedCommand;
import com.knovosky.weedfarming.utils.EconomyUtil;
import com.knovosky.weedfarming.utils.FarmUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class StatsCommand extends WeedCommand {

    private final Bot bot;

    public StatsCommand(Bot bot) {
        this.name = "stats";
        this.arguments = "[member]";
        this.help = "View the business stats of yourself or someone else";
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e)
    {
        EmbedBuilder ebuider = null;

        if(e.getArgs().isEmpty()) {
            if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
                ebuider = new EmbedBuilder()
                        .setColor(4046827)
                        .setDescription("This are the business stats of " + e.getMember().getAsMention())
                        .addField("Business value", EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$", true)
                        .addField("Plants", FarmUtil.totalPlants(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId()) + " plants", true)
                        .addField("Upgrade Level",  "Level " + UpgradeUtil.getUpgrade(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId()), true)
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
                e.getChannel().sendMessage(ebuider.build()).queue();
            }
            else {
                ebuider = new EmbedBuilder()
                        .setColor(16727357)
                        .setDescription("You don't have a Weed Planting business!\nOpen one with `weed open`")
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
                e.getChannel().sendMessage(ebuider.build()).queue();
            }
            e.getMessage().delete().queue();
        }
        else if(e.getMessage().getMentionedMembers().size() >= 1) {
            Member target = e.getMessage().getMentionedMembers().get(0);
            if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), target.getId())) {
                ebuider = new EmbedBuilder()
                        .setColor(4046827)
                        .setDescription("This are the business stats of " + target.getAsMention())
                        .addField("Business value", EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), target.getId())) + "$", true)
                        .addField("Plants", FarmUtil.totalPlants(bot.getDatabase(), e.getGuild().getId(), target.getId()) + " plants", true)
                        .addField("Upgrade Level",  "Level " + UpgradeUtil.getUpgrade(bot.getDatabase(), e.getGuild().getId(), target.getId()), true)
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
                e.getChannel().sendMessage(ebuider.build()).queue();
            }
            else {
                ebuider = new EmbedBuilder()
                        .setColor(16727357)
                        .setDescription(target.getAsMention() + " don't has a Weed Planting business!")
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
                e.getChannel().sendMessage(ebuider.build()).queue();
            }
            e.getMessage().delete().queue();
        }
        else {
            ebuider = new EmbedBuilder()
                    .setColor(16727357)
                    .setDescription("The member you searched for was not found. \nWeed stats usage: `weed stats [member]`")
                    .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
            e.getChannel().sendMessage(ebuider.build()).queue();
            e.getMessage().delete().queue();
        }
    }

}
