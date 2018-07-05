package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.objects.logging.chatlogs.Log;
import me.thesquadmc.objects.logging.chatlogs.LogType;
import me.thesquadmc.objects.logging.chatlogs.LogUser;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FilterListener implements Listener {

	private final Main main;
	private List<UUID> slowchat = new ArrayList<>();

	public FilterListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		String msg = e.getMessage();
		if (StringUtils.serverCommand(player, msg.toUpperCase())) {
			e.setCancelled(true);
			return;
		}
		if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
			if (StringUtils.shouldFilter(msg)) {
				e.setCancelled(true);
				player.sendMessage(CC.translate("&e&lFILTER &6■ &7You are not allowed to say that!"));
			}
		}

        if (!e.isCancelled()) {
            LogUser.fromPlayer(player).getLogs().add(Log.create(LogType.COMMAND, e.getMessage(), Bukkit.getServerName()));
        }
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (!PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
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
						if (StringUtils.isFiltered(msg)) {
							e.setCancelled(true);
							player.sendMessage(CC.translate("&e&lFILTER &6■ &7You are not allowed to say that!"));
							return;
						}
						StringUtils.lastMsg.put(player.getUniqueId(), msg);
						if (StringUtils.getLogs().containsKey(player.getName())) {
							if (StringUtils.getLogs().get(player.getName()).size() >= 50) {
								for (int i = 0; i < 5; i++) {
									StringUtils.getLogs().get(player.getName()).remove(0);
								}
							}
							StringUtils.getLogs().get(player.getName()).add(msg);
						} else {
							ArrayList<String> list = new ArrayList<>();
							list.add(msg);
							StringUtils.getLogs().put(player.getName(), list);
						}
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
					e.setMessage(msg);
					if (StringUtils.lastMsg.containsKey(player.getUniqueId())) {
						if (msg.equalsIgnoreCase(StringUtils.lastMsg.get(player.getUniqueId()))) {
							e.setCancelled(true);
							player.sendMessage(CC.translate("&e&lFILTER &6■ &7You are not allowed to send the same message twice!"));
							return;
						}
					}
					if (StringUtils.isFiltered(msg)) {
						e.setCancelled(true);
						player.sendMessage(CC.translate("&e&lFILTER &6■ &7You are not allowed to say that!"));
					}
					StringUtils.lastMsg.put(player.getUniqueId(), msg);
					if (StringUtils.getLogs().containsKey(player.getName())) {
						if (StringUtils.getLogs().get(player.getName()).size() >= 50) {
							for (int i = 0; i < 5; i++) {
								StringUtils.getLogs().get(player.getName()).remove(0);
							}
						}
						StringUtils.getLogs().get(player.getName()).add(msg);
					} else {
						ArrayList<String> list = new ArrayList<>();
						list.add(msg);
						StringUtils.getLogs().put(player.getName(), list);
					}
				}
			} else {
				e.setCancelled(true);
				player.sendMessage(CC.translate("&e&lCHAT &6■ &7The chat is currently silenced!"));
			}
		}

		if (!e.isCancelled()) {
			LogUser.fromPlayer(player).getLogs().add(Log.create(LogType.CHATLOG, e.getMessage(), Bukkit.getServerName()));
		}
	}

}
