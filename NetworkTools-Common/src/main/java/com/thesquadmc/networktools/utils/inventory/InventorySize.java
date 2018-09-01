package com.thesquadmc.networktools.utils.inventory;

public final class InventorySize {

    private InventorySize() { }

    public static final int ONE_LINE = 9;
    public static final int TWO_LINE = 18;
    public static final int THREE_LINE = 27;
    public static final int FOUR_LINE = 36;
    public static final int FIVE_LINE = 45;
    public static final int SIX_LINE = 54;

    public static int getSize(int size) {
        return (size + 8) / 9 * 9;
    }
}
