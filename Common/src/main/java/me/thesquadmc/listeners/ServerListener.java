package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.time.TimeUtils;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.enums.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class ServerListener implements Listener {

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.THREE_MIN) {
			if (Double.valueOf(ServerUtils.getTPS(0)) <= 13.00) {
				Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
					@Override
					public void run() {
						Multithreading.runAsync(new Runnable() {
							@Override
							public void run() {
								try (Jedis jedis = Main.getMain().getPool().getResource()) {
									JedisTask.withName(UUID.randomUUID().toString())
											.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName() + " ")
											.withArg(RedisArg.UPTIME.getArg(), TimeUtils.millisToRoundedTime(System.currentTimeMillis() - Main.getMain().getStartup()))
											.withArg(RedisArg.COUNT.getArg(), String.valueOf(Bukkit.getOnlinePlayers().size()))
											.withArg(RedisArg.MESSAGE.getArg(), "&7TPS = &e" + ServerUtils.getTPS(0) + "&7, &7Memory = &e" + ServerUtils.getUsedMemory() + "&8/&e" + ServerUtils.getTotalMemory() + "&7")
											.withArg(RedisArg.TPS.getArg(), ServerUtils.getTPS(0))
											.withArg(RedisArg.MEMORY.getArg(), ServerUtils.getUsedMemory() + "/" + ServerUtils.getTotalMemory())
											.send(RedisChannels.MONITOR_INFO.getChannelName(), jedis);
								}
							}
						});
					}
				});
			}
		} else if (e.getUpdateType() == UpdateType.FIVE_MIN) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!PlayerUtils.isDonator(player)) {
					player.sendMessage(CC.translate("&8&l&m------------------------------------"));
					player.sendMessage(" ");
					player.spigot().sendMessage(StringUtils.getHoverMessage("&e&lSTORE &6■ &7" + StringUtils.getRandomAd(), "&e&lstore.thesquadmc.net"));
					player.sendMessage(" ");
					player.sendMessage(CC.translate("&8&l&m------------------------------------"));
				}
			}
		}
	}

}
