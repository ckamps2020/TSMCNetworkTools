package com.thesquadmc.networktools.utils.enums;

public enum Rank {

    TRAINEE("TRAINEE", 1),
    HELPER("HELPER", 2),
    MOD("MOD", 3),
    SRMOD("SRMOD", 4),
    ADMIN("ADMIN", 5),
    YOUTUBE("YOUTUBE", 6),
    MANAGER("MANAGER", 7),
    DEVELOPER("DEVELOPER", 8),
    OWNER("OWNER", 9),;

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
