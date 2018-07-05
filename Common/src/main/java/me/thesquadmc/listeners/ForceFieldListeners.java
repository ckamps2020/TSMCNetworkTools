package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.Sounds;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.UpdateType;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;

public final class ForceFieldListeners implements Listener {

	private final Main main;

	public ForceFieldListeners(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.SEC) {
            main.getQueueManager().getSoloBW().remove("null");
            main.getQueueManager().getSoloBW().remove(null);
		} else if (e.getUpdateType() == UpdateType.HALF_SEC) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (TSMCUser.fromPlayer(player).hasForcefield()) {
					List<Player> players = PlayerUtils.getNearbyPlayers(player.getLocation(), 8);
					for (Player p : players) {
						if (p.getName() != null) {
							if (!PlayerUtils.isEqualOrHigherThen(p, Rank.YOUTUBE)) {
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
