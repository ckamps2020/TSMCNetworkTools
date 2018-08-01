package com.thesquadmc.networktools.player;

import com.thesquadmc.networktools.utils.server.ServerType;

/**
 * Statistics for each server
 */
public class ServerStatistics {

    private final String serverName;
    private final ServerType type;

    private long playtime;
    private int logins;
    private int blocksBroken;

    public ServerStatistics(String serverName, ServerType type) {
        this.serverName = serverName;
        this.type = type;
    }

    public ServerStatistics(String serverName, ServerType type, long playtime, int logins, int blocksBroken) {
        this.serverName = serverName;
        this.type = type;
        this.playtime = playtime;
        this.logins = logins;
        this.blocksBroken = blocksBroken;
    }

    public String getServerName() {
        return serverName;
    }

    public ServerType getType() {
        return type;
    }

    /**
     * @return Gets how long the player has been playing
     * on a specifc server in milliseconds
     */
    public long getPlaytime() {
        return playtime;
    }

    /**
     * Sets how long the player has been playing on
     * a specific server
     *
     * @param playtime how long the player has been playing for in milliseconds
     */
    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    /**
     * @return Gets the amount of times this player has logged into a specific server
     */
    public int getLogins() {
        return logins;
    }

    /**
     * Sets the amount of times this player has logged into a specific server
     *
     * @param logins how many times the player has logged in
     */
    public void setLogins(int logins) {
        this.logins = logins;
    }

    /**
     * @return Gets how many blocks this player has broken on this specific server
     */
    public int getBlocksBroken() {
        return blocksBroken;
    }

    /**
     * Sets how many blocks this player has broken on this specific server
     *
     * @param blocksBroken how many blocks the player has broken
     */
    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }
}
