package me.thesquadmc.utils.server;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class ConnectionUtils {

	public static void sendPlayer(Player player, String server) {
		player.sendMessage(CC.translate("&e&lTRANSPORT &6■ &7Sending you to &e" + server + "&7..."));
		Multithreading.runAsync(new Runnable() {
			@Override
			public void run() {
				try (Jedis jedis = Main.getMain().getPool().getResource()) {
					JedisTask.withName(UUID.randomUUID().toString())
							.withArg(RedisArg.PLAYER.getArg(), player.getName())
							.withArg(RedisArg.SERVER.getArg(), server)
							.send(RedisChannels.TRANSPORT.getChannelName(), jedis);
				}
			}
		});
	}

	public static void findOpenServer(Player player, String serverType) {
		Multithreading.runAsync(new Runnable() {
			@Override
			public void run() {
				try (Jedis jedis = Main.getMain().getPool().getResource()) {
					JedisTask.withName(UUID.randomUUID().toString())
							.withArg(RedisArg.PLAYER.getArg(), player.getName())
							.withArg(RedisArg.ORIGIN_SERVER.getArg(), Bukkit.getServerName())
							.withArg(RedisArg.SERVER.getArg(), serverType)
							.send(RedisChannels.REQUEST_SERVER.getChannelName(), jedis);
				}
			}
		});
	}

}
