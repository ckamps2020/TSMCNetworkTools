package me.thesquadmc.commands;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.MessageSettings;
import me.thesquadmc.utils.RedisArg;
import me.thesquadmc.utils.RedisChannels;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class ManagerChatCommand implements CommandExecutor {

	private final Main main;

	public ManagerChatCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.managerchat")) {
				if (args.length == 0) {
					if (!tempData.isManagerchatEnabled()) {
						tempData.setManagerchatEnabled(true);
						player.sendMessage(StringUtils.msg("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eon&7!"));
					} else {
						tempData.setManagerchatEnabled(false);
						player.sendMessage(StringUtils.msg("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eoff&7!"));
					}
				} else {
					if (!tempData.isManagerchatEnabled()) {
						player.sendMessage(StringUtils.msg("&cPlease enable managerchat first!"));
						return true;
					}
					if (args[0].equalsIgnoreCase("global")) {
						tempData.setManagerSetting(MessageSettings.GLOBAL);
						player.sendMessage(StringUtils.msg("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat settings to &eglobal&7!"));
						return true;
					} else if (args[0].equalsIgnoreCase("server")) {
						tempData.setManagerSetting(MessageSettings.LOCAL);
						player.sendMessage(StringUtils.msg("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat settings to &eserver&7!"));
						return true;
					}
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : args) {
						stringBuilder.append(s + " ");
					}
					UserData cachedData = user.getCachedData();
					Contexts contexts = Contexts.allowAll();
					MetaData metaData = cachedData.getMetaData(contexts);
					String finalMessage = "&8[&c&lMC&8] " + metaData.getPrefix() + " " + player.getName() + " &8» &e" + stringBuilder.toString();
					if (tempData.getManagerSetting() == MessageSettings.GLOBAL) {
						Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
							@Override
							public void run() {
								try (Jedis jedis = main.getPool().getResource()) {
									JedisTask.withName(UUID.randomUUID().toString())
											.withArg(RedisArg.MESSAGE.getArg(), finalMessage)
											.send(RedisChannels.MANAGERCHAT.getChannelName(), jedis);
								}
							}
						});
					} else {
						for (Player p : Bukkit.getOnlinePlayers()) {
							User u = main.getLuckPermsApi().getUser(p.getUniqueId());
							TempData data = main.getTempDataManager().getTempData(p.getUniqueId());
							if (main.hasPerm(u, "tools.staff.managerchat")) {
								if (data.isManagerchatEnabled() && data.getManagerSetting() == MessageSettings.LOCAL) {
									p.sendMessage(StringUtils.msg(finalMessage));
								}
							}
						}
					}
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
