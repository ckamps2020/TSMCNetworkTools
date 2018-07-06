package me.thesquadmc.utils.inventory.builder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface GUI {

	/**
	 * Get this GUI's underlying Bukkit {@link Inventory}
	 *
	 * @return the Bukkit Inventory instance
	 */
    Inventory getInventory();

	/**
	 * Open this GUI for the specified player
	 *
	 * @param player the player for whom to open the GUI
	 */
    void openFor(Player player);

	/**
	 * Check whether the specified slot has an associated click action or not
	 *
	 * @param slot the slot to check
	 * @return true if click action is present, false otherwise
	 */
    boolean hasClickAction(int slot);

}
