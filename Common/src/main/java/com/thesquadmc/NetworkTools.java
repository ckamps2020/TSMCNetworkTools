package com.thesquadmc;

import com.thesquadmc.abstraction.AbstractionModule;
import com.thesquadmc.abstraction.NMSAbstract;
import com.thesquadmc.chat.ChatManager;
import com.thesquadmc.commands.AdminChatCommand;
import com.thesquadmc.commands.AlertCommand;
import com.thesquadmc.commands.ApplyCommand;
import com.thesquadmc.commands.ChangeLogCommand;
import com.thesquadmc.commands.ChatSilenceCommand;
import com.thesquadmc.commands.ChatSlowCommand;
import com.thesquadmc.commands.DiscordCommand;
import com.thesquadmc.commands.DisguisePlayerCommand;
import com.thesquadmc.commands.EssentialCommands;
import com.thesquadmc.commands.FindCommand;
import com.thesquadmc.commands.ForceFieldCommand;
import com.thesquadmc.commands.FreezeCommand;
import com.thesquadmc.commands.FreezePanelCommand;
import com.thesquadmc.commands.GamemodeCommand;
import com.thesquadmc.commands.HealCommand;
import com.thesquadmc.commands.InvseeCommand;
import com.thesquadmc.commands.LaunchCommand;
import com.thesquadmc.commands.LookupCommand;
import com.thesquadmc.commands.ManagerChatCommand;
import com.thesquadmc.commands.MessageCommand;
import com.thesquadmc.commands.MonitorCommand;
import com.thesquadmc.commands.NTVersionCommand;
import com.thesquadmc.commands.NoteCommand;
import com.thesquadmc.commands.OnlineCountCommand;
import com.thesquadmc.commands.PartyCommand;
import com.thesquadmc.commands.PingCommand;
import com.thesquadmc.commands.ProxyTransportCommand;
import com.thesquadmc.commands.RandomTPCommand;
import com.thesquadmc.commands.RestartTimeCommand;
import com.thesquadmc.commands.SmiteCommand;
import com.thesquadmc.commands.StaffChatCommand;
import com.thesquadmc.commands.StaffMenuCommand;
import com.thesquadmc.commands.StafflistCommand;
import com.thesquadmc.commands.StaffmodeCommand;
import com.thesquadmc.commands.StatusCommand;
import com.thesquadmc.commands.StopCommand;
import com.thesquadmc.commands.StoreCommand;
import com.thesquadmc.commands.TeleportCommand;
import com.thesquadmc.commands.TitleCommand;
import com.thesquadmc.commands.UnFreezeCommand;
import com.thesquadmc.commands.UndisguisePlayerCommand;
import com.thesquadmc.commands.VanishCommand;
import com.thesquadmc.commands.VanishListCommand;
import com.thesquadmc.commands.WarpCommand;
import com.thesquadmc.commands.WebsiteCommand;
import com.thesquadmc.commands.WhitelistCommand;
import com.thesquadmc.commands.XrayVerboseCommand;
import com.thesquadmc.commands.YtNickCommand;
import com.thesquadmc.commands.YtVanishCommand;
import com.thesquadmc.inventories.FrozenInventory;
import com.thesquadmc.inventories.StaffmodeInventory;
import com.thesquadmc.listeners.ConnectionListeners;
import com.thesquadmc.listeners.ForceFieldListeners;
import com.thesquadmc.listeners.FreezeListener;
import com.thesquadmc.listeners.LaunchListener;
import com.thesquadmc.listeners.LightningListener;
import com.thesquadmc.listeners.ServerListener;
import com.thesquadmc.listeners.StaffmodeListener;
import com.thesquadmc.listeners.TimedListener;
import com.thesquadmc.listeners.VanishListener;
import com.thesquadmc.listeners.WhitelistListener;
import com.thesquadmc.listeners.XrayListener;
import com.thesquadmc.managers.ClickableMessageManager;
import com.thesquadmc.managers.CountManager;
import com.thesquadmc.managers.HologramManager;
import com.thesquadmc.managers.ItemManager;
import com.thesquadmc.managers.NPCManager;
import com.thesquadmc.managers.PartyManager;
import com.thesquadmc.networking.mongo.MongoManager;
import com.thesquadmc.networking.mongo.MongoUserDatabase;
import com.thesquadmc.networking.mongo.UserDatabase;
import com.thesquadmc.networking.redis.RedisManager;
import com.thesquadmc.networking.redis.RedisMesage;
import com.thesquadmc.networking.redis.channels.AnnounceChannel;
import com.thesquadmc.networking.redis.channels.FindChannel;
import com.thesquadmc.networking.redis.channels.MessageChannel;
import com.thesquadmc.networking.redis.channels.MonitorChannel;
import com.thesquadmc.networking.redis.channels.PartyChannel;
import com.thesquadmc.networking.redis.channels.ServerManagementChannel;
import com.thesquadmc.networking.redis.channels.StaffChatChannels;
import com.thesquadmc.networking.redis.channels.WhitelistChannel;
import com.thesquadmc.player.local.LocalPlayerManager;
import com.thesquadmc.utils.command.CommandHandler;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.file.FileManager;
import com.thesquadmc.utils.handlers.UpdateHandler;
import com.thesquadmc.utils.inventory.builder.AbstractGUI;
import com.thesquadmc.utils.nms.BarUtils;
import com.thesquadmc.utils.player.uuid.UUIDTranslator;
import com.thesquadmc.utils.server.Multithreading;
import com.thesquadmc.utils.server.ServerState;
import com.thesquadmc.utils.server.ServerUtils;
import com.thesquadmc.warp.WarpManager;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.cloud.CloudExpansion;
import me.gong.mcleaks.MCLeaksAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
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

    private static NetworkTools networkTools;
    private UUIDTranslator uuidTranslator;
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

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
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
