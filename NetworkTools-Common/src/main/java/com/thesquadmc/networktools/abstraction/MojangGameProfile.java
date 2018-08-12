package com.thesquadmc.networktools.abstraction;

import com.google.common.collect.Multimap;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a wrapper for a player's game profile. It is from here that properties
 * may be added or removed, and the related player's name may be set
 *
 * @author Parker Hawke - 2008Choco
 */
public interface MojangGameProfile {

    /**
     * Get the UUID of this game profile
     *
     * @return this profile's UUID
     */
    UUID getID();

    /**
     * Get the name of the player represented by this game profile. This name may not
     * necessarily be equal to the result of {@link Player#getName()}, therefore it
     * should not be depended on unless absolutely necessary
     *
     * @return the name of the player
     */
    String getName();

    /**
     * Set and update the name of the player represented by this game profile
     *
     * @param name the name to set
     */
    void setName(String name);

    /**
     * Add a property to this game profile
     *
     * @param propertyName the name of the property (should match {@link ProfileProperty#getName()})
     * @param property     the property to add
     */
    void addProperty(String propertyName, ProfileProperty property);

    /**
     * Remove all properties with the provided key from this game profile
     *
     * @param property the property key to remove
     */
    void removeProperty(String property);

    /**
     * Get a copy of this game profile's properties
     *
     * @return the profile properties
     */
    Multimap<String, ProfileProperty> getPropertyMap();

    /**
     * Check whether this game profile is a legacy profile or not
     *
     * @return true if legacy, false otherwise
     */
    boolean isLegacy();

}