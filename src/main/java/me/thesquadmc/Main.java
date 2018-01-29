package me.thesquadmc;

import com.google.gson.Gson;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.thesquadmc.managers.TempDataManager;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.RedisHandler;
import me.thesquadmc.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public final class Main extends JavaPlugin {

	private JedisPool pool;
	private Gson gson = new Gson();
	private static Main main;
	private LuckPermsApi luckPermsApi;

	private FileManager fileManager;
	private TempDataManager tempDataManager;
	private RedisHandler redisHandler;

	private String host;
	private int port;
	private String password;

	@Override
	public void onEnable() {
		System.out.println("[StaffTools] Starting the plugin up...");
		main = this;
		luckPermsApi = LuckPerms.getApi();
		fileManager = new FileManager(this);
		fileManager.setup();
		tempDataManager = new TempDataManager();
		host = fileManager.getNetworkingConfig().getString("redis.host");
		port = fileManager.getNetworkingConfig().getInt("redis.port");
		password = fileManager.getNetworkingConfig().getString("redis.password");
		System.out.println("[StaffTools] Loading Redis PUB/SUB...");
		redisHandler = new RedisHandler(this);
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClassLoader());
		pool = new JedisPool(host, port);
		pool.getResource().auth(password);
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
						""
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
