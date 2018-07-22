package com.thesquadmc.player.local;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LocalPlayerManager {

    private Map<UUID, LocalPlayer> playerMap = new ConcurrentHashMap<>();

    public void addPlayer(UUID uuid, LocalPlayer player) {
        playerMap.put(uuid, player);
    }

    public LocalPlayer removePlayer(Player player) {
        return playerMap.remove(player.getUniqueId());
    }

    public LocalPlayer removePlayer(UUID uuid) {
        return playerMap.remove(uuid);
    }

    public LocalPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public LocalPlayer getPlayer(UUID uuid) {
        return playerMap.get(uuid);
    }

    public Map<UUID, LocalPlayer> getPlayerMap() {
        return Collections.unmodifiableMap(playerMap);
    }
}
