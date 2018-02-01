package me.thesquadmc.listeners;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.Rank;
import me.thesquadmc.utils.handlers.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
		for (Player player : Bukkit.getOnlinePlayers()) {
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (tempData.isForcefieldEnabled()) {
				List<Player> players = PlayerUtils.getNearbyPlayers(player.getLocation(), 5);
				for (Player p : players) {
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
							finalX = 1;
						} else if (x <= xx) {
							finalX = -1;
						}
						if (y >= yy) {
							finalY = 0.5;
						} else if (y <= yy) {
							finalY = -0.5;
						}
						if (z >= zz) {
							finalZ = 1;
						} else if (z <= zz) {
							finalZ = -1;
						}
						p.setVelocity(new Vector(finalX, finalY, finalZ));
						p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1.0f, 1.0f);
					}
				}
			}
		}
	}

}