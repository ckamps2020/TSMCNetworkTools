package com.thesquadmc.networktools.utils.server;

public enum ServerType {

    HUB,
    SKYBLOCK,
    FACTIONS,
    CREATIVE,
    TROLLWARS,
    PRISON,
    OTHER;

    public static ServerType getServerType(String name) {
        if (name.contains(FACTIONS.name())) {
            return FACTIONS;

        } else if (name.contains(SKYBLOCK.name())) {
            return SKYBLOCK;

        } else if (name.contains(PRISON.name())) {
            return PRISON;

        } else if (name.contains(TROLLWARS.name())) {
            return TROLLWARS;

        } else if (name.contains(HUB.name())) {
            return HUB;

        } else {
            return OTHER;
        }
    }}
