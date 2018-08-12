package com.thesquadmc.networktools;

import com.sgtcaze.nametagedit.NametagEdit;
import com.thesquadmc.networktools.abstraction.AbstractionModule;
import com.thesquadmc.networktools.abstraction.NMSAbstract;
import com.thesquadmc.networktools.chat.ChatManager;
import com.thesquadmc.networktools.commands.AdminChatCommand;
import com.thesquadmc.networktools.commands.AlertCommand;
import com.thesquadmc.networktools.commands.ApplyCommand;
import com.thesquadmc.networktools.commands.ChangeLogCommand;
import com.thesquadmc.networktools.commands.ChatSilenceCommand;
import com.thesquadmc.networktools.commands.ChatSlowCommand;
import com.thesquadmc.networktools.commands.DiscordCommand;
import com.thesquadmc.networktools.commands.DisguisePlayerCommand;
import com.thesquadmc.networktools.commands.EssentialCommands;
import com.thesquadmc.networktools.commands.FindCommand;
import com.thesquadmc.networktools.commands.ForceFieldCommand;
import com.thesquadmc.networktools.commands.FreezeCommand;
import com.thesquadmc.networktools.commands.FreezePanelCommand;
import com.thesquadmc.networktools.commands.GamemodeCommand;
import com.thesquadmc.networktools.commands.HealCommand;
import com.thesquadmc.networktools.commands.HomeCommand;
import com.thesquadmc.networktools.commands.IgnoreCommand;
import com.thesquadmc.networktools.commands.InvseeCommand;
import com.thesquadmc.networktools.commands.KitCommand;
import com.thesquadmc.networktools.commands.LaunchCommand;
import com.thesquadmc.networktools.commands.LookupCommand;
import com.thesquadmc.networktools.commands.ManagerChatCommand;
import com.thesquadmc.networktools.commands.MessageCommand;
import com.thesquadmc.networktools.commands.MonitorCommand;
import com.thesquadmc.networktools.commands.NTVersionCommand;
import com.thesquadmc.networktools.commands.NoteCommand;
import com.thesquadmc.networktools.commands.OnlineCountCommand;
import com.thesquadmc.networktools.commands.PingCommand;
import com.thesquadmc.networktools.commands.ProxyTransportCommand;
import com.thesquadmc.networktools.commands.RandomTPCommand;
import com.thesquadmc.networktools.commands.RestartTimeCommand;
import com.thesquadmc.networktools.commands.SmiteCommand;
import com.thesquadmc.networktools.commands.StaffChatCommand;
import com.thesquadmc.networktools.commands.StaffMenuCommand;
import com.thesquadmc.networktools.commands.StafflistCommand;
import com.thesquadmc.networktools.commands.StaffmodeCommand;
import com.thesquadmc.networktools.commands.StatusCommand;
import com.thesquadmc.networktools.commands.StopCommand;
import com.thesquadmc.networktools.commands.StoreCommand;
import com.thesquadmc.networktools.commands.TeleportCommand;
import com.thesquadmc.networktools.commands.TitleCommand;
import com.thesquadmc.networktools.commands.UnFreezeCommand;
import com.thesquadmc.networktools.commands.UndisguisePlayerCommand;
import com.thesquadmc.networktools.commands.VanishCommand;
import com.thesquadmc.networktools.commands.VanishListCommand;
import com.thesquadmc.networktools.commands.WarpCommand;
import com.thesquadmc.networktools.commands.WebsiteCommand;
import com.thesquadmc.networktools.commands.WhitelistCommand;
import com.thesquadmc.networktools.commands.XrayVerboseCommand;
import com.thesquadmc.networktools.commands.YtNickCommand;
import com.thesquadmc.networktools.commands.YtVanishCommand;
import com.thesquadmc.networktools.inventories.FrozenInventory;
import com.thesquadmc.networktools.inventories.StaffmodeInventory;
import com.thesquadmc.networktools.kit.KitManager;
import com.thesquadmc.networktools.listeners.ConnectionListeners;
import com.thesquadmc.networktools.listeners.ForceFieldListeners;
import com.thesquadmc.networktools.listeners.FreezeListener;
import com.thesquadmc.networktools.listeners.LaunchListener;
import com.thesquadmc.networktools.listeners.LightningListener;
import com.thesquadmc.networktools.listeners.ServerListener;
import com.thesquadmc.networktools.listeners.SignListener;
import com.thesquadmc.networktools.listeners.StaffmodeListener;
import com.thesquadmc.networktools.listeners.TimedListener;
import com.thesquadmc.networktools.listeners.VanishListener;
import com.thesquadmc.networktools.listeners.WhitelistListener;
import com.thesquadmc.networktools.listeners.XrayListener;
import com.thesquadmc.networktools.managers.ClickableMessageManager;
import com.thesquadmc.networktools.managers.CountManager;
import com.thesquadmc.networktools.managers.HologramManager;
import com.thesquadmc.networktools.managers.ItemManager;
import com.thesquadmc.networktools.managers.NPCManager;
import com.thesquadmc.networktools.networking.mongo.MongoManager;
import com.thesquadmc.networktools.networking.mongo.MongoUserDatabase;
import com.thesquadmc.networktools.networking.mongo.UserDatabase;
import com.thesquadmc.networktools.networking.redis.RedisManager;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.networking.redis.channels.AnnounceChannel;
import com.thesquadmc.networktools.networking.redis.channels.FindChannel;
import com.thesquadmc.networktools.networking.redis.channels.MessageChannel;
import com.thesquadmc.networktools.networking.redis.channels.MonitorChannel;
import com.thesquadmc.networktools.networking.redis.channels.ServerManagementChannel;
import com.thesquadmc.networktools.networking.redis.channels.StaffChatChannels;
import com.thesquadmc.networktools.networking.redis.channels.WhitelistChannel;
import com.thesquadmc.networktools.player.local.LocalPlayerManager;
import com.thesquadmc.networktools.player.stats.ServerStatsListener;
import com.thesquadmc.networktools.utils.command.CommandHandler;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.file.FileManager;
import com.thesquadmc.networktools.utils.handlers.UpdateHandler;
import com.thesquadmc.networktools.utils.inventory.builder.AbstractGUI;
import com.thesquadmc.networktools.utils.nms.BarUtils;
import com.thesquadmc.networktools.utils.player.uuid.UUIDTranslator;
import com.thesquadmc.networktools.utils.server.Multithreading;
import com.thesquadmc.networktools.utils.server.ServerState;
import com.thesquadmc.networktools.utils.server.ServerType;
import com.thesquadmc.networktools.warp.WarpManager;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.cloud.CloudExpansion;
import me.gong.mcleaks.MCLeaksAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class NetworkTools extends JavaPlugin {

    private static NetworkTools networkTools;

    private UUIDTranslator uuidTranslator;
    private LuckPermsApi luckPermsApi;
    private String whitelistMessage = ChatColor.translateAlternateColorCodes('&', "&cServer currently whitelisted!");
    private long startup = System.currentTimeMillis();
    private String value = "NONE";
    private String sig = "NONE";
    private int restartTime = 0;

    private String currentSeason;
    private String version;
    private ServerType serverType;
    private String serverState = ServerState.LOADING;

    private Permission vaultPermissions;

    private FileManager fileManager;
    private LocalPlayerManager localPlayerManager;
    private WarpManager warpManager;
    private ChatManager chatManager;
    private FrozenInventory frozenInventory;
    private StaffmodeInventory staffmodeInventory;
    private HologramManager hologramManager;
    private NPCManager npcManager;
    private CommandHandler commandHandler;
    private MCLeaksAPI mcLeaksAPI;
    private CountManager countManager;
    private ClickableMessageManager clickableMessageManager;
    private NMSAbstract nmsAbstract;
    private MongoManager mongoManager;
    private UserDatabase userDatabase;
    private KitManager kitManager;
    private ItemManager itemManager;
    private NametagEdit nametagEdit;

    private RedisManager redisManager;

    public static NetworkTools getInstance() {
        return networkTools;
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting the plugin up...");

        networkTools = this;

        serverType = ServerType.getServerType(Bukkit.getServerName().toUpperCase());
        version = getDescription().getVersion();

        if (!setupNMSAbstract()) {
            getLogger().severe("This jar was not built for this server implementation!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Server implementation set for " + nmsAbstract.getVersionMin() + " - " + nmsAbstract.getVersionMax());

        nametagEdit = new NametagEdit();
        nametagEdit.onEnable();

        if (!nametagEdit.getHandler().getConfig().contains("currentSeason")) {
            getLogger().severe("Disabling plugin due to no season being set.");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        luckPermsApi = LuckPerms.getApi();
        mcLeaksAPI = MCLeaksAPI.builder().threadCount(2).expireAfter(10, TimeUnit.MINUTES).build();

        setupPermissions();

        frozenInventory = new FrozenInventory();
        staffmodeInventory = new StaffmodeInventory();

        UpdateHandler updateHandler = new UpdateHandler(this);
        updateHandler.run();

        localPlayerManager = new LocalPlayerManager();
        kitManager = new KitManager(this);
        warpManager = new WarpManager(this);
        chatManager = new ChatManager(this);
        hologramManager = new HologramManager();
        npcManager = new NPCManager();
        commandHandler = new CommandHandler(this);
        countManager = new CountManager();
        clickableMessageManager = new ClickableMessageManager(this);
        itemManager = new ItemManager();
        fileManager = new FileManager(this);
        fileManager.setup();

        currentSeason = nametagEdit.getHandler().getConfig().getString("currentSeason", "season-1");

        AbstractGUI.initializeListeners(this);

        String host1 = fileManager.getNetworkingConfig().getString("redis.host");
        int port1 = fileManager.getNetworkingConfig().getInt("redis.port");
        String password1 = fileManager.getNetworkingConfig().getString("redis.password");
        getLogger().info("Loading Redis PUB/SUB...");

        uuidTranslator = new UUIDTranslator(this);

        redisManager = new RedisManager(host1, port1, password1);
        redisManager.registerChannel(new FindChannel(this), RedisChannels.REQUEST_LIST, RedisChannels.RETURN_REQUEST_LIST);
        redisManager.registerChannel(new ServerManagementChannel(this), RedisChannels.STARTUP_REQUEST, RedisChannels.PLAYER_COUNT, RedisChannels.RETURN_SERVER, RedisChannels.STOP);
        redisManager.registerChannel(new WhitelistChannel(this), RedisChannels.WHITELIST, RedisChannels.WHITELIST_ADD, RedisChannels.WHITELIST_REMOVE);
        redisManager.registerChannel(new MonitorChannel(this), RedisChannels.MONITOR_INFO, RedisChannels.MONITOR_REQUEST);
        redisManager.registerChannel(new AnnounceChannel(), RedisChannels.ANNOUNCEMENT);
        redisManager.registerChannel(new MessageChannel(this), RedisChannels.MESSAGE, RedisChannels.MESSAGE_RESPONSE, RedisChannels.NOTES);
        redisManager.registerChannel(new StaffChatChannels(), RedisChannels.STAFFCHAT, RedisChannels.ADMINCHAT, RedisChannels.MANAGERCHAT);

        getLogger().info("Redis PUB/SUB setup!");

        getLogger().info("Setting up BuycraftAPI...");
        Multithreading.runAsync(() -> {
            try {
                //Buycraft buycraft = new Buycraft(fileManager.getNetworkingConfig().getString("buycraft.secret"));
                getLogger().info("BuycraftAPI setup and ready to go!");
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("Unable to set up BuycraftAPI");
            }
        });

        Configuration conf = fileManager.getNetworkingConfig();
        String user = conf.getString("mongo.user");
        String db = conf.getString("mongo.database");
        String host = conf.getString("mongo.host");
        String password = conf.getString("mongo.password");
        int port = conf.getInt("mongo.port");

        mongoManager = new MongoManager(user, db, password, host, port);
        userDatabase = new MongoUserDatabase(mongoManager);
        getLogger().info("Setup MongoDB connection!");

        registerListeners();
        registerCommands();

        Stream.of(
                new GamemodeCommand(),
                new NoteCommand(this),
                new WarpCommand(this),
                new ChangeLogCommand(this),
                new FindCommand(this),
                new MessageCommand(this),
                new EssentialCommands(this),
                new TeleportCommand(this),
                new IgnoreCommand(this),
                new HomeCommand(this),
                new KitCommand(this),
                new HealCommand()
        ).forEach(o -> commandHandler.registerCommands(o));

        PlaceholderAPIPlugin plugin = PlaceholderAPIPlugin.getInstance();
        CloudExpansion playerExpansion = plugin.getExpansionCloud().getCloudExpansion("Player");
        if (playerExpansion != null) {
            plugin.getExpansionCloud().downloadExpansion(null, playerExpansion, playerExpansion.getLatestVersion());
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> redisManager.sendMessage(RedisChannels.PLAYER_COUNT, RedisMesage.newMessage()
                .set(RedisArg.SERVER, Bukkit.getServerName())
                .set(RedisArg.COUNT, Bukkit.getOnlinePlayers().size())), 20L, 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> BarUtils.getPlayers().forEach(nmsAbstract.getBossBarManager()::teleportBar), 1, 20L);
        getLogger().info("Plugin started up and ready to go!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down...");
        nametagEdit.onDisable();
        warpManager.saveWarps();
        kitManager.saveKits();
        redisManager.close();
        getLogger().info("Shut down! Cya :D");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }

        vaultPermissions = rsp.getProvider();
        return vaultPermissions != null;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public String getCurrentSeason() {
        return currentSeason;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public LocalPlayerManager getLocalPlayerManager() {
        return localPlayerManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public Permission getVaultPermissions() {
        return vaultPermissions;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public UUIDTranslator getUUIDTranslator() {
        return uuidTranslator;
    }

    public ClickableMessageManager getClickableMessageManager() {
        return clickableMessageManager;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    public CountManager getCountManager() {
        return countManager;
    }

    public MCLeaksAPI getMcLeaksAPI() {
        return mcLeaksAPI;
    }

    public ServerType getServerType() {
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

    public CommandHandler getCommandHandler() {
        return commandHandler;
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

    public String getWhitelistMessage() {
        return whitelistMessage;
    }

    public void setWhitelistMessage(String whitelistMessage) {
        this.whitelistMessage = whitelistMessage;
    }

    public StaffmodeInventory getStaffmodeInventory() {
        return staffmodeInventory;
    }

    public FrozenInventory getFrozenInventory() {
        return frozenInventory;
    }

    public LuckPermsApi getLuckPermsApi() {
        return luckPermsApi;
    }

    public FileManager getFileManager() {
        return fileManager;
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

                if (!entryName.startsWith(NetworkTools.class.getPackage().getName())) {
                    continue;
                }

                entryName = entryName.replace(".class", ""); // Remove the .class file extension
                if (org.apache.commons.lang.StringUtils.countMatches(entryName, ".") > 3) {
                    continue;
                }

                Class<?> clazz;
                try {
                    clazz = Class.forName(entryName);
                } catch (ClassNotFoundException e) {
                    continue;
                }

                if (!clazz.isAnnotationPresent(AbstractionModule.class)) {
                    continue; // Not an abstraction module... ignore
                }

                Object moduleInstance = clazz.newInstance();
                Method[] methods = clazz.getDeclaredMethods();

                if (methods.length != 1) {
                    throw new IllegalStateException("Abstraction modules should only have 1 method present!");
                }

                Method method = methods[0];
                if (method.getReturnType() != NMSAbstract.class) {
                    throw new IllegalStateException("Abstraction module methods must return com.thesquadmc.networktools.abstraction.NMSAbstract");
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
                new ForceFieldListeners(),
                new FreezeListener(this),
                new LaunchListener(),
                new LightningListener(),
                new ServerListener(),
                new StaffmodeListener(this),
                new TimedListener(this),
                new VanishListener(),
                new WhitelistListener(this),
                new ServerStatsListener(),
                new XrayListener(),
                new SignListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerCommands() {
        getCommand("staffchat").setExecutor(new StaffChatCommand(this));
        getCommand("adminchat").setExecutor(new AdminChatCommand(this));
        getCommand("managerchat").setExecutor(new ManagerChatCommand(this));
        getCommand("lookup").setExecutor(new LookupCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("unfreeze").setExecutor(new UnFreezeCommand(this));
        getCommand("freezepanel").setExecutor(new FreezePanelCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("randomtp").setExecutor(new RandomTPCommand());
        getCommand("staffmode").setExecutor(new StaffmodeCommand(this));
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
        getCommand("smite").setExecutor(new SmiteCommand());
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("status").setExecutor(new StatusCommand(this));
        getCommand("proxytransport").setExecutor(new ProxyTransportCommand());
        getCommand("restarttime").setExecutor(new RestartTimeCommand(this));
        getCommand("apply").setExecutor(new ApplyCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("store").setExecutor(new StoreCommand());
        getCommand("website").setExecutor(new WebsiteCommand());
        getCommand("title").setExecutor(new TitleCommand());
        getCommand("vanishlist").setExecutor(new VanishListCommand());
        getCommand("ntversion").setExecutor(new NTVersionCommand(this));
        getCommand("onlinecount").setExecutor(new OnlineCountCommand(this));
    }
}
