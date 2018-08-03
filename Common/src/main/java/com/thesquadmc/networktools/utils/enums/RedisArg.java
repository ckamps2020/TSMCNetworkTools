package com.thesquadmc.networktools.utils.enums;

public enum RedisArg {

    SERVER("SERVER"),
    PLAYER("PLAYER"),
    MESSAGE("MESSAGE"),
    LOGIN("LOGIN"),
    ORIGIN_SERVER("ORIGIN_SERVER"),
    ORIGIN_PLAYER("ORIGIN_PLAYER"),
    UUID("UUID"),
    ONOFF("ONOFF"),

    PROXIES("PROXIES"),
    COUNT("COUNT"),
    UPTIME("UPTIME"),
    TPS("TPS"),
    MEMORY("MEMORY"),

    DATE("DATE"),
    REASON("REASON"),

    TRAINEE("TRAINEE"),
    HELPER("HELPER"),
    MOD("MOD"),
    SRMOD("SRMOD"),
    ADMIN("ADMIN"),
    MANAGER("MANGER"),
    DEVELOPER("DEVELOPER"),
    OWNER("OWNER"),

    FRIENDS("FRIENDS"),
    SSMSG("SSMSG"),

    SERVER_STATE("SERVER_STATE"),
    NEW_SERVER("NEW_SERVER"),;

    private String arg;

    RedisArg(String arg) {
        this.arg = arg;
    }

    @Deprecated
    public String getArg() {
        return arg;
    }

    public String getName() {
        return arg;
    }

}
