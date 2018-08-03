package com.thesquadmc.networktools.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class LightningListener implements Listener {

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER) {
            if (e.getDamager().getType() == EntityType.LIGHTNING) {
                e.setCancelled(true);
            }
        }
    }

}
