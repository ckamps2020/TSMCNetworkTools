package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.RedisArg;
import me.thesquadmc.utils.RedisChannels;
import me.thesquadmc.utils.ServerUtils;
import me.thesquadmc.utils.TimeUtils;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.handlers.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class ServerListener implements Listener {

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.TWO_MIN) {
			if (Double.valueOf(ServerUtils.getTPS(0)) <= 15.00) {
				Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), () -> {
					try (Jedis jedis = Main.getMain().getPool().getResource()) {
						JedisTask.withName(UUID.randomUUID().toString())
								.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName() + " ")
								.withArg(RedisArg.UPTIME.getArg(), TimeUtils.millisToRoundedTime(System.currentTimeMillis() - Main.getMain().getStartup()))
								.withArg(RedisArg.COUNT.getArg(), String.valueOf(Bukkit.getOnlinePlayers().size()))
								.withArg(RedisArg.MESSAGE.getArg(), "&7TPS = &e" + ServerUtils.getTPS(0) + "&7, &7Memory = &e" + ServerUtils.getUsedMemory() + "&8/&e" + ServerUtils.getFreeMemory() + "&7")
								.send(RedisChannels.MONITOR_INFO.getChannelName(), jedis);
					}
				});
			}
		}
	}

}
