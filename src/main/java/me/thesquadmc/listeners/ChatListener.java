package me.thesquadmc.listeners;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.enums.Settings;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		String message = e.getMessage();
		Player player = e.getPlayer();
		if (Main.getMain().getSettings() != null && Main.getMain().getSettings().get(player.getUniqueId()) != null && Main.getMain().getSettings().get(player.getUniqueId()).get(Settings.FRIENDCHAT)) {
			e.setCancelled(true);
			if (Main.getMain().getFriends() == null || Main.getMain().getFriends().get(player.getUniqueId()) == null || Main.getMain().getFriends().get(player.getUniqueId()).isEmpty()) {
				player.sendMessage(CC.translate("&d&lFRIENDS &5■ &7Add some friends before you use this!"));
				return;
			}
			StringBuilder stringBuilder = new StringBuilder();
			for (String s : Main.getMain().getFriends().get(player.getUniqueId())) {
				stringBuilder.append(s + " ");
			}
			User user = Main.getMain().getLuckPermsApi().getUser(player.getName());
			UserData cachedData = user.getCachedData();
			Contexts contexts = Contexts.allowAll();
			MetaData metaData = cachedData.getMetaData(contexts);
			String formattedMsg = CC.translate("&8[&d&lFRIENDCHAT&8] &r" + metaData.getPrefix() + player.getName() + " &8» &d" + message);
			String ssMsg = CC.translate("&8[&d&lFRIENDCHAT SS&8] &7" + player.getName() + " &8» &d" + message);
			player.sendMessage(formattedMsg);
			Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
				@Override
				public void run() {
					try (Jedis jedis = Main.getMain().getPool().getResource()) {
						JedisTask.withName(UUID.randomUUID().toString())
								.withArg(RedisArg.FRIENDS.getArg(), stringBuilder.toString())
								.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
								.withArg(RedisArg.PLAYER.getArg(), player.getName())
								.withArg(RedisArg.MESSAGE.getArg(), formattedMsg)
								.withArg(RedisArg.SSMSG.getArg(), ssMsg)
								.send(RedisChannels.FRIEND_CHAT.getChannelName(), jedis);
					}
				}
			});
		}
	}

}
