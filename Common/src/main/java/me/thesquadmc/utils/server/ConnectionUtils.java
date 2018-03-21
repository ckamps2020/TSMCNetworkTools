package me.thesquadmc.utils.server;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.GameMsgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ConnectionUtils {

	private static List<UUID> fetching = new ArrayList<>();

	public static void sendPlayer(Player player, String server) {
		player.sendMessage(CC.translate("&e&lTRANSPORT &6■ &7Sending you to &e" + server + "&7..."));
		Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
			@Override
			public void run() {
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
		});
	}

	public static void sendPlayerGameServer(Player player, String server) {
		player.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Sending you to &e" + server + "&7..."));
		//send all party members as well
		Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
			@Override
			public void run() {
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
		});
	}

	public static void fetchGameServer(Player player, String serverType) {
		if (!fetching.contains(player.getUniqueId())) {
			//TODO: Check here if they are in a party to respect counts
			player.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Finding you an open " + serverType + " server..."));
			Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
				@Override
				public void run() {
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
							fetching.add(player.getUniqueId());
							try (Jedis jedis = Main.getMain().getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.COUNT.getArg(), "1")
										.withArg(RedisArg.ORIGIN_PLAYER.getArg(), player.getName())
										.withArg(RedisArg.ORIGIN_SERVER.getArg(), Bukkit.getServerName())
										.withArg(RedisArg.SERVER.getArg(), serverType)
										.send(RedisChannels.REQUEST_SERVER.getChannelName(), jedis);
							}
							Bukkit.getScheduler().runTaskLater(Main.getMain(), new Runnable() {
								@Override
								public void run() {
									if (fetching.contains(player.getUniqueId())) {
										fetching.remove(player.getUniqueId());
										if (Bukkit.getPlayer(player.getUniqueId()) != null) {
											player.sendMessage(GameMsgs.GAME_PREFIX + "Unable to find you an open server!");
										}
									}
								}
							}, 3);
						}
					});
				}
			});
		} else {
			player.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Whoa slow down there before queueing again!"));
		}
	}

	public static List<UUID> getFetching() {
		return fetching;
	}

}
