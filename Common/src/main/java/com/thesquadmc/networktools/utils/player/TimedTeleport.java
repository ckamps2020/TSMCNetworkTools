package com.thesquadmc.networktools.utils.player;

import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class TimedTeleport implements Runnable {

    private static final double MOVE_CONSTANT = 0.3;

    private final Player player;
    private final Player targetPlayer;
    private final Location target;

    private final long started;
    private final long delay;

    // Locations had issues comparing (related to rounding)
    private final long x;
    private final long y;
    private final long z;
    private final boolean canMove;

    private int task;
    private double health;

    private Runnable whenComplete;

    /**
     * Teleports a player to a location with a delay. The teleport
     * is cancelled if the player moves around.
     *
     * @param player       player to teleport
     * @param targetPlayer the player teleporting to
     * @param target       the location teleporting to
     * @param delay        how long of a delay there is in seconds
     */
    private TimedTeleport(Player player, Player targetPlayer, Location target, int delay, Runnable whenComplete) {
        this.player = player;
        this.targetPlayer = targetPlayer;
        this.target = target;

        this.started = System.currentTimeMillis();
        this.delay = delay * 1000;

        this.health = player.getHealth();
        this.x = Math.round(player.getLocation().getX() * MOVE_CONSTANT);
        this.y = Math.round(player.getLocation().getY() * MOVE_CONSTANT);
        this.z = Math.round(player.getLocation().getZ() * MOVE_CONSTANT);

        this.canMove = player.hasPermission("essentials.teleport.timer.move");
        this.whenComplete = whenComplete;

        if (player.hasPermission("essentials.teleport.instant")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(NetworkTools.getInstance(), new DelayedTeleportTask());

        } else {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(NetworkTools.getInstance(), this, 20, 20).getTaskId();

        }
    }

    @Override
    public void run() {
        if (isOnline(player) || target == null) {
            cancelTimer(false);
            return;
        }

        Location loc = player.getLocation();
        if (!canMove && (hasMoved(loc.getX(), x) || hasMoved(loc.getY(), y) || hasMoved(loc.getZ(), z) || player.getHealth() < health)) {
            // user moved, cancel teleport
            cancelTimer(true);
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(NetworkTools.getInstance(), new DelayedTeleportTask());
    }

    private class DelayedTeleportTask implements Runnable {
        @Override
        public void run() {
            health = player.getHealth();  // in case user healed, then later gets injured
            final long now = System.currentTimeMillis();

            if (now > (started + delay)) {
                cancelTimer(false);

                if (LocationUtil.isBlockUnsafeForUser(player, target)) {
                    player.teleport(LocationUtil.getSafeDestination(player, target), TeleportCause.PLUGIN);

                } else {
                    player.teleport(target, TeleportCause.PLUGIN);
                }

                whenComplete.run();

            }
        }
    }

    private boolean isOnline(Player player) {
        return player == null || !player.isOnline();
    }

    private boolean hasMoved(double now, double original) {
        return Math.round(now * MOVE_CONSTANT) != original;
    }

    public void cancelTimer(boolean notifyUser) {
        if (task == -1) {
            return;
        }

        Bukkit.getScheduler().cancelTask(task);
        task = -1;

        if (notifyUser) {
            player.sendMessage("pendingTeleportCancelled");

            if (targetPlayer != null && !targetPlayer.equals(player)) {
                targetPlayer.sendMessage("pendingTeleportCancelled");
            }
        }
    }

    public static class Builder {

        private Player player;
        private Location target;
        private Player targetPlayer;
        private int delay = 5;
        private Runnable whenComplete;

        public Builder(Player player, Location target) {
            this.player = player;
            this.target = target;
        }

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public Builder target(Location target) {
            this.target = target;
            return this;
        }

        public Builder targetPlayer(Player targetPlayer) {
            this.targetPlayer = targetPlayer;
            return this;
        }

        public Builder delay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder whenComplete(Runnable whenComplete) {
            this.whenComplete = whenComplete;
            return this;
        }

        public TimedTeleport build() {
            return new TimedTeleport(player, targetPlayer, target, delay, whenComplete);
        }
    }
}