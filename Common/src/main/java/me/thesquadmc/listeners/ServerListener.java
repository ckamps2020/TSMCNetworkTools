package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.enums.UpdateType;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.server.ServerUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ServerListener implements Listener {

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.TWO_MIN) {
			if (Double.valueOf(ServerUtils.getTPS(0)) <= 15.00) {
				Main.getMain().getRedisManager().sendMessage(RedisChannels.MONITOR_INFO, RedisMesage.newMessage()
						.set(RedisArg.SERVER, Bukkit.getServerName() + " ")
						.set(RedisArg.UPTIME, TimeUtils.getFormattedTime(System.currentTimeMillis() - Main.getMain().getStartup()))
						.set(RedisArg.COUNT, String.valueOf(Bukkit.getOnlinePlayers().size()))
						.set(RedisArg.MESSAGE.getName(), String.format("&7TPS = &e%s&7, Memory = &e%s&8/&e%s", ServerUtils.getTPS(0), ServerUtils.getUsedMemory(), ServerUtils.getTotalMemory()))
						.set(RedisArg.TPS, ServerUtils.getTPS(0))
						.set(RedisArg.MEMORY, ServerUtils.getUsedMemory() + "/" + ServerUtils.getTotalMemory()));
			}
		}
	}

}
