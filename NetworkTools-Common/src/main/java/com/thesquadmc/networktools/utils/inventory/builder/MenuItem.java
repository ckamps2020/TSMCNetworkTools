package com.thesquadmc.networktools.utils.inventory.builder;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface MenuItem {

    /**
     * Item that shows up in the inventory
     */
    ItemStack getItem(Player player);

    /**
     * Handle when a player clicks on an item
     */
    void onClick(Player player, Inventory inventory, ClickType clickType);
}
