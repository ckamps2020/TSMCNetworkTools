package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.abstraction.MojangGameProfile;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.objects.Party;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ConnectionListeners implements Listener {

	private final Main main;

	public ConnectionListeners(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		TempData tempData = new TempData();
		Player player = e.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				Multithreading.runAsync(new Runnable() {
					@Override
					public void run() {
						try {
							main.getMySQL().loadFriendAccount(player.getUniqueId().toString());
							System.out.println("[NetworkTools] Loaded account for " + player.getName());
						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println("[NetworkTools] Unable to load friends for " + player.getName());
						}
					}
				});
			}
		});

		MojangGameProfile profile = main.getNMSAbstract().getGameProfile(player);
		profile.getPropertyMap().values().forEach(p -> {
			tempData.setSkinkey(p.getValue());
			tempData.setSignature(p.getSignature());
		});
		
		PlayerUtils.unfreezePlayer(player);
		main.getTempDataManager().registerNewData(player.getUniqueId(), tempData);
		TempData td = main.getTempDataManager().getTempData(player.getUniqueId());
		td.setRealname(player.getName());
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				Multithreading.runAsync(new Runnable() {
					@Override
					public void run() {
						if (main.getMcLeaksAPI().checkAccount(player.getUniqueId()).isMCLeaks()) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (PlayerUtils.isEqualOrHigherThen(p, Rank.TRAINEE)) {
									p.sendMessage(CC.translate("&8[&4&lAnitCheat&8] &4[MCLeaks] &f" + player.getName() + " is a verified MCLeaks account!"));
								}
							}
						}
					}
				});
			}
		});
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
		if (StringUtils.lastMsg.containsKey(player.getUniqueId())) {
			StringUtils.lastMsg.remove(player.getUniqueId());
		}
		
		// Leave party if in one
		PartyManager partyManager = main.getPartyManager();
		Party party = partyManager.getParty(player);
		if (party != null && !party.isDestroyed()) {
			boolean disbanded = party.isOwner(player);
			if (disbanded) partyManager.removeParty(party);
			else party.removeMember(player);
			
			for (OfflinePlayer member : party.getMembers()) {
				if (!member.isOnline()) continue;
				member.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ " + (disbanded
						? "&7Your party has &edisbanded"
						: "&e" + player.getName() + " &7has left your &eparty")));
			}
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				Multithreading.runAsync(new Runnable() {
					@Override
					public void run() {
						try {
							main.getMySQL().saveFriendAccount(player.getUniqueId().toString());
							System.out.println("[NetworkTools] Saved account for " + player.getName());
						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println("[NetworkTools] Unable to save friends for " + player.getName());
						}
					}
				});
			}
		});
		TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (tempData.isYtVanishEnabled()) {
				p.showPlayer(player);
			}
		}
		main.getTempDataManager().unregisterNewData(player.getUniqueId());
	}

}
