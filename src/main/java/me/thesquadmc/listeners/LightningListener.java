package me.thesquadmc.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LightningListener implements Listener {

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e){
		if (e.getEntity().getType() == EntityType.PLAYER) {
			if (e.getDamager().getType() == EntityType.LIGHTNING) {
				e.setCancelled(true);
			}
		}
	}

}