package me.thesquadmc.listeners;

import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class FilterListener implements Listener {

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
			if (StringUtils.shouldFilter(e.getMessage())) {
				e.setCancelled(true);
				player.sendMessage(StringUtils.msg("&e&lFILTER &6■ &7You are not allowed to say that!"));
			}
		}
	}

}
