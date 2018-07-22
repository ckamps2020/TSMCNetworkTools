package me.thesquadmc;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.cloud.CloudExpansion;
import me.gong.mcleaks.MCLeaksAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.thesquadmc.abstraction.AbstractionModule;
import me.thesquadmc.abstraction.NMSAbstract;
import me.thesquadmc.chat.ChatManager;
import me.thesquadmc.commands.AdminChatCommand;
import me.thesquadmc.commands.AlertCommand;
import me.thesquadmc.commands.ApplyCommand;
import me.thesquadmc.commands.ChangeLogCommand;
import me.thesquadmc.commands.ChatSilenceCommand;
import me.thesquadmc.commands.ChatSlowCommand;
import me.thesquadmc.commands.DiscordCommand;
import me.thesquadmc.commands.DisguisePlayerCommand;
import me.thesquadmc.commands.EssentialCommands;
import me.thesquadmc.commands.FindCommand;
import me.thesquadmc.commands.ForceFieldCommand;
import me.thesquadmc.commands.FreezeCommand;
import me.thesquadmc.commands.FreezePanelCommand;
import me.thesquadmc.commands.GamemodeCommand;
import me.thesquadmc.commands.HealCommand;
import me.thesquadmc.commands.InvseeCommand;
import me.thesquadmc.commands.LaunchCommand;
import me.thesquadmc.commands.LookupCommand;
import me.thesquadmc.commands.ManagerChatCommand;
import me.thesquadmc.commands.MessageCommand;
import me.thesquadmc.commands.MonitorCommand;
import me.thesquadmc.commands.NTVersionCommand;
import me.thesquadmc.commands.NoteCommand;
import me.thesquadmc.commands.OnlineCountCommand;
import me.thesquadmc.commands.PartyCommand;
import me.thesquadmc.commands.PingCommand;
import me.thesquadmc.commands.ProxyTransportCommand;
import me.thesquadmc.commands.RandomTPCommand;
import me.thesquadmc.commands.RestartTimeCommand;
import me.thesquadmc.commands.SmiteCommand;
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
import me.thesquadmc.commands.VanishCommand;
import me.thesquadmc.commands.VanishListCommand;
import me.thesquadmc.commands.WarpCommand;
import me.thesquadmc.commands.WebsiteCommand;
import me.thesquadmc.commands.WhitelistCommand;
import me.thesquadmc.commands.XrayVerboseCommand;
import me.thesquadmc.commands.YtNickCommand;
import me.thesquadmc.commands.YtVanishCommand;
import me.thesquadmc.inventories.FrozenInventory;
import me.thesquadmc.inventories.StaffmodeInventory;
import me.thesquadmc.listeners.ConnectionListeners;
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
import me.thesquadmc.managers.ClickableMessageManager;
import me.thesquadmc.managers.CountManager;
import me.thesquadmc.managers.HologramManager;
import me.thesquadmc.managers.ItemManager;
import me.thesquadmc.managers.NPCManager;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.networking.mongo.MongoManager;
import me.thesquadmc.networking.mongo.MongoUserDatabase;
import me.thesquadmc.networking.mongo.UserDatabase;
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
import me.thesquadmc.player.local.LocalPlayerManager;
import me.thesquadmc.utils.command.CommandHandler;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.file.FileManager;
import me.thesquadmc.utils.handlers.UpdateHandler;
import me.thesquadmc.utils.inventory.builder.AbstractGUI;
import me.thesquadmc.utils.nms.BarUtils;
import me.thesquadmc.utils.player.uuid.UUIDTranslator;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerState;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.warp.WarpManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
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

    private UUIDTranslator uuidTranslator;

    private static NetworkTools networkTools;
    private LuckPermsApi luckPermsApi;
    private String whitelistMessage = ChatColor.translateAlternateColorCodes('&', "&cServer currently whitelisted!");
    private long startup = System.currentTimeMillis();
    private String value = "NONE";
    private String sig = "NONE";
    private int restartTime = 0;

    private String version;
    private String serverType = "UNKNOWN";
    private String serverState = ServerState.LOADING;

    private Chat vaultChat;
    private Permission vaultPermissions;
    private Economy vaultEconomy;

    private FileManager fileManager;
    private LocalPlayerManager localPlayerManager;
    private WarpManager warpManager;
    private ChatManager chatManager;
    private FrozenInventory frozenInventory;
    private StaffmodeInventory staffmodeInventory;
    private HologramManager hologramManager;
    private NPCManager npcManager;
    private CommandHandler commandHandler;
    private PartyManager partyManager;
    private MCLeaksAPI mcLeaksAPI;
    private CountManager countManager;
    private ClickableMessageManager clickableMessageManager;
    private NMSAbstract nmsAbstract;
    private MongoManager mongoManager;
    private UserDatabase userDatabase;
    private ItemManager itemManager;

    private RedisManager redisManager;

    public static NetworkTools getInstance() {
        return networkTools;
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting the plugin up...");

        networkTools = this;
        version = getDescription().getVersion();

        if (!setupNMSAbstract()) {
            getLogger().severe("This jar was not built for this server implementation!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Server implementation set for " + nmsAbstract.getVersionMin() + " - " + nmsAbstract.getVersionMax());

        luckPermsApi = LuckPerms.getApi();
        mcLeaksAPI = MCLeaksAPI.builder().threadCount(2).expireAfter(10, TimeUnit.MINUTES).build();

        setupEconomy();
        setupChat();
        setupPermissions();

        frozenInventory = new FrozenInventory();
        staffmodeInventory = new StaffmodeInventory();

        UpdateHandler updateHandler = new UpdateHandler(this);
        updateHandler.run();

        localPlayerManager = new LocalPlayerManager();
        warpManager = new WarpManager(this);
        chatManager = new ChatManager(this);
        hologramManager = new HologramManager();
        npcManager = new NPCManager();
        commandHandler = new CommandHandler(this);
        partyManager = new PartyManager();
        countManager = new CountManager();
        clickableMessageManager = new ClickableMessageManager(this);
        itemManager = new ItemManager();
        fileManager = new FileManager(this);
        fileManager.setup();

        AbstractGUI.initializeListeners(this);

        String host1 = fileManager.getNetworkingConfig().getString("redis.host");
        int port1 = fileManager.getNetworkingConfig().getInt("redis.port");
        String password1 = fileManager.getNetworkingConfig().getString("redis.password");
        getLogger().info("Loading Redis PUB/SUB...");

        uuidTranslator = new UUIDTranslator(this);

        redisManager = new RedisManager(host1, port1, password1);
        redisManager.registerChannel(new FindChannel(this), RedisChannels.FIND, RedisChannels.FOUND, RedisChannels.REQUEST_LIST, RedisChannels.RETURN_REQUEST_LIST);
        redisManager.registerChannel(new ServerManagementChannel(this), RedisChannels.STARTUP_REQUEST, RedisChannels.PLAYER_COUNT, RedisChannels.RETURN_SERVER, RedisChannels.STOP);
        redisManager.registerChannel(new WhitelistChannel(this), RedisChannels.WHITELIST, RedisChannels.WHITELIST_ADD, RedisChannels.WHITELIST_REMOVE);
        redisManager.registerChannel(new PartyChannel(), RedisChannels.PARTY_JOIN_SERVER, RedisChannels.PARTY_DISBAND, RedisChannels.PARTY_UPDATE);
        redisManager.registerChannel(new MonitorChannel(this), RedisChannels.MONITOR_INFO, RedisChannels.MONITOR_REQUEST);
        redisManager.registerChannel(new AnnounceChannel(), RedisChannels.ANNOUNCEMENT);
        //redisManager.registerChannel(new FriendsChannel(this), RedisChannels.LEAVE);
        redisManager.registerChannel(new MessageChannel(), RedisChannels.MESSAGE);
        redisManager.registerChannel(new StaffChatChannels(), RedisChannels.STAFFCHAT, RedisChannels.ADMINCHAT, RedisChannels.MANAGERCHAT, RedisChannels.DISCORD_STAFFCHAT_SERVER);

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
        String user = conf.getString("mongoManager.user");
        String db = conf.getString("mongoManager.database");
        String host = conf.getString("mongoManager.host");
        String password = conf.getString("mongoManager.password");
        int port = conf.getInt("mongoManager.port");

        mongoManager = new MongoManager(user, db, password, host, port);
        userDatabase = new MongoUserDatabase(mongoManager);
        getLogger().info("Setup MongoDB connection!");

        registerListeners();
        registerCommands();

        ServerUtils.calculateServerType();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> redisManager.sendMessage(RedisChannels.PLAYER_COUNT, RedisMesage.newMessage()
                .set(RedisArg.SERVER.getName(), Bukkit.getServerName())
                .set(RedisArg.COUNT.getName(), Bukkit.getOnlinePlayers().size())), 20L, 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> BarUtils.getPlayers().forEach(nmsAbstract.getBossBarManager()::teleportBar), 1, 20L);

        Stream.of(
                new GamemodeCommand(),
                new NoteCommand(this),
                new WarpCommand(this),
                new ChangeLogCommand(this),
                new FindCommand(this),
                new MessageCommand(this),
                new EssentialCommands(this),
                new TeleportCommand(this),
                new HealCommand()
        ).forEach(o -> commandHandler.registerCommands(o));

        PlaceholderAPIPlugin plugin = PlaceholderAPIPlugin.getInstance();
        CloudExpansion playerExpansion = plugin.getExpansionCloud().getCloudExpansion("Player");
        if (playerExpansion != null) {
            plugin.getExpansionCloud().downloadExpansion(null, playerExpansion, playerExpansion.getLatestVersion());
        }

        getLogger().info("Plugin started up and ready to go!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down...");
        warpManager.saveWarps();
        redisManager.close();
        getLogger().info("Shut down! Cya :D");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        vaultEconomy = rsp.getProvider();
        return vaultEconomy != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null) {
            return false;
        }

        vaultChat = rsp.getProvider();
        return vaultChat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }

        vaultPermissions = rsp.getProvider();
        return vaultPermissions != null;
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

    public Chat getVaultChat() {
        return vaultChat;
    }

    public Permission getVaultPermissions() {
        return vaultPermissions;
    }

    public Economy getVaultEconomy() {
        return vaultEconomy;
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

    public PartyManager getPartyManager() {
        return partyManager;
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

                if (!entryName.startsWith(NetworkTools.class.getPackage().getName())) continue;
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
                //new FilterListener(this),
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
        getCommand("lookup").setExecutor(new LookupCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("unfreeze").setExecutor(new UnFreezeCommand(this));
        getCommand("freezepanel").setExecutor(new FreezePanelCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("randomtp").setExecutor(new RandomTPCommand());
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
        getCommand("party").setExecutor(new PartyCommand(this));
    }
}
