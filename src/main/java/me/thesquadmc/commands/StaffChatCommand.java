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

public final class StaffChatCommand implements CommandExecutor {

	private final Main main;

	public StaffChatCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				if (args.length == 0) {
					if (!tempData.isStaffchatEnabled()) {
						tempData.setStaffchatEnabled(true);
						player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7You toggled Staff Chat &eon&7!"));
					} else {
						tempData.setStaffchatEnabled(false);
						player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7You toggled Staff Chat &eoff&7!"));
					}
				} else {
					if (!tempData.isStaffchatEnabled()) {
						player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7Please enable staffchat first!"));
						return true;
					}
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : args) {
						stringBuilder.append(s + " ");
					}
					UserData cachedData = user.getCachedData();
					Contexts contexts = Contexts.allowAll();
					MetaData metaData = cachedData.getMetaData(contexts);
					String finalMessage = "&8[&a&lSTAFFCHAT&8] " + metaData.getPrefix() + "" + player.getName() + " &8» &a" + stringBuilder.toString();
					Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
						@Override
						public void run() {
							try (Jedis jedis = main.getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.MESSAGE.getArg(), finalMessage)
										.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
										.send(RedisChannels.STAFFCHAT.getChannelName(), jedis);
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
