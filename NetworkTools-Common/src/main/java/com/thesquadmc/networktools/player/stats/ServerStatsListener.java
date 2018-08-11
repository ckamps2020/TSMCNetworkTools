package com.thesquadmc.networktools.player.stats;

import com.thesquadmc.networktools.player.TSMCUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.thesquadmc.networktools.player.stats.Stat.BLOCKS_BROKEN;
import static com.thesquadmc.networktools.player.stats.Stat.LOGINS;
import static com.thesquadmc.networktools.player.stats.Stat.PLAYTIME;

public class ServerStatsListener implements Listener {

    private final Map<UUID, Long> playTime = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerJoinEvent e) {
        playTime.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());

        ServerStatistics stats = getStats(e.getPlayer());
        stats.updateStat(LOGINS, stats.getStat(LOGINS) + 1);
    }

    //TODO Add AFK checks into this, when they go afk add to their playtime
    //TODO and remove them from the map, when they are no longer afk, add them back

    // Lowest priority so we add the data before the TSMCUser is unloaded
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent e) {
        long diff = System.currentTimeMillis() - playTime.remove(e.getPlayer().getUniqueId());

        ServerStatistics stats = getStats(e.getPlayer());
        stats.updateStat(PLAYTIME, stats.getStat(PLAYTIME) + diff);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(BlockBreakEvent e) {
        if (!e.isCancelled()) {

            ServerStatistics stats = getStats(e.getPlayer());
            stats.updateStat(BLOCKS_BROKEN, stats.getStat(BLOCKS_BROKEN) + 1);
        }
    }

    private ServerStatistics getStats(Player player) {
        return TSMCUser.fromPlayer(player).getCurrentStatistics();
    }
}
