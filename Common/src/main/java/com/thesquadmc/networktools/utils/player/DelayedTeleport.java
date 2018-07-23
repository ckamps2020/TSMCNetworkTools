package com.thesquadmc.networktools.utils.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayedTeleport extends BukkitRunnable {

    private final Player player;
    private final Location location;
    private final int delay;

    public DelayedTeleport(Player player, Location location, int delay) {
        this.player = player;
        this.location = location;
        this.delay = delay;
    }

    @Override
    public void run() {


    }
}
