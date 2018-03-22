package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.*;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.server.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class StopCommand implements CommandExecutor {

	private final Main main;

	public StopCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
				if (args.length >= 2) {
					String server = args[0];
					StringBuilder stringBuilder = new StringBuilder();
					for (int i = 1; i < args.length; i++) {
						stringBuilder.append(args[i] + " ");
					}
					player.sendMessage(CC.translate("&e&lSTOP &6■ &7You have stopped &e" + server + " &7for &e" + stringBuilder.toString() + "&7"));
					Multithreading.runAsync(new Runnable() {
						@Override
						public void run() {
							try (Jedis jedis = Main.getMain().getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.SERVER.getArg(), server.toUpperCase())
										.withArg(RedisArg.MESSAGE.getArg(), stringBuilder.toString())
										.send(RedisChannels.STOP.getChannelName(), jedis);
							}
						}
					});
				} else {
					player.sendMessage(CC.translate("&e&lSTOP &6■ &7Usage: /stop <servertype|server|all> <reason>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		} else {
			ServerUtils.safeShutdown();
		}
		return true;
	}

}
