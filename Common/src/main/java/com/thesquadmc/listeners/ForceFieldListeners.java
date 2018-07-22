package com.thesquadmc.listeners;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.abstraction.Sounds;
import com.thesquadmc.player.PlayerSetting;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.enums.UpdateType;
import com.thesquadmc.utils.handlers.UpdateEvent;
import com.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;

public final class ForceFieldListeners implements Listener {

    private final NetworkTools networkTools;

    public ForceFieldListeners(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getUpdateType() == UpdateType.HALF_SEC) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (TSMCUser.fromPlayer(player).getSetting(PlayerSetting.FORCEFIELD)) {
                    List<Player> players = PlayerUtils.getNearbyPlayers(player.getLocation(), 8);
                    for (Player p : players) {
                        if (p.getName() != null) {
                            if (!PlayerUtils.isEqualOrHigherThen(p, Rank.MOD)) {
                                double x = p.getLocation().getX();
                                double y = p.getLocation().getY();
                                double z = p.getLocation().getZ();
                                double xx = player.getLocation().getX();
                                double yy = player.getLocation().getY();
                                double zz = player.getLocation().getZ();
                                double finalX = 0;
                                double finalY = 0;
                                double finalZ = 0;
                                if (x >= xx) {
                                    finalX = 0.8;
                                } else if (x <= xx) {
                                    finalX = -0.8;
                                }
                                if (y >= yy) {
                                    finalY = 0.7;
                                } else if (y <= yy) {
                                    finalY = -0.7;
                                }
                                if (z >= zz) {
                                    finalZ = 0.8;
                                } else if (z <= zz) {
                                    finalZ = -0.8;
                                }
                                p.setVelocity(new Vector(finalX, finalY, finalZ));
                                p.playSound(p.getLocation(), Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0f, 1.0f);
                            }
                        }
                    }
                }
            }
        }
    }

}
