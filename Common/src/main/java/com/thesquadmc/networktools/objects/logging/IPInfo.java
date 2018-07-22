package com.thesquadmc.networktools.objects.logging;

import com.thesquadmc.networktools.networking.mongo.UserDatabase;
import org.bson.Document;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

/**
 * This class represent an IP that
 * a player has logged in with
 */
public class IPInfo {

    private final String ip;
    private final Date firstJoined;

    private Date lastJoined;
    private int count = 1;

    public IPInfo(String ip, Date firstJoined, Date lastJoined) {
        this.ip = ip;
        this.firstJoined = firstJoined;
        this.lastJoined = lastJoined;
    }

    public IPInfo(String ip, Date firstJoined, Date lastJoined, int count) {
        this.ip = ip;
        this.firstJoined = firstJoined;
        this.lastJoined = lastJoined;
        this.count = count;
    }

    public static IPInfo fromDocument(Document document) {
        String ip = document.getString("_id");
        Date firstJoin = document.getDate(UserDatabase.FIRST_JOINED);
        Date lastJoin = document.getDate(UserDatabase.LAST_JOINED);
        Integer count = document.getInteger(UserDatabase.COUNT);

        return new IPInfo(ip, firstJoin, lastJoin, count);
    }

    /**
     * @return Gets the IP that the player logged in
     * with
     */
    public String getIP() {
        return ip;
    }

    /**
     * @return Gets the date the player first logged
     * in with this IP
     */
    public Date getFirstJoined() {
        return firstJoined;
    }

    /**
     * @return Gets the date the player last logged
     * in with this IP
     */
    public Date getLastJoined() {
        return lastJoined;
    }

    public void setLastJoined(Date lastJoined) {
        this.lastJoined = lastJoined;
    }

    /**
     * @return Gets the amount of times  the player
     * logged in with this IP
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the amount of times  the player
     * logged in with this IP
     *
     * @param count number of times logged in with
     *              the IP
     */
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof IPInfo)) {
            return false;
        }

        IPInfo info = (IPInfo) o;
        return ip.equals(info.ip) &&
                firstJoined.equals(info.firstJoined) &&
                lastJoined.equals(info.lastJoined) &&
                count == info.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, firstJoined, lastJoined, count);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}=[firstJoin = {1}, lastJoin = {2}, count = {3}]", ip, firstJoined, lastJoined, count);
    }

}
