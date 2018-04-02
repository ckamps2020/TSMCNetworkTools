package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.*;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class ProxyListCommand implements CommandExecutor {

	private final Main main;

	public ProxyListCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
					@Override
					public void run() {
						Multithreading.runAsync(new Runnable() {
							@Override
							public void run() {
								try (Jedis jedis = main.getPool().getResource()) {
									JedisTask.withName(UUID.randomUUID().toString())
											.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
											.withArg(RedisArg.PLAYER.getArg(), player.getName())
											.send(RedisChannels.PROXY_REQUEST.getChannelName(), jedis);
								}
							}
						});
					}
				});
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}