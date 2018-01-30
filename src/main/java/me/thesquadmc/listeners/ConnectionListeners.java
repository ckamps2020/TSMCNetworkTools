package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.commands.UnFreezeCommand;
import me.thesquadmc.objects.TempData;
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
		UnFreezeCommand.unfreezePlayer(player);
		main.getTempDataManager().registerNewData(player.getUniqueId(), tempData);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		main.getTempDataManager().unregisterNewData(player.getUniqueId());
	}

}
