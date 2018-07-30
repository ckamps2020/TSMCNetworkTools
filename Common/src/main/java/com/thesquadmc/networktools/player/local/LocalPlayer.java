package com.thesquadmc.networktools.player.local;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class LocalPlayer {

    private final UUID uuid;

    private final Map<String, Long> usedKits = Maps.newHashMap();
    private final Map<String, Location> homes = Maps.newHashMap();

    private String username;
    private String nickname;

    private transient UUID teleportRequester;
    private transient boolean teleportRequestHere;
    private transient Location teleportLocation;
    private transient long teleportRequestTime;

    public LocalPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.username = name;
    }

    public void requestTeleport(Player requester, boolean here) {
        teleportRequestTime = System.currentTimeMillis();
        teleportRequester = requester == null ? null : requester.getUniqueId();
        teleportRequestHere = here;
        if (requester == null) {
            teleportLocation = null;

        } else {
            teleportLocation = here ? requester.getLocation() : getPlayer().getLocation();
        }
    }

    public int getHomesSize() {
        return homes.size();
    }

    public boolean hasHome(String name) {
        return homes.containsKey(name);
    }

    public Location getHome(String name) {
        return homes.get(name);
    }

    public boolean addHome(String name, Location location) {
        Preconditions.checkNotNull(name, "Name of home cannot be null!");
        Preconditions.checkNotNull(location, "Location of home cannot be null!");

        if (homes.containsKey(name)) {
            return false;
        }

        homes.put(name, location);
        return true;
    }

    public void removeHome(String name) {
        homes.remove(name);
    }

    public UUID getTeleportRequest() {
        return teleportRequester;
    }

    public boolean isTpRequestHere() {
        return teleportRequestHere;
    }

    public Location getTpRequestLocation() {
        return teleportLocation;
    }

    public long getTeleportRequestTime() {
        return teleportRequestTime;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getUsedKit(String kitName) {
        return usedKits.getOrDefault(kitName, -1L);
    }

    public void setUsedKit(String kitName, Long timestamp) {
        usedKits.put(kitName, timestamp);
    }
}
