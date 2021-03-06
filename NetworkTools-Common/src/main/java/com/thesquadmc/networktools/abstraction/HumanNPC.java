package com.thesquadmc.networktools.abstraction;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a player NPC which may be spawned within the world and interacted with
 *
 * @author Parker Hawke - 2008Choco
 */
public interface HumanNPC {

    /**
     * Spawn this NPC in the world for the provided players
     *
     * @param players the players who will see this NPC
     */
    void spawn(Player... players);

    /**
     * Destroy this NPC for the provided players
     *
     * @param players the players who will have the NPC removed
     */
    void destroy(Player... players);

    /**
     * Get this NPC's ID
     *
     * @return the NPC ID
     */
    int getId();

    /**
     * Get the location at which this NPC resides
     *
     * @return the NPC location
     */
    Location getLocation();

    /**
     * Get this NPC's display name
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Get the String representation of the player skin used by this NPC
     *
     * @return the NPC's skin
     */
    String getPlayerSkin();

    /**
     * Get the net.minecraft.server Object wrapped by this instance
     *
     * @return the underlying NMS instance
     */
    Object getHandle();

}