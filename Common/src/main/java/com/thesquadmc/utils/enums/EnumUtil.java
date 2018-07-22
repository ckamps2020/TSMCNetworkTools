package com.thesquadmc.utils.enums;

public class EnumUtil {

    private EnumUtil() {
        //A utility class
    }

    public static <T extends Enum<?>> T getEnum(final Class<T> enumType, final String name) {
        for (T enumn : enumType.getEnumConstants()) {
            if (enumn.name().equalsIgnoreCase(name)) {
                return enumn;
            }
        }
        return null;
    }
}