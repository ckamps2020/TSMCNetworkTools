package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.*;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class WhitelistCommand implements CommandExecutor {

	private final Main main;

	public WhitelistCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("list")) {
						StringBuilder stringBuilder = new StringBuilder();
						for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()) {
							stringBuilder.append(offlinePlayer.getName() + " ");
						}
						player.sendMessage(CC.translate("&e&lWhitelisted users &7(" + Bukkit.getWhitelistedPlayers().size() + "): &f" + stringBuilder.toString()));
					} else {
						player.sendMessage(CC.translate("&cUsage: /whitelist <servertype|server|all> <on|off> <reason>"));
						player.sendMessage(CC.translate("&cUsage: /whitelist add <player> <servertype|server|all>"));
						player.sendMessage(CC.translate("&cUsage: /whitelist remove <player> <servertype|server|all>"));
						player.sendMessage(CC.translate("&cUsage: /whitelist list"));
					}
				} else if (args.length >= 3) {
					String a = args[0];
					if (a.equalsIgnoreCase("add")) {
						String name = args[1];
						String server = args[2];
						player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have added &e" + name + " &7to the whitelist on server &e" + server));
						Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
							@Override
							public void run() {
								try (Jedis jedis = main.getPool().getResource()) {
									JedisTask.withName(UUID.randomUUID().toString())
											.withArg(RedisArg.SERVER.getArg(), server.toUpperCase())
											.withArg(RedisArg.PLAYER.getArg(), name)
											.send(RedisChannels.WHITELIST_ADD.getChannelName(), jedis);
								}
							}
						});
					} else if (a.equalsIgnoreCase("remove")) {
						String name = args[1];
						String server = args[2];
						player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have removed &e" + name + " &7to the whitelist on server &e" + server));
						Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
							@Override
							public void run() {
								try (Jedis jedis = main.getPool().getResource()) {
									JedisTask.withName(UUID.randomUUID().toString())
											.withArg(RedisArg.SERVER.getArg(), server.toUpperCase())
											.withArg(RedisArg.PLAYER.getArg(), name)
											.send(RedisChannels.WHITELIST_REMOVE.getChannelName(), jedis);
								}
							}
						});
					} else {
						String server = args[0];
						String onoff = args[1];
						if (onoff.equalsIgnoreCase("ON")) {
							if (server.equalsIgnoreCase("ALL")) {
								player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have whitelisted &eall servers&7"));
							} else {
								player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have whitelisted &e" + server + "&7"));
							}
						} else if (onoff.equalsIgnoreCase("OFF")) {
							if (server.equalsIgnoreCase("ALL")) {
								player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have turned whitelist off on &eall servers&7"));
							} else {
								player.sendMessage(CC.translate("&e&lWHITELIST &6■ &7You have turned whitelist off on &e" + server + "&7"));
							}
						} else {
							player.sendMessage(CC.translate("&e&lWHITELIST &6■ &e" + onoff + " &7is not valid and should either be on or off!"));
							return true;
						}
						StringBuilder stringBuilder = new StringBuilder();
						for (int i = 2; i < args.length; i++) {
							stringBuilder.append(args[i] + " ");
						}
						Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
							@Override
							public void run() {
								try (Jedis jedis = main.getPool().getResource()) {
									JedisTask.withName(UUID.randomUUID().toString())
											.withArg(RedisArg.SERVER.getArg(), server.toUpperCase())
											.withArg(RedisArg.ONOFF.getArg(), onoff)
											.withArg(RedisArg.MESSAGE.getArg(), stringBuilder.toString())
											.send(RedisChannels.WHITELIST.getChannelName(), jedis);
								}
							}
						});
					}
				} else {
					player.sendMessage(CC.translate("&cUsage: /whitelist <servertype|server|all> <on|off> <reason>"));
					player.sendMessage(CC.translate("&cUsage: /whitelist add <player> <servertype|server|all>"));
					player.sendMessage(CC.translate("&cUsage: /whitelist remove <player> <servertype|server|all>"));
					player.sendMessage(CC.translate("&cUsage: /whitelist list"));
				}
			} else {
				player.sendMessage(CC.translate("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
