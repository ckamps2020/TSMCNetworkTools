package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FilterListener implements Listener {

	private final Main main;
	private List<UUID> slowchat = new ArrayList<>();

	public FilterListener(Main main) {
		this.main = main;
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
			if (!main.isChatSilenced()) {
				if (main.getChatslow() != 0) {
					if (!slowchat.contains(player.getUniqueId())) {
						String msg = e.getMessage();
						if (StringUtils.lastMsg.containsKey(player.getUniqueId())) {
							if (msg.equalsIgnoreCase(StringUtils.lastMsg.get(player.getUniqueId()))) {
								e.setCancelled(true);
								player.sendMessage(CC.translate("&e&lFILTER &6■ &7You are not allowed to send the same message twice!"));
								return;
							}
						}
						String message = StringUtils.fixStringForCaps(msg);
						e.setMessage(message);
						if (StringUtils.shouldFilter(msg)) {
							e.setCancelled(true);
							player.sendMessage(CC.translate("&e&lFILTER &6■ &7You are not allowed to say that!"));
							return;
						}
						StringUtils.lastMsg.put(player.getUniqueId(), message);
						slowchat.add(player.getUniqueId());
						Bukkit.getScheduler().runTaskLater(main, new Runnable() {
							@Override
							public void run() {
								slowchat.remove(player.getUniqueId());
							}
						}, main.getChatslow() * 20L);
					} else {
						e.setCancelled(true);
						player.sendMessage(CC.translate("&e&lCHAT &6■ &7Please slow down your chatting!"));
					}
				} else {
					String msg = e.getMessage();
					String message = msg.toLowerCase();
					e.setMessage(message);
					if (StringUtils.shouldFilter(msg)) {
						e.setCancelled(true);
						player.sendMessage(CC.translate("&e&lFILTER &6■ &7You are not allowed to say that!"));
					}
				}
			} else {
				e.setCancelled(true);
				player.sendMessage(CC.translate("&e&lCHAT &6■ &7The chat is currently silenced!"));
			}
		}
	}

}
