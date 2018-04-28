package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.MojangGameProfile;
import me.thesquadmc.objects.PlayerSetting;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ConnectionListeners implements Listener {

	private final Main main;

	public ConnectionListeners(Main main) {
		this.main = main;
	}

	@EventHandler
	public void on(AsyncPlayerPreLoginEvent e) {
		if (main.getMcLeaksAPI().checkAccount(e.getUniqueId()).isMCLeaks()) {
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			Bukkit.getLogger().info("Prevented " + e.getName() + " from logging in as it is an MCLeaks Account");

			//Bukkit.getScheduler().runTask(main, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + e.getName() + " Compromised Account"));
			/*for (Player p : Bukkit.getOnlinePlayers()) {
				if (PlayerUtils.isEqualOrHigherThen(p, Rank.TRAINEE)) {
					p.sendMessage(CC.translate("&8[&4&lAnitCheat&8] &4[MCLeaks] &f" + e.getName() + " is a verified MCLeaks account!"));
				}
			} */
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		TSMCUser user = TSMCUser.fromPlayer(player);
		user.setLoginTime(StringUtils.getDate());
		MojangGameProfile profile = main.getNMSAbstract().getGameProfile(player);
		profile.getPropertyMap().values().forEach(p -> {
			user.setSkinKey(p.getValue());
			user.setSignature(p.getSignature());
		});

		PlayerUtils.unfreezePlayer(player);
		Bukkit.getScheduler().runTaskLater(main, () -> {
			if (Bukkit.getServerName().toUpperCase().startsWith("MG")
					|| Bukkit.getServerName().toUpperCase().startsWith("FACTIONS")
					|| Bukkit.getServerName().toUpperCase().startsWith("HUB")
					|| Bukkit.getServerName().toUpperCase().startsWith("CREATIVE")) {
				if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
					player.chat("/ev");
				}
			}
			if (Bukkit.getServerName().toUpperCase().startsWith("MG")
					|| Bukkit.getServerName().toUpperCase().startsWith("FACTIONS")
					|| Bukkit.getServerName().toUpperCase().startsWith("HUB")
					|| Bukkit.getServerName().toUpperCase().startsWith("PRISON")
					|| Bukkit.getServerName().toUpperCase().startsWith("SKYBLOCK")
					|| Bukkit.getServerName().toUpperCase().startsWith("CREATIVE")) {
				if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
					player.chat("/vanish");
				}
			}

			if (!Main.getMain().getSig().equalsIgnoreCase("NONE")) {
				PlayerUtils.setSameSkin(player);
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				TSMCUser targetUser = TSMCUser.fromPlayer(p);
				if (targetUser.isVanished()) {
					PlayerUtils.hidePlayerSpectatorStaff(p);
				} else if (targetUser.isYtVanished()) {
					PlayerUtils.hidePlayerSpectatorYT(p);
				}
			}
		}, 3L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (StringUtils.lastMsg.containsKey(player.getUniqueId())) {
			StringUtils.lastMsg.remove(player.getUniqueId());
		}
		
		player.performCommand("party leave");
		
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (TSMCUser.fromPlayer(p).isYtVanished()) {
				p.showPlayer(player);
			}
		}
		
		TSMCUser.unloadUser(player);
	}

}
