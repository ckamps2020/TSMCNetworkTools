package me.thesquadmc;

import com.mongodb.client.MongoCollection;
import me.gong.mcleaks.MCLeaksAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.thesquadmc.abstraction.AbstractionModule;
import me.thesquadmc.abstraction.NMSAbstract;
import me.thesquadmc.commands.AdminChatCommand;
import me.thesquadmc.commands.AlertCommand;
import me.thesquadmc.commands.ApplyCommand;
import me.thesquadmc.commands.ChangeLogCommand;
import me.thesquadmc.commands.ChatSilenceCommand;
import me.thesquadmc.commands.ChatSlowCommand;
import me.thesquadmc.commands.DiscordCommand;
import me.thesquadmc.commands.DisguisePlayerCommand;
import me.thesquadmc.commands.EssentialCommands;
import me.thesquadmc.commands.Find2Command;
import me.thesquadmc.commands.FindCommand;
import me.thesquadmc.commands.ForceFieldCommand;
import me.thesquadmc.commands.FreezeCommand;
import me.thesquadmc.commands.FreezePanelCommand;
import me.thesquadmc.commands.GamemodeCommand;
import me.thesquadmc.commands.HealCommand;
import me.thesquadmc.commands.InvseeCommand;
import me.thesquadmc.commands.LaunchCommand;
import me.thesquadmc.commands.LogsCommand;
import me.thesquadmc.commands.LookupCommand;
import me.thesquadmc.commands.MGCommand;
import me.thesquadmc.commands.MOTDClearCommand;
import me.thesquadmc.commands.MOTDCommand;
import me.thesquadmc.commands.ManagerChatCommand;
import me.thesquadmc.commands.MessageCommand;
import me.thesquadmc.commands.MonitorCommand;
import me.thesquadmc.commands.NTVersionCommand;
import me.thesquadmc.commands.NoteCommand;
import me.thesquadmc.commands.OnlineCountCommand;
import me.thesquadmc.commands.PartyCommand;
import me.thesquadmc.commands.PingCommand;
import me.thesquadmc.commands.ProxyTransportCommand;
import me.thesquadmc.commands.QueueManagerCommand;
import me.thesquadmc.commands.QueueRestartCommand;
import me.thesquadmc.commands.RandomTPCommand;
import me.thesquadmc.commands.RestartTimeCommand;
import me.thesquadmc.commands.SmiteCommand;
import me.thesquadmc.commands.StaffAlertCommand;
import me.thesquadmc.commands.StaffChatCommand;
import me.thesquadmc.commands.StaffMenuCommand;
import me.thesquadmc.commands.StafflistCommand;
import me.thesquadmc.commands.StaffmodeCommand;
import me.thesquadmc.commands.StatusCommand;
import me.thesquadmc.commands.StopCommand;
import me.thesquadmc.commands.StoreCommand;
import me.thesquadmc.commands.TeleportCommand;
import me.thesquadmc.commands.TitleCommand;
import me.thesquadmc.commands.UnFreezeCommand;
import me.thesquadmc.commands.UndisguisePlayerCommand;
import me.thesquadmc.commands.UniquePlayersCommand;
import me.thesquadmc.commands.VanishCommand;
import me.thesquadmc.commands.VanishListCommand;
import me.thesquadmc.commands.WebsiteCommand;
import me.thesquadmc.commands.WhitelistCommand;
import me.thesquadmc.commands.XrayVerboseCommand;
import me.thesquadmc.commands.YtNickCommand;
import me.thesquadmc.commands.YtVanishCommand;
import me.thesquadmc.inventories.FrozenInventory;
import me.thesquadmc.inventories.StaffmodeInventory;
import me.thesquadmc.listeners.ConnectionListeners;
import me.thesquadmc.listeners.FilterListener;
import me.thesquadmc.listeners.ForceFieldListeners;
import me.thesquadmc.listeners.FreezeListener;
import me.thesquadmc.listeners.LaunchListener;
import me.thesquadmc.listeners.LightningListener;
import me.thesquadmc.listeners.ServerListener;
import me.thesquadmc.listeners.StaffmodeListener;
import me.thesquadmc.listeners.TimedListener;
import me.thesquadmc.listeners.VanishListener;
import me.thesquadmc.listeners.WhitelistListener;
import me.thesquadmc.listeners.XrayListener;
import me.thesquadmc.managers.BootManager;
import me.thesquadmc.managers.CountManager;
import me.thesquadmc.managers.HologramManager;
import me.thesquadmc.managers.NPCManager;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.managers.QueueManager;
import me.thesquadmc.managers.TempDataManager;
import me.thesquadmc.networking.RedisHandler;
import me.thesquadmc.networking.mongo.Mongo;
import me.thesquadmc.networking.mongo.MongoUserDatabase;
import me.thesquadmc.networking.mongo.UserDatabase;
import me.thesquadmc.networking.mysql.DatabaseManager;
import me.thesquadmc.networking.redis.RedisManager;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.networking.redis.channels.AnnounceChannel;
import me.thesquadmc.networking.redis.channels.FindChannel;
import me.thesquadmc.networking.redis.channels.MessageChannel;
import me.thesquadmc.networking.redis.channels.MonitorChannel;
import me.thesquadmc.networking.redis.channels.PartyChannel;
import me.thesquadmc.networking.redis.channels.ServerManagementChannel;
import me.thesquadmc.networking.redis.channels.StaffChatChannels;
import me.thesquadmc.networking.redis.channels.WhitelistChannel;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.objects.logging.chatlogs.LogSaveTask;
import me.thesquadmc.objects.logging.chatlogs.LogUser;
import me.thesquadmc.utils.command.CommandHandler;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.enums.Settings;
import me.thesquadmc.utils.file.FileManager;
import me.thesquadmc.utils.handlers.UpdateHandler;
import me.thesquadmc.utils.inventory.builder.AbstractGUI;
import me.thesquadmc.utils.msgs.ServerType;
import me.thesquadmc.utils.nms.BarUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerState;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.uuid.UUIDTranslator;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class Main extends JavaPlugin {

    private UUIDTranslator uuidTranslator;

    private JedisPool pool;
    private static Main main;
    private LuckPermsApi luckPermsApi;
    private String whitelistMessage = ChatColor.translateAlternateColorCodes('&', "&cServer currently whitelisted!");
    private long startup = System.currentTimeMillis();
    private String value = "NONE";
    private String sig = "NONE";
    // private Jedis jedis;
    // private JedisPoolConfig poolConfig;
    private Jedis j;
    private DatabaseManager MySQL;
    private ThreadPoolExecutor threadPoolExecutor;
    private int restartTime = 0;

    private String version;
    private String serverType = "UNKNOWN";
    private int chatslow = 0;
    private boolean chatSilenced = false;
    private String serverState = ServerState.LOADING;

    private FileManager fileManager;
    private TempDataManager tempDataManager;
    private RedisHandler redisHandler;
    private FrozenInventory frozenInventory;
    private StaffmodeInventory staffmodeInventory;
    private UpdateHandler updateHandler;
    private HologramManager hologramManager;
    private NPCManager npcManager;
    private QueueManager queueManager;
    private BootManager bootManager;
    private CommandHandler commandHandler;
    private PartyManager partyManager;
    private MCLeaksAPI mcLeaksAPI;
    private CountManager countManager;
    private NMSAbstract nmsAbstract;
    private Mongo mongo;
    private UserDatabase userDatabase;

    private String host;
    private int port;
    private String password;
    private String mysqlhost;
    private String mysqlport;
    private String mysqlpassword;
    private String mysqldb;
    private String dbuser;

    private RedisManager redisManager;

    private Map<UUID, List<String>> friends = new HashMap<>();
    private Map<UUID, List<String>> requests = new HashMap<>();
    private Map<UUID, Map<Settings, Boolean>> settings = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("[NetworkTools] Starting the plugin up...");
        main = this;

        if (!setupNMSAbstract()) {
            getLogger().severe("This jar was not built for this server implementation!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("[NetworkTools] Server implementation set for " + nmsAbstract.getVersionMin() + " - " + nmsAbstract.getVersionMax());

        version = getDescription().getVersion();

        luckPermsApi = LuckPerms.getApi();
        mcLeaksAPI = MCLeaksAPI.builder().threadCount(2).expireAfter(10, TimeUnit.MINUTES).build();
        fileManager = new FileManager(this);
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        MySQL = new DatabaseManager(this, this);

        frozenInventory = new FrozenInventory(this);
        staffmodeInventory = new StaffmodeInventory(this);
        updateHandler = new UpdateHandler(this);
        fileManager.setup();
        updateHandler.run();
        tempDataManager = new TempDataManager();
        hologramManager = new HologramManager();
        npcManager = new NPCManager();
        queueManager = new QueueManager();
        bootManager = new BootManager();
        commandHandler = new CommandHandler(this);
        partyManager = new PartyManager();
        countManager = new CountManager();
        AbstractGUI.initializeListeners(this);

        host = fileManager.getNetworkingConfig().getString("redis.host");
        port = fileManager.getNetworkingConfig().getInt("redis.port");
        password = fileManager.getNetworkingConfig().getString("redis.password");
        mysqlhost = fileManager.getNetworkingConfig().getString("mysql.host");
        mysqlport = fileManager.getNetworkingConfig().getString("mysql.port");
        mysqlpassword = fileManager.getNetworkingConfig().getString("mysql.dbpassword");
        mysqldb = fileManager.getNetworkingConfig().getString("mysql.dbname");
        dbuser = fileManager.getNetworkingConfig().getString("mysql.dbuser");
        getLogger().info("[NetworkTools] Loading Redis PUB/SUB...");
        redisHandler = new RedisHandler(this);

        uuidTranslator = new UUIDTranslator(this);
        /*try {
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(main.getClassLoader());
            poolConfig = new JedisPoolConfig();
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMinIdle(20);
            poolConfig.setMaxIdle(150);
            poolConfig.setMaxTotal(150);
            pool = new JedisPool(poolConfig, host, port, 40 * 1000, password);
            //pool = new JedisPool(poolConfig, host, port, 40*1000);
            jedis = pool.getResource();
            Thread.currentThread().setContextClassLoader(previous);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Multithreading.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    j = new Jedis(host, port, 40 * 1000);
                    j.auth(password);
                    //j = new Jedis(host, port);
                    j.connect();
                    j.subscribe(new JedisPubSub() {
                                    @Override
                                    public void onMessage(String channel, String message) {
                                        JedisTask task = gson.fromJson(message, JedisTask.class);
                                        getRedisHandler().processRedisMessage(task, channel, message);
                                    }
                                },
                            RedisChannels.ADMINCHAT.getChannelName(),
                            RedisChannels.REQUEST_LIST.getChannelName(),
                            RedisChannels.RETURN_REQUEST_LIST.getChannelName(),
                            RedisChannels.STAFFCHAT.getChannelName(),
                            RedisChannels.MANAGERCHAT.getChannelName(),
                            RedisChannels.FIND.getChannelName(),
                            RedisChannels.FOUND.getChannelName(),
                            RedisChannels.ANNOUNCEMENT.getChannelName(),
                            RedisChannels.STOP.getChannelName(),
                            RedisChannels.WHITELIST.getChannelName(),
                            RedisChannels.WHITELIST_ADD.getChannelName(),
                            RedisChannels.WHITELIST_REMOVE.getChannelName(),
                            RedisChannels.REPORTS.getChannelName(),
                            RedisChannels.CLOSED_REPORTS.getChannelName(),
                            RedisChannels.MONITOR_INFO.getChannelName(),
                            RedisChannels.PROXY_RETURN.getChannelName(),
                            RedisChannels.MONITOR_REQUEST.getChannelName(),
                            RedisChannels.HEARTBEAT.getChannelName(),
                            RedisChannels.FRIEND_ADD.getChannelName(),
                            RedisChannels.FRIEND_REMOVE_INBOUND.getChannelName(),
                            RedisChannels.FRIEND_REMOVE_OUTBOUND.getChannelName(),
                            RedisChannels.FRIEND_CHAT.getChannelName(),
                            RedisChannels.FRIEND_CHECK_REQUEST.getChannelName(),
                            RedisChannels.FRIEND_RETURN_REQUEST.getChannelName(),
                            RedisChannels.PARTY_JOIN_SERVER.getChannelName(),
                            RedisChannels.PARTY_UPDATE.getChannelName(),
                            RedisChannels.PARTY_DISBAND.getChannelName(),
                            RedisChannels.LEAVE.getChannelName(),
                            RedisChannels.LOGIN.getChannelName(),
                            RedisChannels.RETURN_SERVER.getChannelName(),
                            RedisChannels.STARTUP_REQUEST.getChannelName(),
                            RedisChannels.SERVER_STATE.getChannelName(),
                            RedisChannels.PLAYER_COUNT.getChannelName(),
                            RedisChannels.DISCORD_STAFFCHAT_SERVER.getChannelName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        redisManager = new RedisManager(host, port, password);
        redisManager.registerChannel(new FindChannel(this), RedisChannels.FIND, RedisChannels.FOUND, RedisChannels.REQUEST_LIST, RedisChannels.RETURN_REQUEST_LIST);
        redisManager.registerChannel(new ServerManagementChannel(this), RedisChannels.STARTUP_REQUEST, RedisChannels.PLAYER_COUNT, RedisChannels.RETURN_SERVER, RedisChannels.STOP);
        redisManager.registerChannel(new WhitelistChannel(this), RedisChannels.WHITELIST, RedisChannels.WHITELIST_ADD, RedisChannels.WHITELIST_REMOVE);
        redisManager.registerChannel(new PartyChannel(this), RedisChannels.PARTY_JOIN_SERVER, RedisChannels.PARTY_DISBAND, RedisChannels.PARTY_UPDATE);
        redisManager.registerChannel(new MonitorChannel(this), RedisChannels.MONITOR_INFO, RedisChannels.MONITOR_REQUEST);
        redisManager.registerChannel(new AnnounceChannel(this), RedisChannels.ANNOUNCEMENT);
        //redisManager.registerChannel(new FriendsChannel(this), RedisChannels.LEAVE);
        redisManager.registerChannel(new MessageChannel(this), RedisChannels.MESSAGE);
        redisManager.registerChannel(new StaffChatChannels(this), RedisChannels.STAFFCHAT, RedisChannels.ADMINCHAT, RedisChannels.MANAGERCHAT, RedisChannels.DISCORD_STAFFCHAT_SERVER);

        getLogger().info("[NetworkTools] Redis PUB/SUB setup!");
        Multithreading.runAsync(() -> {
            getLogger().info("[NetworkTools] Connecting to mysql userDatabase...");
            try {
                MySQL.setupDB();
                getLogger().info("[NetworkTools] Connected to mysql userDatabase!");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                getLogger().severe("[NetworkTools] Unable to connect to mysql userDatabase!");
            }
        });

        getLogger().info("[NetworkTools] Setting up BuycraftAPI...");
        Multithreading.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    //Buycraft buycraft = new Buycraft(fileManager.getNetworkingConfig().getString("buycraft.secret"));
                    getLogger().info("[NetworkTools] BuycraftAPI setup and ready to go!");
                } catch (Exception e) {
                    e.printStackTrace();
                    getLogger().severe("[NetworkTools] Unable to set up BuycraftAPI");
                }
            }
        });

        Configuration conf = fileManager.getNetworkingConfig();
        String user = conf.getString("mongo.user");
        String db = conf.getString("mongo.database");
        String host = conf.getString("mongo.host");
        String password = conf.getString("mongo.password");
        int port = conf.getInt("mongo.port");

        getLogger().info(user);
        getLogger().info(db);
        getLogger().info(host);
        getLogger().info(password);

        mongo = new Mongo(user, db, password, host, port);
        userDatabase = new MongoUserDatabase(mongo);
        getLogger().info("[NetworkTools] Setup MongoDB connection!");

        registerListeners();
        registerCommands();

        ServerUtils.calculateServerType();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            redisManager.sendMessage(RedisChannels.PLAYER_COUNT, RedisMesage.newMessage()
                    .set(RedisArg.SERVER.getName(), Bukkit.getServerName())
                    .set(RedisArg.COUNT.getName(), Bukkit.getOnlinePlayers().size()));
            /*
            Multithreading.runAsync(new Runnable() {
                @Override
                public void run() {
                    try (Jedis jedis = Main.getMain().getPool().getResource()) {
                        JedisTask.withName(UUID.randomUUID().toString())
                                .withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
                                .withArg(RedisArg.COUNT.getArg(), Bukkit.getOnlinePlayers().size())
                                .send(RedisChannels.PLAYER_COUNT.getChannelName(), jedis);
                    }
                }
            });*/

        }, 20L, 20L);

        if (serverType.startsWith(ServerType.MINIGAME_HUB)) {
            redisManager.sendMessage(RedisChannels.STARTUP_REQUEST, RedisMesage.newMessage()
                    .set(RedisArg.SERVER, "ALL"));
            /*
            Multithreading.runAsync(new Runnable() {
                @Override
                public void run() {
                    try (Jedis jedis = Main.getMain().getPool().getResource()) {
                        JedisTask.withName(UUID.randomUUID().toString())
                                .withArg(RedisArg.SERVER.getArg(), "ALL")
                                .send(RedisChannels.STARTUP_REQUEST.getChannelName(), jedis);
                    }
                }
            }); */
        }

        new LogSaveTask(this).runTaskTimerAsynchronously(this, 20L * 60L * 5L, 20L * 60L * 5L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> BarUtils.getPlayers().forEach(nmsAbstract.getBossBarManager()::teleportBar), 1, 20L);

        Stream.of(
                new GamemodeCommand(),
                new NoteCommand(this),
                new ChangeLogCommand(this),
                new Find2Command(this),
                new MessageCommand(this),
                new EssentialCommands(),
                new TeleportCommand(),
                new HealCommand()
        ).forEach(o -> commandHandler.registerCommands(o));
        commandHandler.registerHelp();

        getLogger().info("[NetworkTools] Plugin started up and ready to go!");
    }

    @Override
    public void onDisable() {
        List<Document> updateDocuments = LogUser.toDocuments();

        if (!updateDocuments.isEmpty()) {
            MongoCollection<Document> collection = mongo.getMongoDatabase().getCollection("playerLogs");
            collection.insertMany(updateDocuments); //Running this sync to ensure it is completed
            return;
        }

        getLogger().info("[NetworkTools] Shutting down...");
        getLogger().info("[NetworkTools] Shut down! Cya :D");

        redisManager.close();
    }

    public UUIDTranslator getUUIDTranslator() {
        return uuidTranslator;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public UserDatabase getMongoDatabase() {
        return userDatabase;
    }

    public CountManager getCountManager() {
        return countManager;
    }

    public MCLeaksAPI getMcLeaksAPI() {
        return mcLeaksAPI;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public BootManager getBootManager() {
        return bootManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerType() {
        return serverType;
    }

    public String getVersion() {
        return version;
    }

    public int getRestartTime() {
        return restartTime;
    }

    public void setRestartTime(int restartTime) {
        this.restartTime = restartTime;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public String getServerState() {
        return serverState;
    }

    public void setServerState(String serverState) {
        this.serverState = serverState;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public DatabaseManager getMySQL() {
        return MySQL;
    }

    public String getMysqlhost() {
        return mysqlhost;
    }

    public String getMysqlport() {
        return mysqlport;
    }

    public String getMysqlpassword() {
        return mysqlpassword;
    }

    public String getMysqldb() {
        return mysqldb;
    }

    public String getDbuser() {
        return dbuser;
    }

    /**
     * @deprecated Legacy API. See {@link TSMCUser#getFriends()}
     */
    public Map<UUID, List<String>> getFriends() {
        return friends;
    }

    /**
     * @deprecated Legacy API. See {@link TSMCUser#getRequests()}
     */
    @Deprecated
    public Map<UUID, List<String>> getRequests() {
        return requests;
    }

    /**
     * @deprecated Legacy API. See {@link TSMCUser#getSetting(me.thesquadmc.objects.PlayerSetting)}
     */
    @Deprecated
    public Map<UUID, Map<Settings, Boolean>> getSettings() {
        return settings;
    }

    //  public Jedis getJedis() {
    //      return jedis;
    // }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public int getChatslow() {
        return chatslow;
    }

    public void setChatslow(int chatslow) {
        this.chatslow = chatslow;
    }

    public boolean isChatSilenced() {
        return chatSilenced;
    }

    public void setChatSilenced(boolean chatSilenced) {
        this.chatSilenced = chatSilenced;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public long getStartup() {
        return startup;
    }

    public void setStartup(long startup) {
        this.startup = startup;
    }

    public void setWhitelistMessage(String whitelistMessage) {
        this.whitelistMessage = whitelistMessage;
    }

    public String getWhitelistMessage() {
        return whitelistMessage;
    }


    public StaffmodeInventory getStaffmodeInventory() {
        return staffmodeInventory;
    }

    public FrozenInventory getFrozenInventory() {
        return frozenInventory;
    }

    public RedisHandler getRedisHandler() {
        return redisHandler;
    }

    public LuckPermsApi getLuckPermsApi() {
        return luckPermsApi;
    }

    /**
     * @deprecated Legacy API. See {@link TSMCUser}
     */
    @Deprecated
    public TempDataManager getTempDataManager() {
        return tempDataManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public static Main getMain() {
        return main;
    }

    public JedisPool getPool() {
        return pool;
    }

    /**
     * Get an instance of the NMSAbstract class for this server version implementation
     *
     * @return the related NMSAbstract instance
     */
    public NMSAbstract getNMSAbstract() {
        return nmsAbstract;
    }

    private boolean setupNMSAbstract() {
        try (JarFile jarFile = new JarFile(getFile())) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName().replace("/", ".");

                if (!entryName.startsWith(Main.class.getPackage().getName())) continue;
                entryName = entryName.replace(".class", ""); // Remove the .class file extension
                if (org.apache.commons.lang.StringUtils.countMatches(entryName, ".") > 2) continue;

                Class<?> clazz;
                try {
                    clazz = Class.forName(entryName);
                } catch (ClassNotFoundException e) {
                    continue;
                }

                if (!clazz.isAnnotationPresent(AbstractionModule.class))
                    continue; // Not an abstraction module... ignore

                Object moduleInstance = clazz.newInstance();
                Method[] methods = clazz.getDeclaredMethods();

                if (methods.length != 1) {
                    throw new IllegalStateException("Abstraction modules should only have 1 method present!");
                }

                Method method = methods[0];
                if (method.getReturnType() != NMSAbstract.class) {
                    throw new IllegalStateException("Abstraction module methods must return me.thesquadmc.abstraction.NMSAbstract");
                }
                if (method.getParameterTypes().length != 0) {
                    throw new IllegalStateException("Abstraction module methods must not have any parameters");
                }

                this.nmsAbstract = (NMSAbstract) method.invoke(moduleInstance);
                break;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            this.getLogger().info("Abstraction modules must not have any constructor parameters");
            e.printStackTrace();
        } catch (IOException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return nmsAbstract != null;
    }

    private void registerListeners() {
        Stream.of(
                new ConnectionListeners(this),
                new FilterListener(this),
                new ForceFieldListeners(this),
                new FreezeListener(this),
                new LaunchListener(),
                new LightningListener(),
                new ServerListener(),
                new StaffmodeListener(this),
                new TimedListener(this),
                new VanishListener(),
                new WhitelistListener(this),
                new XrayListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerCommands() {
        getCommand("staffchat").setExecutor(new StaffChatCommand(this));
        getCommand("adminchat").setExecutor(new AdminChatCommand(this));
        getCommand("managerchat").setExecutor(new ManagerChatCommand(this));
        getCommand("find").setExecutor(new FindCommand(this));
        getCommand("lookup").setExecutor(new LookupCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("unfreeze").setExecutor(new UnFreezeCommand(this));
        getCommand("freezepanel").setExecutor(new FreezePanelCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("randomtp").setExecutor(new RandomTPCommand(this));
        getCommand("staffmode").setExecutor(new StaffmodeCommand());
        getCommand("staff").setExecutor(new StafflistCommand(this));
        getCommand("xray").setExecutor(new XrayVerboseCommand());
        getCommand("alert").setExecutor(new AlertCommand(this));
        getCommand("stop").setExecutor(new StopCommand(this));
        getCommand("whitelist").setExecutor(new WhitelistCommand(this));
        getCommand("launch").setExecutor(new LaunchCommand(this));
        getCommand("ytvanish").setExecutor(new YtVanishCommand());
        getCommand("forcefield").setExecutor(new ForceFieldCommand());
        getCommand("staffmenu").setExecutor(new StaffMenuCommand(this));
        getCommand("monitor").setExecutor(new MonitorCommand());
        getCommand("ytnick").setExecutor(new YtNickCommand());
        getCommand("disguiseplayer").setExecutor(new DisguisePlayerCommand(this));
        getCommand("undisguiseplayer").setExecutor(new UndisguisePlayerCommand(this));
        getCommand("silence").setExecutor(new ChatSilenceCommand(this));
        getCommand("slowchat").setExecutor(new ChatSlowCommand(this));
        getCommand("smite").setExecutor(new SmiteCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("status").setExecutor(new StatusCommand(this));
        getCommand("proxytransport").setExecutor(new ProxyTransportCommand(this));
        getCommand("restarttime").setExecutor(new RestartTimeCommand(this));
        getCommand("apply").setExecutor(new ApplyCommand(this));
        getCommand("discord").setExecutor(new DiscordCommand(this));
        getCommand("store").setExecutor(new StoreCommand(this));
        getCommand("website").setExecutor(new WebsiteCommand(this));
        getCommand("uniqueplayers").setExecutor(new UniquePlayersCommand(this));
        getCommand("title").setExecutor(new TitleCommand(this));
        getCommand("queuerestart").setExecutor(new QueueRestartCommand(this));
        getCommand("vanishlist").setExecutor(new VanishListCommand());
        getCommand("ntversion").setExecutor(new NTVersionCommand(this));
        getCommand("mg").setExecutor(new MGCommand(this));
        getCommand("queuemanager").setExecutor(new QueueManagerCommand(this));
        getCommand("staffalert").setExecutor(new StaffAlertCommand(this));
        getCommand("onlinecount").setExecutor(new OnlineCountCommand(this));
        getCommand("logs").setExecutor(new LogsCommand(this));
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("l1").setExecutor(new MOTDCommand(main, 1));
        getCommand("l2").setExecutor(new MOTDCommand(main, 2));
        getCommand("motdclear").setExecutor(new MOTDClearCommand(main));
    }
}
