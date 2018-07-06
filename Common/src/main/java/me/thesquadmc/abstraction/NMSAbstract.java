package me.thesquadmc.abstraction;

import me.thesquadmc.utils.server.ServerProperty;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Represents the central class managing all NMS-based actions and features
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface NMSAbstract {

	/**
	 * Get the minimum version supported by this NMSAbstract instance
	 * 
	 * @return the minimum version
	 */
    String getVersionMin();

	/**
	 * Get the maximum version supported by this NMSAbstract instance
	 * 
	 * @return the maximum version
	 */
    String getVersionMax();

	/**
	 * Get the ping of the specified player
	 * 
	 * @param player the player whose ping to retrieve
	 * @return the player's ping
	 */
    int getPing(Player player);

	/**
	 * Get the {@link MojangGameProfile} of the specified player
	 * 
	 * @param player the player whose game profile to fetch
	 * @return the player's game profile
	 */
    MojangGameProfile getGameProfile(OfflinePlayer player);

	/**
	 * Get an instance of {@link ProfileProperty} that represents the provided player's
	 * skin retrieved from Mojang's servers
	 * 
	 * @param player the player whose skin property to fetch
	 * @return the resulting profile property
	 * 
	 * @see #getSkinProperty(String)
	 */
    ProfileProperty getSkinProperty(OfflinePlayer player);

	/**
	 * Get an instance of {@link ProfileProperty} that represents the provided player's
	 * skin retrieved from Mojang's server
	 * 
	 * @param name the name of the player whose skin property to fetch
	 * @return the resulting profile property
	 * 
	 * @see #getSkinProperty(OfflinePlayer)
	 */
    ProfileProperty getSkinProperty(String name);

	/**
	 * Create a new {@link ProfileProperty} with a given name, value and signature
	 * 
	 * @param name the name of the property
	 * @param value the property's value
	 * @param signature the property's signature
	 * 
	 * @return the resulting profile property
	 */
    ProfileProperty createNewProperty(String name, String value, String signature);

	/**
	 * Create and return a new {@link HumanNPC} instance
	 * 
	 * @param location the location at which the NPC is positioned
	 * @param name the custom name of the NPC
	 * @param skin the name of the player whose skin should be shown by the NPC
	 * 
	 * @return the newly created HumanNPC instance
	 */
    HumanNPC createHumanNPC(Location location, String name, String skin);

	/**
	 * Create and return a new {@link MobNPC} instance
	 * 
	 * @param name the custom name of the NPC
	 * @param displayName the display name of the NPC (displayed above its head)
	 * @param type this NPC's entity type
	 * @param ai whether the NPC should have AI enabled or not
	 * 
	 * @return the newly created MobNPC instance
	 */
    MobNPC createMobNPC(String name, String displayName, EntityType type, boolean ai);

	/**
	 * Save the server properties file
	 */
    void savePropertiesFile();

	/**
	 * Set and update a server property
	 * 
	 * @param property the property to set
	 * @param value the property value to set
	 */
    <T> void setServerProperty(ServerProperty<T> property, T value);

	/**
	 * Set whether an entity is enabled or not
	 * <p>
	 * <b>NOTE:</b> If the codebase is written for Minecraft 1.9+, see
	 * {@code LivingEntity#setAI(boolean)} as a proper alternative
	 * 
	 * @param entity the entity whose AI to set
	 * @param ai the new state
	 */
    void setAI(LivingEntity entity, boolean ai);

	/**
	 * Get an instance of the {@link BossBarManager}
	 * 
	 * @return the boss bar manager
	 */
    BossBarManager getBossBarManager();

	/**
	 * Set the direction in which the provided entity should look
	 * 
	 * @param entity the entity whose direction to change
	 * @param pitch the pitch to set
	 * @param yaw the yaw to set
	 */
    void setLookDirection(Entity entity, float pitch, float yaw);

	/**
	 * Set the direction in which the provided entity's head should be rotated
	 * 
	 * @param entity the entity whose head direction to change
	 * @param yaw the yaw to set
	 */
    void setHeadYaw(LivingEntity entity, float yaw);

	/**
	 * Send a title to the specified player
	 * <p>
	 * <b>NOTE:</b> If the codebase is written for Minecraft 1.11+, see
	 * {@code Player#sendTitle(String, String, int, int, int)} as a proper alternative
	 * 
	 * @param player the player to whom the title should be sent
	 * @param title the title to send
	 * @param subtitle the subtitle to send
	 * @param in the title's fade in time (in ticks)
	 * @param stay the title's stay time (in ticks)
	 * @param out the title' fade out time (in ticks)
	 */
    void sendTitle(Player player, String title, String subtitle, int in, int stay, int out);

	/**
	 * Send a title to all players on the server
	 * 
	 * @param title the title to send
	 * @param subtitle the subtitle to send
	 * @param in the title's fade in time (in ticks)
	 * @param stay the title's stay time (in ticks)
	 * @param out the title' fade out time (in ticks)
	 */
    void broadcastTitle(String title, String subtitle, int in, int stay, int out);

	/**
	 * Send an action bar to the specified player
	 * 
	 * @param player the player to whom the action bar should be sent
	 * @param text the text to send
	 */
    void sendActionBar(Player player, String text);

	/**
	 * Send an action bar to all players on the server
	 * 
	 * @param text the text to send
	 */
    void broadcastActionBar(String text);

	/**
	 * Send a header/footer tab list to the specified player
	 * 
	 * @param player the player to whom the tab list should be sent
	 * @param header the header to display. Supports colour codes
	 * @param footer the footer to display. Supports colour codes
	 */
    void sendTabList(Player player, String header, String footer);

	/**
	 * Send a header/footer tab list to all players on the server
	 * 
	 * @param header the header to display. Supports colour codes
	 * @param footer the footer to display. Supports colour codes
	 */
    void broadcastTabList(String header, String footer);

	/**
	 * Force a player to sit
	 * 
	 * @param player the player to sit
	 */
    void sit(Player player);

	/**
	 * Force a player to stand (will not affect players who are not sitting)
	 * 
	 * @param player the player to stand
	 */
    void stand(Player player);

	/**
	 * Get a set of all players who are actively sitting
	 * 
	 * @return all sitting players
	 */
    Set<Player> getSitting();

	/**
	 * Send a world border packet to the specified player with the provided distance away
	 * from the border to display a warning on the screen
	 * 
	 * @param player the player to whom the world border should be send
	 * @param warningBlocks the distance from the border to start showing warnings
	 */
    void sendWorldBorder(Player player, int warningBlocks);

	/**
	 * Convert an ItemStack to Base64
	 * 
	 * @param item the item to convert
	 * @return the resulting Base64 string
	 */
    String toBase64(ItemStack item);

	/**
	 * Convert a Base64 string to an ItemStack
	 * 
	 * @param data the Base64 item data
	 * @return the resulting ItemStack. null if invalid
	 */
    ItemStack fromBase64(String data);

	/**
	 * Set the type of monster egg represented by the provided ItemStack
	 * 
	 * @param item the monster egg item to update
	 * @param type the type of entity to set
	 * 
	 * @return the updated ItemStack
	 */
    ItemStack setMonsterEggType(ItemStack item, EntityType type);

}