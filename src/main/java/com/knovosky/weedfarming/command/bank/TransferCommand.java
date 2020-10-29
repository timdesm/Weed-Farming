package com.knovosky.weedfarming.command.bank;

import com.knovosky.weedfarming.Bot;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.command.BankCommand;
import com.knovosky.weedfarming.utils.FarmUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class TransferCommand extends BankCommand {

    private final Bot bot;

    public TransferCommand(Bot bot) {
        this.name = "transfer";
        this.help = "Transfer money to someone else";
        this.arguments = "<user> <amount>";
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e)
    {
        EmbedBuilder ebuider = null;

        if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
            if(!e.getArgs().isEmpty()) {
                if(e.getMessage().getMentionedMembers().size() == 1) {

                }
            }
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
