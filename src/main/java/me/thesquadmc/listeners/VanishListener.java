package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.commands.StaffmodeCommand;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.enums.UpdateType;
import me.thesquadmc.utils.nms.TitleUtils;
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
						TitleUtils.sendActionBarToPlayer("&e&lVanished &7and nicknamed as &e&l" + player.getName(), player);
					} else {
						TitleUtils.sendActionBarToPlayer("&7Vanish is &e&lenabled", player);
					}
				} else if (tempData.isNicknamed()) {
					TitleUtils.sendActionBarToPlayer("&7Nicked as &e&l" + player.getName() + "&7", player);
				} else if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
					TitleUtils.sendActionBarToPlayer("&7Staffmode is &e&lenabled", player);
				} else if (tempData.isVanished()) {
					TitleUtils.sendActionBarToPlayer("&7Vanish is &e&lenabled", player);
				}
			}
		}
	}

}
