package me.thesquadmc.networking;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.commands.FindCommand;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.MessageSettings;
import me.thesquadmc.utils.RedisArg;
import me.thesquadmc.utils.RedisChannels;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;

public final class RedisHandler {

	private final Main main;

	public RedisHandler(Main main) {
		this.main = main;
	}

	public void processRedisMessage(JedisTask task, String channel, String message) {
		Map<String, Object> data = task.getData();
		if (channel.equalsIgnoreCase(RedisChannels.STAFFCHAT.toString())) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				User user = main.getLuckPermsApi().getUser(player.getUniqueId());
				TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
				if (main.hasPerm(user, "tools.staff.staffchat")) {
					if (tempData.isStaffchatEnabled() && tempData.getStaffchatSetting() == MessageSettings.GLOBAL) {
						player.sendMessage(StringUtils.msg(String.valueOf(data.get(RedisArg.MESSAGE.getArg()))));
					}
				}
			}
		} else if (channel.equalsIgnoreCase(RedisChannels.FIND.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getName().equalsIgnoreCase(name)) {
							TempData tempData = main.getTempDataManager().getTempData(p.getUniqueId());
							try (Jedis jedis = main.getPool().getResource()) {
								JedisTask.withName(UUID.randomUUID().toString())
										.withArg(RedisArg.SERVER.getArg(), String.valueOf(data.get(RedisArg.SERVER.getArg())))
										.withArg(RedisArg.ORIGIN_SERVER.getArg(), Bukkit.getServerName())
										.withArg(RedisArg.PLAYER.getArg(), name)
										.withArg(RedisArg.ORIGIN_PLAYER.getArg(), String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg())))
										.withArg(RedisArg.LOGIN.getArg(), tempData.getLoginTime())
										.send(RedisChannels.FOUND.getChannelName(), jedis);
							}
							return;
						}
					}
				}
			});
		} else if (channel.equalsIgnoreCase(RedisChannels.FOUND.getChannelName())) {
			main.getServer().getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					String server = String.valueOf(data.get(RedisArg.SERVER.getArg()));
					if (server.equalsIgnoreCase(Bukkit.getServerName())) {
						String name = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
						String origin = String.valueOf(data.get(RedisArg.ORIGIN_PLAYER.getArg()));
						String originServer = String.valueOf(data.get(RedisArg.ORIGIN_SERVER.getArg()));
						String time = String.valueOf(data.get(RedisArg.LOGIN.getArg()));
						Player p = Bukkit.getPlayer(origin);
						if (p != null) {
							FindCommand.getStillLooking().remove(p.getName());
							p.sendMessage(" ");
							p.sendMessage(StringUtils.msg("&6&l" + name));
							p.sendMessage(StringUtils.msg("&8■ &7Server: &f" + originServer));
							p.sendMessage(StringUtils.msg("&8■ &7Online Since: &f" + time));
						}
					}
				}
			});
		}
	}

}
