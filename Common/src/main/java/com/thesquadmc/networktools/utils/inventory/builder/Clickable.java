package com.thesquadmc.networktools.utils.inventory.builder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Clickable {

    /**
     * An action to be called upon clicking an object in the slot of a GUI. Whether the
     * ItemStack at the slot clicked is null or not will not affect the result of this
     * method
     *
     * @param player     the player who performed the click
     * @param inventory  the inventory in which was clicked (the GUI's inventory)
     * @param cursor     the ItemStack on the player's cursor. May be null
     * @param slot       the clicked inventory slot
     * @param leftClick  whether the inventory was clicked with the left mouse button or not
     * @param rightClick whether the inventory was clicked with the right mouse button or not
     * @param shiftClick whether the inventory was clicked while holding shift or not
     */
    void click(Player player, Inventory inventory, ItemStack cursor, int slot, boolean leftClick, boolean rightClick, boolean shiftClick);

}
