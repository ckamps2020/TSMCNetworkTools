package com.thesquadmc.networktools.utils.enums;

public class EnumUtil {

    private EnumUtil() {
        //A utility class
    }

    /**
     * Gets the enum for the class
     *
     * @param enumType the class of the enum to query
     * @param name     the name of the enum
     * @param <T>      the type of enum
     * @return the enum, null if not found
     */
    public static <T extends Enum<?>> T getEnum(final Class<T> enumType, final String name) {
        if (enumType == null) return null;

        for (T enumn : enumType.getEnumConstants()) {
            if (enumn.name().equalsIgnoreCase(name)) {
                return enumn;
            }
        }
        return null;
    }
}