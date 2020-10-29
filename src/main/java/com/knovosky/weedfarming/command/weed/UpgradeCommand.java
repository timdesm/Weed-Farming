package com.knovosky.weedfarming.command.weed;

import com.knovosky.weedfarming.Bot;
import com.knovosky.weedfarming.command.WeedCommand;
import com.knovosky.weedfarming.utils.EconomyUtil;
import com.knovosky.weedfarming.utils.FarmUtil;
import com.knovosky.weedfarming.utils.UpgradeUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class UpgradeCommand extends WeedCommand {

    private final Bot bot;

    public UpgradeCommand(Bot bot) {
        this.name = "upgrade";
        this.help = "Upgrade your business level to farm faster and have better plants";
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e)
    {
        EmbedBuilder ebuider = null;

        if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {

            int currUpgrade = UpgradeUtil.getUpgrade(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());

            if(currUpgrade < UpgradeUtil.maxUpgrade) {
                double balance = EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());
                double price = UpgradeUtil.prices[currUpgrade];

                if (UpgradeUtil.canBuy(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
                    EconomyUtil.setBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId(), balance - price);
                    UpgradeUtil.setUpgrade(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId(), (currUpgrade + 1));

                    ebuider = new EmbedBuilder()
                            .setColor(6676048)
                            .setDescription("You upgraded your farming business to Upgrade Level **" + (currUpgrade + 1) + "**")
                            .addField("Price", EconomyUtil.formatNumber(price) + "$", true)
                            .addField("Growing Time", "-20 seconds", true)
                            .addField("Plant Die Rate",  "-6.6% chance", true)
                            .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                            .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
                    e.getMessage().delete().queue();
                    e.getChannel().sendMessage(ebuider.build()).queue();
                    return;
                }

                ebuider = new EmbedBuilder()
                        .setColor(16748861)
                        .setDescription("You can't upgrade your farm, you need " + EconomyUtil.formatNumber(UpgradeUtil.prices[currUpgrade] - balance) + "$ more to have Upgrade " + (currUpgrade + 1) + "!")
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                        .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
                e.getMessage().delete().queue();
                e.getChannel().sendMessage(ebuider.build()).queue();
                return;
            }
            ebuider = new EmbedBuilder()
                    .setColor(4046827)
                    .setDescription("You already are at the max upgrade level!\nYou can get higher in ranking if you level your account with `weed level`")
                    .addField("Upgrade Level",  "Level " + currUpgrade, true)
                    .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());
            e.getMessage().delete().queue();
            e.getChannel().sendMessage(ebuider.build()).queue();
            return;
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
}
