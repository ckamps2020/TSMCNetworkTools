package com.thesquadmc.abstraction;

/**
 * Represents a property held within a player's {@link MojangGameProfile}
 *
 * @author Parker Hawke - 2008Choco
 */
public interface ProfileProperty {

    /**
     * Get the net.minecraft.server Object wrapped by this instance
     *
     * @return the underlying NMS instance
     */
    Object getHandle();

    /**
     * Get this property's name
     *
     * @return the property name
     */
    String getName();

    /**
     * Get this property's value
     *
     * @return the property value
     */
    String getValue();

    /**
     * Get this property's signature
     *
     * @return the property signature
     */
    String getSignature();

}