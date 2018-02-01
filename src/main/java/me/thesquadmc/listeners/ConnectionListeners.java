package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ConnectionListeners implements Listener {

	private final Main main;

	public ConnectionListeners(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		TempData tempData = new TempData();
		Player player = e.getPlayer();
		PlayerUtils.unfreezePlayer(player);
		main.getTempDataManager().registerNewData(player.getUniqueId(), tempData);
		Bukkit.getScheduler().runTaskLater(main, () -> {
			for (Player p : Bukkit.getOnlinePlayers()) {
				TempData t = main.getTempDataManager().getTempData(p.getUniqueId());
				if (t.isVanished()) {
					PlayerUtils.hidePlayerSpectatorStaff(p);
				} else if (t.isYtVanishEnabled()) {
					PlayerUtils.hidePlayerSpectatorYT(p);
				}
			}
		}, 3L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (tempData.isYtVanishEnabled()) {
				p.showPlayer(player);
			}
		}
		main.getTempDataManager().unregisterNewData(player.getUniqueId());
	}

}
