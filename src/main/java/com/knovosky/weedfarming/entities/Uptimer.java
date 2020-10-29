package com.knovosky.weedfarming.entities;

import com.knovosky.weedfarming.Bot;
import com.knovosky.weedfarming.utils.FormatUtil;
import net.dv8tion.jda.api.JDA;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class Uptimer {

    protected final Bot bot;
    private final int delay;

    private boolean started = false;

    private Uptimer(Bot bot, int delay)
    {
        this.bot = bot;
        this.delay = delay;
    }

    public synchronized void start(ScheduledExecutorService threadpool)
    {
        if(started)
            return;
        started = true;
        threadpool.scheduleWithFixedDelay(() -> check(), delay, delay, TimeUnit.SECONDS);
    }

    protected abstract void check();


    public static class DatabaseUptimer extends Uptimer {
        private int failures = 0;

        public DatabaseUptimer(Bot bot)
        {
            super(bot, 120);
        }

        @Override
        protected void check()
        {
            if(!bot.getDatabase().databaseCheck())
            {
                failures++;
                if(failures < 3)
                    bot.getWebhookLog().send(WebhookLog.Level.ERROR, "Failed a database check (" + failures + ")!");
                else
                {
                    bot.getWebhookLog().send(WebhookLog.Level.ERROR, "Failed a database check (" + failures + ")! Restarting...");
                    System.exit(0);
                }
            }
            else
                failures = 0;
        }
    }

    public static class StatusUptimer extends Uptimer
    {
        private enum BotStatus { LOADING, ONLINE, PARTIAL_OUTAGE, OFFLINE }

        private BotStatus status = BotStatus.LOADING;
        private Instant lastChange = Instant.now();
        private boolean attemptedFix = false;

        public StatusUptimer(Bot bot)
        {
            super(bot, 30);
        }

        @Override
        protected void check()
        {
            long onlineCount = bot.getShardManager().getShardCache().stream().filter(jda -> jda.getStatus() == JDA.Status.CONNECTED).count();
            BotStatus curr = onlineCount == bot.getShardManager().getShardCache().size() ? BotStatus.ONLINE
                    : status == BotStatus.LOADING ? BotStatus.LOADING
                    : onlineCount == 0 ? BotStatus.OFFLINE
                    : BotStatus.PARTIAL_OUTAGE;

            if(curr != status) // log if it changed
            {
                bot.getWebhookLog().send(WebhookLog.Level.INFO, "Status changed from `" + status + "` to `" + curr + "`: "
                        + FormatUtil.formatShardStatuses(bot.getShardManager().getShards()));
                lastChange = Instant.now();
                status = curr;
                if(status == BotStatus.ONLINE)
                    attemptedFix = false; // if we're fully online, reset status of an outage
            }
            else // if it didn't change, maybe take action
            {
                if(status == BotStatus.PARTIAL_OUTAGE)
                {
                    int minutes = (int) lastChange.until(Instant.now(), ChronoUnit.MINUTES);
                    if(minutes > 10 && !attemptedFix)
                    {
                        List<Integer> down = bot.getShardManager().getShardCache().stream()
                                .filter(jda -> jda.getStatus() != JDA.Status.CONNECTED)
                                .map(jda -> jda.getShardInfo().getShardId())
                                .collect(Collectors.toList());
                        bot.getWebhookLog().send(WebhookLog.Level.WARNING, "Attempting to restart some shards: `" + down + "`");
                        down.forEach(i -> bot.getShardManager().restart(i));
                        attemptedFix = true;
                    }
                    else if(minutes > 30)
                    {
                        bot.getWebhookLog().send(WebhookLog.Level.ERROR, "Extended outage, restarting...");
                        System.exit(0);
                    }
                }
            }
        }
    }
}
