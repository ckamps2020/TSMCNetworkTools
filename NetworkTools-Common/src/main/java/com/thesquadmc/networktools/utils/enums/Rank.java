package com.thesquadmc.networktools.utils.enums;

/**
 * Provies a list of ranks on the server
 */
public enum Rank {

    OWNER(1, "&c&lOWNER"),
    DEVELOPER(2, "&c&lDEV"),
    MANAGER(3, "&c&lMANAGER"),
    YOUTUBE(4, "&f&lYOU&cTube"),
    ADMIN(5, "&c&lADMIN"),
    SRMOD(6, "&5&lSR MOD"),
    MOD(7, "&5MOD"),
    HELPER(8, "&b&lCHAT MOD"),
    TRAINEE(9, "&a&lTRAINEE"),
    DEFAULT(10, "&7Default");

    private final int priority;
    private final String prefix;

    Rank(int priority, String prefix) {
        this.priority = priority;
        this.prefix = prefix;
    }

    public int getPriority() {
        return priority;
    }

    public String getPrefix() {
        return prefix;
    }
}
