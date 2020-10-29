package com.knovosky.weedfarming.utils;

import com.knovosky.weedfarming.Constants;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.JDA;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class FormatUtil {

    public static String formatShardStatuses(Collection<JDA> shards)
    {
        HashMap<JDA.Status, String> map = new HashMap<>();
        shards.forEach(jda -> map.put(jda.getStatus(), map.getOrDefault(jda.getStatus(), "") + " " + jda.getShardInfo().getShardId()));
        StringBuilder sb = new StringBuilder("```diff");
        map.entrySet().forEach(entry -> sb.append("\n").append(entry.getKey()==JDA.Status.CONNECTED ? "+ " : "- ")
                .append(entry.getKey()).append(":").append(entry.getValue()));
        return sb.append(" ```").toString();
    }

    public static String formatHelp(CommandEvent event)
    {
        StringBuilder builder = new StringBuilder(Constants.SHAMROCK+" __**"+event.getSelfUser().getName()+"** commands:__\n");
        Command.Category category = null;
        for(Command command : event.getClient().getCommands())
        {
            if(command.isHidden())
                continue;
            if(command.isOwnerCommand() && !event.getAuthor().getId().equals(event.getClient().getOwnerId()))
                continue;
            if(!Objects.equals(category, command.getCategory()))
            {
                category = command.getCategory();
                builder.append("\n\n  __").append(category==null ? "No Category" : category.getName()).append("__:\n");
            }
            builder.append("\n**").append(event.getClient().getPrefix()).append(command.getName())
                    .append(command.getArguments()==null ? "**" : " "+command.getArguments()+"**")
                    .append(" - ").append(command.getHelp());
        }
        builder.append("\n\nDo not include <> nor [] - <> means required and [] means optional."
                + "\nFor additional help, contact "+ Constants.OWNER+" or check out "+Constants.WEBSITE);
        return builder.toString();
    }


}
