package com.knovosky.weedfarming;

import com.knovosky.weedfarming.command.bank.BalanceCommand;
import com.knovosky.weedfarming.command.general.AuthorCommand;
import com.knovosky.weedfarming.command.general.PingCommand;
import com.knovosky.weedfarming.command.weed.*;
import com.knovosky.weedfarming.database.MySQL;
import com.knovosky.weedfarming.entities.Uptimer;
import com.knovosky.weedfarming.entities.WebhookLog;
import com.knovosky.weedfarming.utils.FormatUtil;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Bot {

    private ShardManager shards;

    private final WebhookLog webhook;
    private final ScheduledExecutorService threadpool;
    private final MySQL database;
    private final EventWaiter waiter;
    private final Logger LOG = LoggerFactory.getLogger("Bot");

    private Bot(MySQL database, String webhookUrl) {
        this.database = database;
        this.threadpool = Executors.newScheduledThreadPool(20);
        this.webhook = new WebhookLog(webhookUrl, System.getProperty("logname"));
        this.waiter = new EventWaiter(Executors.newSingleThreadScheduledExecutor(), false);

        try {
            this.database.openConnection();
        } catch (Exception e) {
            this.getWebhookLog().send(WebhookLog.Level.ERROR, "MySQLservice disabled because of: (" + e.getMessage() + ")! Restarting...");
            System.exit(0);
        }

        new Uptimer.DatabaseUptimer(this).start(this.threadpool);
        new Uptimer.StatusUptimer(this).start(this.threadpool);
    }

    public ShardManager getShardManager()
    {
        return this.shards;
    }

    public ScheduledExecutorService getThreadpool()
    {
        return threadpool;
    }

    public WebhookLog getWebhookLog() { return this.webhook; }

    public MySQL getDatabase()
    {
        return this.database;
    }

    public EventWaiter getWaiter() { return this.waiter; }

    public void shutdown() {
        threadpool.shutdown();
        shards.shutdown();
    }

    /**
     * Starts the application in Bot mode
     * @param shardTotal
     * @param shardSetId
     * @param shardSetSize
     * @throws java.lang.Exception
     */
    public static void main(int shardTotal, int shardSetId, int shardSetSize) throws Exception
    {
        System.setProperty("config.file", System.getProperty("config.file", "application.conf"));
        Config config = ConfigFactory.load();

        Bot bot = new Bot(new MySQL(config.getString("database.host"),
                                    config.getString("database.port"),
                                    config.getString("database.database"),
                                    config.getString("database.username"),
                                    config.getString("database.password")),
                            config.getString("bot.webhook-url"));

        CommandClient client = new CommandClientBuilder()
                .setPrefix(config.getString("settings.prefix"))
                .setAlternativePrefix(config.getString("settings.prefix-alt"))
                .setOwnerId(Constants.OWNERID)
                .setHelpConsumer(event -> event.replyInDm(FormatUtil.formatHelp(event),
                        m-> {try{event.getMessage().addReaction(Constants.REACTION).queue(s->{},f->{});}catch(PermissionException ignored){}},
                        f-> event.replyWarning("Help could not be sent because you are blocking Direct Messages")))
                .setDiscordBotsKey(config.getString("stats.discordbots-key"))
                .addCommands(
                        new AuthorCommand(bot),
                        new PingCommand(bot),
                        new OpenCommand(bot),
                        new BuyCommand(bot),
                        new FarmCommand(bot),
                        new StatsCommand(bot),
                        new StatusCommand(bot),
                        new UpgradeCommand(bot),
                        new BalanceCommand(bot)
                ).build();

        bot.webhook.send(WebhookLog.Level.INFO, "Starting shards `"+(shardSetId*shardSetSize + 1) + " - " + ((shardSetId+1)*shardSetSize) + "` of `"+shardTotal+"`...");

        bot.shards = DefaultShardManagerBuilder
                .createLight(config.getString("bot.token"), GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
                .setShardsTotal(shardTotal)
                .setShards(shardSetId*shardSetSize, (shardSetId+1)*shardSetSize-1)
                .setActivity(Activity.playing("loading..."))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(client, bot.getWaiter())
                .enableCache(CacheFlag.MEMBER_OVERRIDES)
                .setChunkingFilter(ChunkingFilter.NONE)
                .build();

        try {
            new DiscordBotListAPI.Builder()
                    .token(config.getString("stats.discordbotlist-key"))
                    .botId(config.getString("bot.client"))
                    .build()
                    .setStats(bot.shards.getGuilds().size());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
