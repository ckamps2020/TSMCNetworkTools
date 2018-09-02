package com.thesquadmc.networktools.utils.inventory.builder;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Optional;

public class MenuListener implements Listener {

    private final MenuManager menuManager;

    public MenuListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Optional<Menu> menu = menuManager.getMenu(e.getInventory());

        if (!menu.isPresent()) {
            return;
        }

        e.setCancelled(true);
        MenuItem item = menu.get().getMenuItem(e.getSlot());

        if (item == null) {
            return;
        }

        item.onClick((Player) e.getWhoClicked(), e.getInventory(), e.getClick());
    }

    @EventHandler
    public void on(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }

        Optional<Menu> menu = menuManager.getMenu(e.getInventory());
        if (!menu.isPresent()) {
            return;
        }

        menu.get().getOnClose().accept((Player) e.getPlayer());
    }
}
