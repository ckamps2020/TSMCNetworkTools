package me.thesquadmc.commands;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class AdminChatCommand implements CommandExecutor {

	private final Main main;

	public AdminChatCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
				if (args.length == 0) {
					if (!tempData.isAdminchatEnabled()) {
						tempData.setAdminchatEnabled(true);
						player.sendMessage(StringUtils.msg("&e&lADMIN CHAT &6■ &7You toggled Admin Chat &eon&7!"));
					} else {
						tempData.setAdminchatEnabled(false);
						player.sendMessage(StringUtils.msg("&e&lADMIN CHAT &6■ &7You toggled Admin Chat &eoff&7!"));
					}
				} else {
					if (!tempData.isAdminchatEnabled()) {
						player.sendMessage(StringUtils.msg("&cPlease enable adminchat first!"));
						return true;
					}
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : args) {
						stringBuilder.append(s + " ");
					}
					UserData cachedData = user.getCachedData();
					Contexts contexts = Contexts.allowAll();
					MetaData metaData = cachedData.getMetaData(contexts);
					String finalMessage = "&8[&c&lADMINCHAT&8] " + metaData.getPrefix() + "" + player.getName() + " &8» &c" + stringBuilder.toString();
					Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
						@Override
						public void run() {
							try (Jedis jedis = main.getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.MESSAGE.getArg(), finalMessage)
										.send(RedisChannels.ADMINCHAT.getChannelName(), jedis);
							}
						}
					});
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
