package me.thesquadmc.abstraction;

import java.util.Set;

import org.bukkit.entity.Player;

/**
 * Represents an abstract manager for boss bars to hold messages and display different
 * percentages of progress
 * <p>
 * <b>NOTE:</b> For 1.9+ code, the use of the BossBar API is far superior and this
 * manager should be avoided at all costs due to missing API methods
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface BossBarManager {
	
	/**
	 * Set the boss bar for the provided player and set its text and percent
	 * 
	 * @param player the player for whom to display the boss bar
	 * @param text the boss bar text
	 * @param healthPercent the percentage of health to display on the bar
	 */
	public void setBar(Player player, String text, float healthPercent);
	
	/**
	 * Remove a boss bar from a player
	 * 
	 * @param player the player for whom to remove the boss bar
	 */
	public void removeBar(Player player);
	
	/**
	 * Check whether a player has a boss bar or not
	 * 
	 * @param player the player to check
	 * @return true if present, false otherwise
	 */
	public boolean hasBar(Player player);
	
	/**
	 * Teleport the boss bar to the player's position. This is used internally to constantly
	 * keep the bar as close to the player as possible
	 * 
	 * @param player the player to have their boss bar teleported
	 */
	public void teleportBar(Player player);
	
	/**
	 * Change the text of the boss bar for the provided player
	 * 
	 * @param player the player whose boss bar to change
	 * @param text the text to set
	 * 
	 * @see #updateBar(Player, String, float)
	 */
	public void updateText(Player player, String text);
	
	/**
	 * Change the health percentage of the boss bar for the provided player
	 * 
	 * @param player the player whose boss bar to change
	 * @param healthPercent the health percentage to set
	 * 
	 * @see #updateBar(Player, String, float)
	 */
	public void updateHealth(Player player, float healthPercent);
	
	/**
	 * Update the text and health percentage of the boss bar for the provided player
	 * 
	 * @param player the player whose boss bar to change
	 * @param text the text to set
	 * @param healthPercent the health percentage to set
	 */
	public void updateBar(Player player, String text, float healthPercent);
	
	/**
	 * Get a set of all players who have an active boss bar
	 * 
	 * @return all players with boss bars
	 */
	public Set<Player> getPlayers();
	
}