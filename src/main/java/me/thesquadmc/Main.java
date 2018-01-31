package me.thesquadmc;

import com.google.gson.Gson;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.thesquadmc.commands.*;
import me.thesquadmc.inventories.FrozenInventory;
import me.thesquadmc.inventories.ReportInventory;
import me.thesquadmc.inventories.StaffmodeInventory;
import me.thesquadmc.listeners.*;
import me.thesquadmc.managers.ReportManager;
import me.thesquadmc.managers.TempDataManager;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.RedisHandler;
import me.thesquadmc.utils.FileManager;
import me.thesquadmc.utils.RedisChannels;
import me.thesquadmc.utils.handlers.UpdateHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.*;

public final class Main extends JavaPlugin {

	private JedisPool pool;
	private Gson gson = new Gson();
	private static Main main;
	private LuckPermsApi luckPermsApi;
	private String whitelistMessage = ChatColor.translateAlternateColorCodes('&', "&cServer currently whitelisted!");

	private FileManager fileManager;
	private TempDataManager tempDataManager;
	private RedisHandler redisHandler;
	private FrozenInventory frozenInventory;
	private StaffmodeInventory staffmodeInventory;
	private UpdateHandler updateHandler;
	private ReportManager reportManager;
	private ReportInventory reportInventory;

	private String host;
	private int port;
	private String password;

	@Override
	public void onEnable() {
		System.out.println("[StaffTools] Starting the plugin up...");
		main = this;
		luckPermsApi = LuckPerms.getApi();
		fileManager = new FileManager(this);
		frozenInventory = new FrozenInventory(this);
		staffmodeInventory = new StaffmodeInventory(this);
		updateHandler = new UpdateHandler(this);
		reportManager = new ReportManager();
		reportInventory = new ReportInventory(this);
		fileManager.setup();
		updateHandler.run();
		tempDataManager = new TempDataManager();
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
		getServer().getPluginManager().registerEvents(new WhitelistListener(this), this);
		getServer().getPluginManager().registerEvents(new ReportListener(this), this);
		getServer().getPluginManager().registerEvents(new ConnectionListeners(this), this);
		getServer().getPluginManager().registerEvents(new XrayListener(this), this);
		getServer().getPluginManager().registerEvents(new StaffmodeListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		host = fileManager.getNetworkingConfig().getString("redis.host");
		port = fileManager.getNetworkingConfig().getInt("redis.port");
		password = fileManager.getNetworkingConfig().getString("redis.password");
		System.out.println("[StaffTools] Loading Redis PUB/SUB...");
		redisHandler = new RedisHandler(this);
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClassLoader());
		//JedisPoolConfig poolConfig = new JedisPoolConfig();
		//pool = new JedisPool(poolConfig, host, port, 10000, password);
		pool = new JedisPool(host, port);
		Thread.currentThread().setContextClassLoader(previous);

		JedisPubSub pubSub = new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				JedisTask task = gson.fromJson(message, JedisTask.class);
				getRedisHandler().processRedisMessage(task, channel, message);
			}
		};

		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				pool.getResource().subscribe(pubSub,
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
						RedisChannels.WHITELIST_REMOVE.getChannelName()
				);
			}
		});
		System.out.println("[StaffTools] Redis PUB/SUB setup!");
		System.out.println("[StaffTools] Plugin started up and ready to go!");
	}

	@Override
	public void onDisable() {
		System.out.println("[StaffTools] Shutting down...");
		if (!pool.isClosed()) {
			pool.close();
		}
		System.out.println("[StaffTools] Shut down! Cya :D");
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

	public boolean hasPerm(User user, String perm) {
		return user.getPermissions().stream()
				.filter(Node::getValue)
				.filter(Node::isPermanent)
				.filter(n -> !n.isServerSpecific())
				.filter(n -> !n.isWorldSpecific())
				.anyMatch(n -> n.getPermission().startsWith(perm));
	}

}
