package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.Sounds;
import me.thesquadmc.utils.enums.UpdateType;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TimedListener implements Listener {

	private final Main main;

	public TimedListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.MIN) {
			if (Bukkit.getServerName().toUpperCase().contains("HUB")
					|| Bukkit.getServerName().toUpperCase().contains("SKYBLOCK")
					|| Bukkit.getServerName().toUpperCase().contains("FACTIONS")
					|| Bukkit.getServerName().toUpperCase().contains("PRISON")
					|| Bukkit.getServerName().toUpperCase().contains("TROLLWARS")
					|| Bukkit.getServerName().toUpperCase().contains("CREATIVE")) {
				main.setRestartTime(main.getRestartTime() + 1);
				if (main.getRestartTime() == 720) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.kickPlayer(CC.translate("&e&lRESTART &6■ &7Daily server restart"));
					}
					Bukkit.shutdown();
				} else if (main.getRestartTime() == 715) {
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(CC.translate("&e&lRESTART &6■ &7Daily server restart in &e5 &7min"));
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sounds.NOTE_PLING.bukkitSound(), 1.0f, 1.0f);
					}
				} else if (main.getRestartTime() == 719) {
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(CC.translate("&e&lRESTART &6■ &7Daily server restart in &e1 &7min"));
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sounds.NOTE_PLING.bukkitSound(), 1.0f, 1.0f);
					}
				}
			}
		}
	}

}
