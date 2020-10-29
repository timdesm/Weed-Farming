package com.knovosky.weedfarming.command.bank;

import com.knovosky.weedfarming.Bot;
import com.knovosky.weedfarming.command.BankCommand;
import com.knovosky.weedfarming.utils.EconomyUtil;
import com.knovosky.weedfarming.utils.FarmUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class BalanceCommand extends BankCommand {

    private final Bot bot;

    public BalanceCommand(Bot bot) {
        this.name = "balance";
        this.help = "View your current balance";
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e)
    {
        EmbedBuilder ebuider = null;

        if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
            ebuider = new EmbedBuilder()
                    .setColor(4046827)
                    .setDescription("The current bank balance of " + e.getMember().getAsMention())
                    .addField("Balance", EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$", true)
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
}