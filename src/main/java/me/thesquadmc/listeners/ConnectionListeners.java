package me.thesquadmc.listeners;

import com.mojang.authlib.properties.Property;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Map;

public final class ConnectionListeners implements Listener {

	private final Main main;

	public ConnectionListeners(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		TempData tempData = new TempData();
		Player player = e.getPlayer();
		CraftPlayer pl = (CraftPlayer) player;
		for (Map.Entry<String, Collection<Property>> map : pl.getProfile().getProperties().asMap().entrySet()) {
			map.getValue().forEach(value -> {
				tempData.setSkinkey(value.getValue());
				tempData.setSignature(value.getSignature());
			});
		}
		PlayerUtils.unfreezePlayer(player);
		main.getTempDataManager().registerNewData(player.getUniqueId(), tempData);
		TempData td = main.getTempDataManager().getTempData(player.getUniqueId());
		td.setRealname(player.getName());
		Bukkit.getScheduler().runTaskLater(main, () -> {
			if (!Main.getMain().getSig().equalsIgnoreCase("NONE")) {
				PlayerUtils.setSameSkin(player);
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				TempData t = main.getTempDataManager().getTempData(p.getUniqueId());
				if (t.isVanished()) {
					PlayerUtils.hidePlayerSpectatorStaff(p);
				} else if (t.isYtVanishEnabled()) {
					PlayerUtils.hidePlayerSpectatorYT(p);
				}
			}
		}, 3L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (tempData.isYtVanishEnabled()) {
				p.showPlayer(player);
			}
		}
		main.getTempDataManager().unregisterNewData(player.getUniqueId());
	}

}
