package me.thesquadmc.commands;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.*;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
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
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
				if (args.length == 0) {
					if (!tempData.isManagerchatEnabled()) {
						tempData.setManagerchatEnabled(true);
						player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eon&7!"));
					} else {
						tempData.setManagerchatEnabled(false);
						player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eoff&7!"));
					}
				} else {
					if (!tempData.isManagerchatEnabled()) {
						player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7Please enable managerchat first!"));
						return true;
					}
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : args) {
						stringBuilder.append(s + " ");
					}
					UserData cachedData = user.getCachedData();
					Contexts contexts = Contexts.allowAll();
					MetaData metaData = cachedData.getMetaData(contexts);
					String finalMessage = "&8[&c&lMANAGERCHAT&8] " + metaData.getPrefix() + "" + player.getName() + " &8» &c" + stringBuilder.toString();
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
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
