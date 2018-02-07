package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.commands.FreezeCommand;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public final class FreezeListener implements Listener {

	private final Main main;

	public FreezeListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (FreezeCommand.getFrozen().contains(player.getUniqueId())) {
			PlayerUtils.unfreezePlayer(player);
			main.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
			main.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
			main.getFrozenInventory().getAdmitted().remove(player.getUniqueId());
			main.getFrozenInventory().getDenying().remove(player.getUniqueId());
			FreezeCommand.getFrozen().remove(player.getUniqueId());
			for (Player t : Bukkit.getOnlinePlayers()) {
				if (PlayerUtils.isEqualOrHigherThen(t, Rank.MOD)) {
					t.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &c" + player.getName() + " has logged out while frozen!"));
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() != null && e.getEntity().getType() == EntityType.PLAYER) {
			Player player = (Player) e.getEntity();
			if (FreezeCommand.getFrozen().contains(player.getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getInventory().getName().equalsIgnoreCase("FROZEN")) {
			if (FreezeCommand.getFrozen().contains(e.getPlayer().getUniqueId())) {
				Player player = (Player) e.getPlayer();
				Bukkit.getScheduler().runTaskLater(main, () -> {
					if (main.getFrozenInventory().getScreenshare().containsKey(player.getUniqueId())) {
						main.getFrozenInventory().buildScreenshareInventory(player, main.getFrozenInventory().getScreenshare().get(player.getUniqueId()));
					} else if (main.getFrozenInventory().getAdmitMenu().contains(player.getUniqueId())) {
						main.getFrozenInventory().buildAdmitInventory(player);
					} else if (main.getFrozenInventory().getScreenshare().containsKey(player.getUniqueId())) {
						main.getFrozenInventory().buildScreenshareInventory(player, main.getFrozenInventory().getScreenshare().get(player.getUniqueId()));
					} else {
						main.getFrozenInventory().buildFrozenInventory(player);
					}
				}, 2L);
			}
		} else if (e.getInventory().getName().toUpperCase().startsWith("FREEZE MENU FOR")) {
			main.getFrozenInventory().getViewing().remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (main.getFrozenInventory().getTyping().containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			String discordName = e.getMessage();
			Player t = main.getFrozenInventory().getTyping().get(e.getPlayer().getUniqueId());
			if (t != null) {
				main.getFrozenInventory().getTyping().remove(e.getPlayer().getUniqueId());
				t.closeInventory();
				main.getFrozenInventory().getScreenshare().put(t.getUniqueId(), discordName);
				main.getFrozenInventory().buildScreenshareInventory(t, discordName);
				e.getPlayer().sendMessage(StringUtils.msg("&9Thanks! Informing the frozen player now"));
			} else {
				main.getFrozenInventory().getTyping().remove(e.getPlayer().getUniqueId());
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (e.getInventory() != null && e.getInventory().getName().toUpperCase().startsWith("FREEZE MENU FOR ")) {
			e.setCancelled(true);
			Player t = Bukkit.getPlayer(main.getFrozenInventory().getViewing().get(player.getUniqueId()));
			if (t == null) {
				player.closeInventory();
				main.getFrozenInventory().getViewing().remove(player.getUniqueId());
				return;
			}
			if (e.getSlot() == 11) {
				player.closeInventory();
				player.sendMessage(StringUtils.msg("&ePlease enter your discord name in chat now:"));
				main.getFrozenInventory().getTyping().put(player.getUniqueId(), t);
			} else if (e.getSlot() == 13) {
				PlayerUtils.unfreezePlayer(t);
				FreezeCommand.getFrozen().remove(t.getUniqueId());
				main.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
				main.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
				main.getFrozenInventory().getAdmitted().remove(player.getUniqueId());
				main.getFrozenInventory().getDenying().remove(player.getUniqueId());
				main.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
				player.closeInventory();
				player.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &7You have unfrozen &e" + t.getName()));
				t.closeInventory();
				t.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &7You have been &eunfrozen&7. Thank you for your &epatience&7"));
			} else if (e.getSlot() == 15) {
				ItemStack itemStack = e.getCurrentItem();
				if (itemStack != null) {
					if (itemStack.getData().getData() == (byte) 7) {
						main.getFrozenInventory().getAdmitMenu().add(t.getUniqueId());
						main.getFrozenInventory().buildAdmitInventory(t);
						player.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &7You have asked the player to admit to breaking the rules!"));
						player.closeInventory();
					}
				}
			} else if (e.getSlot() == 26) {
				player.closeInventory();
			}
		} else if (e.getInventory() != null && e.getInventory().getName().toUpperCase().startsWith("FROZEN")) {
			e.setCancelled(true);
			ItemStack itemStack = e.getCurrentItem();
			if (itemStack != null) {
				if (itemStack.getData().getData() == (byte) 7) {
					if (e.getClick() == ClickType.RIGHT) {
						main.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
						main.getFrozenInventory().getDenying().add(player.getUniqueId());
						main.getFrozenInventory().buildAdmitInventory(player);
					} else if (e.getClick() == ClickType.LEFT) {
						main.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
						main.getFrozenInventory().getAdmitted().add(player.getUniqueId());
						main.getFrozenInventory().buildAdmitInventory(player);
						for (Map.Entry<UUID, UUID> map : main.getFrozenInventory().getViewing().entrySet()) {
							Player t = Bukkit.getPlayer(map.getValue());
							if (t != null && t.getUniqueId() == player.getUniqueId()) {
								Player p = Bukkit.getPlayer(map.getKey());
								main.getFrozenInventory().buildStaffGUI(p, player);
							}
						}
					}
				}
			}
		}
	}

}
