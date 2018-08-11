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
        if (name.startsWith(FACTIONS.name())) {
            return FACTIONS;

        } else if (name.startsWith(SKYBLOCK.name())) {
            return SKYBLOCK;

        } else if (name.startsWith(PRISON.name())) {
            return PRISON;

        } else if (name.startsWith(TROLLWARS.name())) {
            return TROLLWARS;

        } else if (name.startsWith(HUB.name())) {
            return HUB;

        } else {
            return OTHER;
        }
    }}
