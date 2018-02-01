package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class AlertCommand implements CommandExecutor {

	private final Main main;

	public AlertCommand(Main main) {
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
					player.sendMessage(StringUtils.msg("&e&lALERT &6■ &7You sent an alert:"));
					player.sendMessage(StringUtils.msg("  &7Server: &e" + server));
					player.sendMessage(StringUtils.msg("  &7Message: &e" + stringBuilder.toString()));
					Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
						@Override
						public void run() {
							try (Jedis jedis = main.getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.SERVER.getArg(), server.toUpperCase())
										.withArg(RedisArg.MESSAGE.getArg(), stringBuilder.toString())
										.send(RedisChannels.ANNOUNCEMENT.getChannelName(), jedis);
							}
						}
					});
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /alert <servertype|server|all> <message>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
