package com.knovosky.weedfarming.command.weed;

import com.knovosky.weedfarming.Bot;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.knovosky.weedfarming.command.WeedCommand;
import com.knovosky.weedfarming.utils.EconomyUtil;
import com.knovosky.weedfarming.utils.FarmUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class BuyCommand extends WeedCommand {

    private final Bot bot;
    private final OrderedMenu.Builder builder;

    public BuyCommand(Bot bot) {
        this.name = "buy";
        this.arguments = "<amount>";
        this.help = "Buy an amount of plants";
        this.bot = bot;

        builder = new OrderedMenu.Builder()
                .allowTextInput(false)
                .useNumbers()
                .useCancelButton(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void doCommand(CommandEvent e)
    {
        EmbedBuilder ebuider = null;

        if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
            if(e.getArgs().isEmpty()) {

                if(canBuy(e) == 0) {
                    ebuider = new EmbedBuilder()
                            .setColor(16748861)
                            .setDescription("You can't buy any plants, you need " + FarmUtil.price + "$ for 1 plant")
                            .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                            .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
                    e.getMessage().delete().queue();
                    e.getChannel().sendMessage(ebuider.build()).queue();

                    return;
                }
                else {
                    ebuider = new EmbedBuilder()
                            .setColor(4046827)
                            .setDescription("Loading...")
                            .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                            .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");

                    e.getChannel().sendMessage(ebuider.build()).queue(response -> {
                        this.builder.setChoices(new String[0])
                                .setColor(Color.CYAN)
                                .setDescription("How many plants do you want to buy?\n\nYou can buy **" + canBuy(e) + "** plants")
                                .setSelection((msg, i) -> {
                                    long amount = i;

                                    int multiply = 1;
                                    if(canBuy(e) >= 100) {
                                        multiply = 10;
                                    }
                                    if(canBuy(e) >= 1000) {
                                        multiply = 100;
                                    }

                                    amount = amount * multiply;

                                    double price = amount * FarmUtil.price;
                                    double balance = EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());

                                    if(price > balance) {
                                        EmbedBuilder eb = new EmbedBuilder()
                                                .setColor(16748861)
                                                .setDescription("You have not enough balance!\n Required: " + EconomyUtil.formatNumber(price) + "$\n\nYou can buy **" + canBuy(e) + "** plants")
                                                .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                                                .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");

                                        e.getChannel().sendMessage(eb.build()).queue();
                                        return;
                                    }

                                    EconomyUtil.setBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId(), balance - price);
                                    FarmUtil.addPlant(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId(), amount);

                                    EmbedBuilder eb = new EmbedBuilder()
                                            .setColor(6676048)
                                            .setDescription("You bought **" + amount + "** new plants!\nYou have now a total of **" + FarmUtil.totalPlants(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId()) + "** plants")
                                            .addField("Price (Per)", EconomyUtil.formatNumber(FarmUtil.price) + "$", true)
                                            .addField("Amount", amount + " plants", true)
                                            .addField("Total", EconomyUtil.formatNumber(price) + "$", true)
                                            .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                                            .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");

                                    e.getChannel().sendMessage(eb.build()).queue();
                                    return;

                                }).setCancel((msg) -> {

                            EmbedBuilder eb = new EmbedBuilder()
                                    .setColor(16727357)
                                    .setDescription("The buy request has been canceled")
                                    .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                                    .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");

                            e.getChannel().sendMessage(eb.build()).queue();

                        }).setUsers(new User[] { e.getMessage().getAuthor()});

                        loadReactions(e);

                        this.builder.build().display(response);
                    });
                }
            }
            else {
                if(e.getArgs().split(" ")[0].matches("-?(0|[1-9]\\d*)")) {
                    long amount = Long.parseLong(e.getArgs().split(" ")[0]);
                    double price = amount * FarmUtil.price;

                    double balance = EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());
                    if(price > balance) {
                        ebuider = new EmbedBuilder()
                                .setColor(16748861)
                                .setDescription("You have not enough balance!\n Required: " + EconomyUtil.formatNumber(price) + "$\n\nYou can buy **" + canBuy(e) + "** plants")
                                .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                                .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
                    }
                    else {
                        EconomyUtil.setBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId(), balance - price);
                        FarmUtil.addPlant(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId(), amount);

                        ebuider = new EmbedBuilder()
                                .setColor(6676048)
                                .setDescription("You bought **" + amount + "** new plants!\nYou have now a total of **" + FarmUtil.totalPlants(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId()) + "** plants")
                                .addField("Price (Per)", EconomyUtil.formatNumber(FarmUtil.price) + "$", true)
                                .addField("Amount", amount + " plants", true)
                                .addField("Total", EconomyUtil.formatNumber(price) + "$", true)
                                .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                                .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
                    }
                }
                else {
                    ebuider = new EmbedBuilder()
                            .setColor(16727357)
                            .setDescription("Please provide a valid number as amount!")
                            .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                            .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
                }

                e.getChannel().sendMessage(ebuider.build()).queue();
                e.getMessage().delete().queue();
                return;
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

    private void loadReactions(CommandEvent e) {
        Long canBuy = canBuy(e);

        int multiply = 1;
        if(canBuy >= 100) {
            multiply = 10;
        }
        if(canBuy >= 1000) {
            multiply = 100;
        }

        for(long i = 0; i < canBuy; i++) {
            if(i <= 9) {
                if((i + 1) == 1) {
                    this.builder.addChoice("Buy " + (i + 1) * multiply  + " plant");
                }
                else {
                    this.builder.addChoice("Buy " + (i + 1) * multiply + " plants");
                }
            }
        }
    }

    private Long canBuy(CommandEvent e) {
        double balance = EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());
        return (long) Math.floor(balance/FarmUtil.price);
    }
}
