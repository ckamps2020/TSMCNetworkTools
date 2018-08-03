package com.thesquadmc.networktools.player.stats;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.TSMCUser;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerStatsListener implements Listener {

    private final NetworkTools plugin;
    private final Map<UUID, Long> playTime = new HashMap<>();

    public ServerStatsListener(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        playTime.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    //TODO Add AFK checks into this, when they go afk add to their playtime
    //TODO and remove them from the map, when they are no longer afk, add them back

    // Lowest priority so we add the data before the TSMCUser is unloaded
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent e) {
        long start = playTime.remove(e.getPlayer().getUniqueId());
        ServerStatistics stats = TSMCUser.fromPlayer(e.getPlayer()).getServerStatistic(Bukkit.getServerName());

        stats.setPlaytime(stats.getPlaytime() + (System.currentTimeMillis() - start));
    }
}
