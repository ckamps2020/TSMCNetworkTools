package me.thesquadmc;

import com.google.gson.Gson;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.thesquadmc.commands.*;
import me.thesquadmc.inventories.FrozenInventory;
import me.thesquadmc.inventories.ReportInventory;
import me.thesquadmc.inventories.StaffmodeInventory;
import me.thesquadmc.listeners.*;
import me.thesquadmc.managers.HologramManager;
import me.thesquadmc.managers.NPCManager;
import me.thesquadmc.managers.ReportManager;
import me.thesquadmc.managers.TempDataManager;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.RedisHandler;
import me.thesquadmc.networking.mysql.DatabaseManager;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.enums.Settings;
import me.thesquadmc.utils.file.FileManager;
import me.thesquadmc.utils.handlers.UpdateHandler;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerState;
import me.thesquadmc.utils.server.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
	private JedisPubSub jedisPubSub;
	private Jedis j;
	private DatabaseManager MySQL;
	private ThreadPoolExecutor threadPoolExecutor;

	private int chatslow = 0;
	private boolean chatSilenced = false;
	private String serverState = ServerState.LOADING;

	private FileManager fileManager;
	private TempDataManager tempDataManager;
	private RedisHandler redisHandler;
	private FrozenInventory frozenInventory;
	private StaffmodeInventory staffmodeInventory;
	private UpdateHandler updateHandler;
	private ReportManager reportManager;
	private ReportInventory reportInventory;
	private HologramManager hologramManager;
	private NPCManager npcManager;

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
		luckPermsApi = LuckPerms.getApi();
		fileManager = new FileManager(this);
		threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		MySQL = new DatabaseManager(this, this);
		new StringUtils();
		frozenInventory = new FrozenInventory(this);
		staffmodeInventory = new StaffmodeInventory(this);
		updateHandler = new UpdateHandler(this);
		reportManager = new ReportManager();
		reportInventory = new ReportInventory(this);
		fileManager.setup();
		updateHandler.run();
		tempDataManager = new TempDataManager();
		hologramManager = new HologramManager();
		npcManager = new NPCManager();
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
		getCommand("report").setExecutor(new ReportCommand(this));
		getCommand("managereports").setExecutor(new ManageReportsCommand(this));
		getCommand("alert").setExecutor(new AlertCommand(this));
		getCommand("stop").setExecutor(new StopCommand(this));
		getCommand("whitelist").setExecutor(new WhitelistCommand(this));
		getCommand("launch").setExecutor(new LaunchCommand(this));
		getCommand("ytvanish").setExecutor(new YtVanishCommand(this));
		getCommand("forcefield").setExecutor(new ForceFieldCommand(this));
		getCommand("staffmenu").setExecutor(new StaffMenuCommand(this));
		getCommand("proxylist").setExecutor(new ProxyListCommand(this));
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
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new SettingsListener(), this);
		getServer().getPluginManager().registerEvents(new LaunchListener(), this);
		getServer().getPluginManager().registerEvents(new LightningListener(), this);
		getServer().getPluginManager().registerEvents(new FilterListener(this), this);
		getServer().getPluginManager().registerEvents(new ServerListener(), this);
		getServer().getPluginManager().registerEvents(new ForceFieldListeners(this), this);
		getServer().getPluginManager().registerEvents(new VanishListener(this), this);
		getServer().getPluginManager().registerEvents(new WhitelistListener(this), this);
		getServer().getPluginManager().registerEvents(new ReportListener(this), this);
		getServer().getPluginManager().registerEvents(new ConnectionListeners(this), this);
		getServer().getPluginManager().registerEvents(new XrayListener(this), this);
		getServer().getPluginManager().registerEvents(new StaffmodeListener(this), this);
		getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
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
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClassLoader());
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
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
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
									RedisChannels.LEAVE.getChannelName(),
									RedisChannels.LOGIN.getChannelName());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
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
		ServerUtils.updateServerState(ServerState.ONLINE);
		System.out.println("[NetworkTools] Plugin started up and ready to go!");
	}

	@Override
	public void onDisable() {
		System.out.println("[NetworkTools] Shutting down...");
		pool.getResource().disconnect();
		j.disconnect();
		System.out.println("[NetworkTools] Shut down! Cya :D");
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

	public JedisPubSub getJedisPubSub() {
		return jedisPubSub;
	}

	public Jedis getJedis() {
		return jedis;
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

	public ReportInventory getReportInventory() {
		return reportInventory;
	}

	public ReportManager getReportManager() {
		return reportManager;
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

}
