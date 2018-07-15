package me.thesquadmc.player.local;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class LocalPlayer {

    private final UUID uuid;
    private final Map<String, Long> usedKits = Maps.newHashMap();
    private String username;
    private String nickname;

    public LocalPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.username = name;
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
