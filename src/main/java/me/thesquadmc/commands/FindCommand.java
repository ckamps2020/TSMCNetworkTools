package me.thesquadmc.commands;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.RedisArg;
import me.thesquadmc.utils.RedisChannels;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FindCommand implements CommandExecutor {

	private final Main main;
	private static List<String> stillLooking = new ArrayList<>();

	public FindCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.find")) {
				if (args.length == 1) {
					String name = args[0];
					Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
						@Override
						public void run() {
							player.sendMessage(StringUtils.msg("&e&lFIND&6■ &7Trying to find &e" + name));
							try (Jedis jedis = main.getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
										.withArg(RedisArg.PLAYER.getArg(), name)
										.withArg(RedisArg.ORIGIN_PLAYER.getArg(), player.getName())
										.send(RedisChannels.FIND.getChannelName(), jedis);
								Bukkit.getScheduler().runTaskLater(main, () -> {
									if (stillLooking.contains(player.getName())) {
										stillLooking.remove(player.getName());
										player.sendMessage(StringUtils.msg("&e&lFIND&6■ &7Unable to find player &e" + name));
									}
								}, 5 * 20L);
							}
						}
					});
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /find <player>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

	public static List<String> getStillLooking() {
		return stillLooking;
	}

}
