package me.thesquadmc;

import com.google.gson.Gson;
import me.gong.mcleaks.MCLeaksAPI;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.thesquadmc.commands.*;
import me.thesquadmc.abstraction.AbstractionModule;
import me.thesquadmc.abstraction.NMSAbstract;
import me.thesquadmc.inventories.FrozenInventory;
import me.thesquadmc.inventories.StaffmodeInventory;
import me.thesquadmc.listeners.*;
import me.thesquadmc.managers.*;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.RedisHandler;
import me.thesquadmc.networking.mongo.Mongo;
import me.thesquadmc.networking.mysql.DatabaseManager;
import me.thesquadmc.utils.command.CommandHandler;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.enums.Settings;
import me.thesquadmc.utils.file.FileManager;
import me.thesquadmc.utils.handlers.UpdateHandler;
import me.thesquadmc.utils.inventory.builder.AbstractGUI;
import me.thesquadmc.utils.msgs.ServerType;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.nms.BarUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerState;
import me.thesquadmc.utils.server.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.*;

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

public final class Main extends JavaPlugin {

	private JedisPool pool;
	private Gson gson = new Gson();
	private static Main main;
	private LuckPermsApi luckPermsApi;
	private String whitelistMessage = ChatColor.translateAlternateColorCodes('&', "&cServer currently whitelisted!");
	private long startup = System.currentTimeMillis();
	private String value = "NONE";
	private String sig = "NONE";
	private Jedis jedis;
	private JedisPoolConfig poolConfig;
	private Jedis j;
	private DatabaseManager MySQL;
	private ThreadPoolExecutor threadPoolExecutor;
	private int restartTime = 0;
	private String version = "1.3.2";
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

	private String host;
	private int port;
	private String password;
	private String mysqlhost;
	private String mysqlport;
	private String mysqlpassword;
	private String mysqldb;
	private String dbuser;

	private Map<UUID, List<String>> friends = new HashMap<>();
	private Map<UUID, List<String>> requests = new HashMap<>();
	private Map<UUID, Map<Settings, Boolean>> settings = new HashMap<>();

	@Override
	public void onEnable() {
		System.out.println("[NetworkTools] Starting the plugin up...");
		main = this;

		if (!setupNMSAbstract()) {
			this.getLogger().severe("This jar was not built for this server implementation!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		this.getLogger().info("[NetworkTools] Server implementation set for " + nmsAbstract.getVersionMin() + " - " + nmsAbstract.getVersionMax());
		
		luckPermsApi = LuckPerms.getApi();
		mcLeaksAPI =  MCLeaksAPI.builder().threadCount(2).expireAfter(10, TimeUnit.MINUTES).build();
		fileManager = new FileManager(this);
		threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		MySQL = new DatabaseManager(this, this);
		new StringUtils();
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
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new SettingsListener(), this);
		getServer().getPluginManager().registerEvents(new LaunchListener(), this);
		getServer().getPluginManager().registerEvents(new LightningListener(), this);
		getServer().getPluginManager().registerEvents(new FilterListener(this), this);
		getServer().getPluginManager().registerEvents(new ServerListener(), this);
		getServer().getPluginManager().registerEvents(new ForceFieldListeners(this), this);
		getServer().getPluginManager().registerEvents(new VanishListener(this), this);
		getServer().getPluginManager().registerEvents(new WhitelistListener(this), this);
		getServer().getPluginManager().registerEvents(new TimedListener(this), this);
		getServer().getPluginManager().registerEvents(new ConnectionListeners(this), this);
		getServer().getPluginManager().registerEvents(new XrayListener(this), this);
		getServer().getPluginManager().registerEvents(new StaffmodeListener(this), this);
		getServer().getPluginManager().registerEvents(new FreezeListener(this), this);

		commandHandler.registerCommands(new CreativeCommand());

		host = fileManager.getNetworkingConfig().getString("redis.host");
		port = fileManager.getNetworkingConfig().getInt("redis.port");
		password = fileManager.getNetworkingConfig().getString("redis.password");
		mysqlhost = fileManager.getNetworkingConfig().getString("mysql.host");
		mysqlport = fileManager.getNetworkingConfig().getString("mysql.port");
		mysqlpassword = fileManager.getNetworkingConfig().getString("mysql.dbpassword");
		mysqldb = fileManager.getNetworkingConfig().getString("mysql.dbname");
		dbuser = fileManager.getNetworkingConfig().getString("mysql.dbuser");
		System.out.println("[NetworkTools] Loading Redis PUB/SUB...");
		redisHandler = new RedisHandler(this);
		try {
			ClassLoader previous = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(main.getClassLoader());
			poolConfig = new JedisPoolConfig();
			poolConfig.setTestOnReturn(true);
			poolConfig.setTestWhileIdle(true);
			poolConfig.setMinIdle(20);
			poolConfig.setMaxIdle(150);
			poolConfig.setMaxTotal(150);
			pool = new JedisPool(poolConfig, host, port, 40*1000, password);
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
					            }, RedisChannels.ADMINCHAT.getChannelName(),
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
							RedisChannels.PLAYER_COUNT.getChannelName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("[NetworkTools] Redis PUB/SUB setup!");
		Multithreading.runAsync(new Runnable() {
			@Override
			public void run() {
				System.out.println("[NetworkTools] Connecting to mysql database...");
				try {
					MySQL.setupDB();
					System.out.println("[NetworkTools] Connected to mysql database!");
				}
				catch (ClassNotFoundException|SQLException e) {
					e.printStackTrace();
					System.out.println("[NetworkTools] Unable to connect to mysql database!");
				}
			}
		});
		Multithreading.runAsync(new Runnable() {
			@Override
			public void run() {
				//mongo = new Mongo();
			}
		});
		getCommand("staffchat").setExecutor(new StaffChatCommand(this));
		getCommand("adminchat").setExecutor(new AdminChatCommand(this));
		getCommand("managerchat").setExecutor(new ManagerChatCommand(this));
		getCommand("find").setExecutor(new FindCommand(this));
		getCommand("lookup").setExecutor(new LookupCommand(this));
		getCommand("vanish").setExecutor(new VanishCommand(this));
		getCommand("freeze").setExecutor(new FreezeCommand(this));
		getCommand("unfreeze").setExecutor(new UnFreezeCommand(this));
		getCommand("freezepanel").setExecutor(new FreezePanelCommand(this));
		getCommand("invsee").setExecutor(new InvseeCommand(this));
		getCommand("randomtp").setExecutor(new RandomTPCommand(this));
		getCommand("staffmode").setExecutor(new StaffmodeCommand(this));
		getCommand("staff").setExecutor(new StafflistCommand(this));
		getCommand("xray").setExecutor(new XrayVerboseCommand(this));
		getCommand("alert").setExecutor(new AlertCommand(this));
		getCommand("stop").setExecutor(new StopCommand(this));
		getCommand("whitelist").setExecutor(new WhitelistCommand(this));
		getCommand("launch").setExecutor(new LaunchCommand(this));
		getCommand("ytvanish").setExecutor(new YtVanishCommand(this));
		getCommand("forcefield").setExecutor(new ForceFieldCommand(this));
		getCommand("staffmenu").setExecutor(new StaffMenuCommand(this));
		getCommand("monitor").setExecutor(new MonitorCommand(this));
		getCommand("ytnick").setExecutor(new YtNickCommand(this));
		getCommand("disguiseplayer").setExecutor(new DisguisePlayerCommand(this));
		getCommand("undisguiseplayer").setExecutor(new UndisguisePlayerCommand(this));
		getCommand("silence").setExecutor(new ChatSilenceCommand(this));
		getCommand("slowchat").setExecutor(new ChatSlowCommand(this));
		getCommand("smite").setExecutor(new SmiteCommand(this));
		getCommand("friend").setExecutor(new FriendCommand(this));
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
		getCommand("vanishlist").setExecutor(new VanishListCommand(this));
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
		ServerUtils.calculateServerType();
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
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
				});
			}
		}, 1, 1 * 20L);
		if (serverType.startsWith(ServerType.MINIGAME_HUB)) {
			Multithreading.runAsync(new Runnable() {
				@Override
				public void run() {
					try (Jedis jedis = Main.getMain().getPool().getResource()) {
						JedisTask.withName(UUID.randomUUID().toString())
								.withArg(RedisArg.SERVER.getArg(), "ALL")
								.send(RedisChannels.STARTUP_REQUEST.getChannelName(), jedis);
					}
				}
			});
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> BarUtils.getPlayers().forEach(nmsAbstract.getBossBarManager()::teleportBar), 1, 20L);
		System.out.println("[NetworkTools] Plugin started up and ready to go!");

		commandHandler.registerHelp();
	}

	@Override
	public void onDisable() {
		System.out.println("[NetworkTools] Shutting down...");
		System.out.println("[NetworkTools] Shut down! Cya :D");
	}

	public Jedis getJ() {
		return j;
	}

	public Mongo getMongo() {
		return mongo;
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

	public Map<UUID, List<String>> getFriends() {
		return friends;
	}

	public Map<UUID, List<String>> getRequests() {
		return requests;
	}

	public Map<UUID, Map<Settings, Boolean>> getSettings() {
		return settings;
	}

	public Jedis getJedis() {
		return jedis;
	}

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

	public Gson getGson() {
		return gson;
	}
	
	/**
	 * Get an instance of the NMSAbstract class for this server version implementation
	 * 
	 * @return the related NMSAbstract instance
	 */
	public NMSAbstract getNMSAbstract() {
		return nmsAbstract;
	}

	private String getPoolCurrentUsage() {
		int active = pool.getNumActive();
		int idle = pool.getNumIdle();
		int total = active + idle;
		String log = String.format(
				"Active=%d, Idle=%d, Waiters=%d, total=%d, maxTotal=%d, minIdle=%d, maxIdle=%d",
				active,
				idle,
				pool.getNumWaiters(),
				total,
				poolConfig.getMaxTotal(),
				poolConfig.getMinIdle(),
				poolConfig.getMaxIdle()
		);
		return log;
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
				
				Class<?> clazz = null;
				try {
					clazz = Class.forName(entryName);
				} catch (ClassNotFoundException e) { continue; }
				
				if (!clazz.isAnnotationPresent(AbstractionModule.class)) continue; // Not an abstraction module... ignore
				
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

}
