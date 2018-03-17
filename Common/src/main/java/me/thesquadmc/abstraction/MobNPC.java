package me.thesquadmc.abstraction;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import me.thesquadmc.objects.Hologram;

/**
 * Represents a non-player NPC which may be spawned within the world and interacted with
 * 
 * @author Parker Hawke - 2008Choco
 */
public interface MobNPC {
	
	/**
	 * Spawn this NPC in the world at the provided location
	 * 
	 * @param location the location at which to spawn the NPC
	 */
	public void spawn(Location location);
	
	/**
	 * Destroy this NPC. After the invocation of this method, methods for this instance
	 * will no longer function as intended
	 */
	public void destroy();
	
	/**
	 * Force this entity to walk to the provided location at a given speed
	 * 
	 * @param location the location to which the NPC should walk
	 * @param speed the walking speed
	 */
	public void walk(Location location, double speed);
	
	/**
	 * Get the hologram above this NPC (its "nametag", so to speak)
	 * 
	 * @return the hologram
	 */
	public Hologram getHologram();
	
	/**
	 * Get this NPC's name
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Get this NPC's display name
	 * 
	 * @return the display name
	 */
	public String getDisplayName();
	
	/**
	 * Get the entity type represented by this NPC
	 * 
	 * @return the entity's type
	 */
	public EntityType getEntityType();
	
	/**
	 * Check whether this NPC has AI or not
	 * 
	 * @return true if AI is active, false otherwise
	 */
	public boolean hasAI();
	
	/**
	 * Get the underlying Bukkit {@link Entity} represented by this NPC
	 * 
	 * @return the underlying entity
	 */
	public Entity getEntity();
	
}