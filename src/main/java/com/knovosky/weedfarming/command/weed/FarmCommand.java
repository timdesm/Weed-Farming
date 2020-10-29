package com.knovosky.weedfarming.command.weed;

import com.knovosky.weedfarming.Bot;
import com.knovosky.weedfarming.command.WeedCommand;
import com.knovosky.weedfarming.database.MySQL;
import com.knovosky.weedfarming.utils.UpgradeUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.knovosky.weedfarming.utils.EconomyUtil;
import com.knovosky.weedfarming.utils.FarmUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public class FarmCommand extends WeedCommand {

    private final Bot bot;

    public FarmCommand(Bot bot) {
        this.name = "farm";
        this.help = "Farm all plants that can be farmed";
        this.bot = bot;
    }

    @Override
    public void doCommand(CommandEvent e)
    {
        EmbedBuilder ebuider = null;

        if(FarmUtil.hasBusiness(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) {
            if(FarmUtil.totalPlants(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId()) >= 1 ) {
                List<Integer> ready = FarmUtil.farmablePlats(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId());
                if(ready.size() >= 1) {

                    ebuider = new EmbedBuilder()
                            .setColor(4046827)
                            .setDescription("Farming...")
                            .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                            .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");

                    e.getChannel().sendMessage(ebuider.build())
                            .queue(response -> {
                                response.editMessage(farmPlants(bot.getDatabase(), e, ready).build()).queue();
                            });

                    e.getMessage().delete().queue();
                    return;
                }
                else {
                    ebuider = new EmbedBuilder()
                            .setColor(16748861)
                            .setDescription("Your plants are growing, please wait")
                            .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                            .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
                }
            }
            else {
                ebuider = new EmbedBuilder()
                        .setColor(16748861)
                        .setDescription("You don't have plants yet\nbuy them with `weed buy`")
                        .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl())
                        .setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(bot.getDatabase(), e.getGuild().getId(), e.getMember().getId())) + "$");
            }
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

    private static EmbedBuilder farmPlants(MySQL database, CommandEvent e, List<Integer> ready) {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(4046827)
                .setAuthor(e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl(), e.getMember().getUser().getAvatarUrl());

        Double total = 0.0;

        HashMap<Integer, Long> amounts = new HashMap<>();

        int totalAmount = 0;

        for(Integer id : ready) {
            FarmUtil.farmPlant(database, id, UpgradeUtil.getFarmUsage(UpgradeUtil.getUpgrade(database, e.getGuild().getId(), e.getMember().getId())));
            Long amount = FarmUtil.getAmount(database, id);
            totalAmount += amount;

            Double reward = FarmUtil.getReward();
            reward = reward * amount;

            total += reward;

            Integer used = FarmUtil.getUsage(database, id);

            if(!amounts.containsKey(used)) {
                amounts.put(used, 0L);
            }

            Long temp = amounts.get(used);
            temp += amount;
            amounts.put(used, temp);
        }

        Map<Integer, Long> sorted = new TreeMap<Integer, Long>(Collections.reverseOrder());
        sorted.putAll(amounts);

        StringBuilder sb = new StringBuilder();
        for(int i = sorted.entrySet().iterator().next().getKey(); i > 0; i--) {
            Long a = 0L;
            if(sorted.containsKey(i)) {
                a = sorted.get(i);
            }
            sb.append("- " + i + " uses: " + a + "\n");
        }

        eb.setDescription("You farmed **" + totalAmount +  "** plants for in total " + EconomyUtil.formatNumber(total) + "$\n\n" + sb.toString());

        EconomyUtil.setBalance(database, e.getGuild().getId(), e.getMember().getId(), EconomyUtil.getBalance(database, e.getGuild().getId(), e.getMember().getId()) + total);
        eb.setFooter("Balance: " + EconomyUtil.formatNumber(EconomyUtil.getBalance(database, e.getGuild().getId(), e.getMember().getId())) + "$");

        return eb;
    }

}
