package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.handlers.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class VanishListener implements Listener {

	private final Main main;

	public VanishListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.SEC) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
				if (tempData.isYtVanishEnabled()) {
					if (tempData.isNicknamed()) {
						PlayerUtils.sendActionBarToPlayer("&e&lVanished &7and nicknamed as &e&l" + player.getName(), player);
					} else {
						PlayerUtils.sendActionBarToPlayer("&7Vanish is &e&lenabled", player);
					}
				} else if (tempData.isNicknamed()) {
					PlayerUtils.sendActionBarToPlayer("&7Nicked as &e&l" + player.getName() + "&7", player);
				}
			}
		}
	}

}
