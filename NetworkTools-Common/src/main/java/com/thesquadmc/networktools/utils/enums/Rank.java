package com.thesquadmc.networktools.utils.enums;

/**
 * Provies a list of ranks on the server
 */
public enum Rank {

    DEFAULT("DEFAULT", 1),
    TRAINEE("TRAINEE", 2),
    HELPER("HELPER", 3),
    MOD("MOD", 4),
    SRMOD("SRMOD", 5),
    ADMIN("ADMIN", 6),
    YOUTUBE("YOUTUBE", 7),
    MANAGER("MANAGER", 8),
    DEVELOPER("DEVELOPER", 9),
    OWNER("OWNER", 10),;

    private String name;
    private int priority;

    Rank(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

}
