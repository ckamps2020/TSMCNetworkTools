package me.thesquadmc.listeners;

import me.thesquadmc.commands.LaunchCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public final class LaunchListener implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() != null && e.getEntity().getType() == EntityType.PLAYER) {
			Player player = (Player) e.getEntity();
			if (LaunchCommand.getLaunched().contains(player.getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

}
