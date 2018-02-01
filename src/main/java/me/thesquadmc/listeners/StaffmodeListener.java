package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.commands.StaffmodeCommand;
import me.thesquadmc.utils.InventorySize;
import me.thesquadmc.utils.StringUtils;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.handlers.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public final class StaffmodeListener implements Listener {

	private final Main main;
	private Map<UUID, Integer> timeRemaining = new HashMap<>();
	private Map<UUID, Map<UUID, Integer>> cps = new HashMap<>();

	public StaffmodeListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.SEC) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (timeRemaining.containsKey(player.getUniqueId())) {
					if (timeRemaining.get(player.getUniqueId()) - 1 == 0) {
						Map<UUID, Integer> m = cps.get(player.getUniqueId());
						for (Map.Entry<UUID, Integer> mm : m.entrySet()) {
							int averageCPS = mm.getValue() / 5;
							Player t = Bukkit.getPlayer(mm.getKey());
							player.sendMessage(StringUtils.msg("&e&lSTAFF &6■ &7CPS Calculated! Average CPS of &e" + t.getName() + " &7is: &e" + averageCPS));
						}
						cps.remove(player.getUniqueId());
						timeRemaining.remove(player.getUniqueId());
					} else {
						timeRemaining.put(player.getUniqueId(), timeRemaining.get(player.getUniqueId()) - 1);
						player.sendMessage(StringUtils.msg("&e&lSTAFF &6■ &7Calculating… printing CPS in &e" + timeRemaining.get(player.getUniqueId()) + "&7"));
					}
				}
			}
		}
	}

	@EventHandler
	public void onInter(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (e.getPlayer().getItemInHand() != null) {
			if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
				ItemStack stack = e.getPlayer().getItemInHand();
				if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("VANISH")) {
					player.performCommand("vanish");
				}
				if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("CONTROL PANEL")) {
					player.closeInventory();
					main.getStaffmodeInventory().buildStaffpanel(player);
				}
			}
		}
		for (Map.Entry<UUID, Map<UUID, Integer>> m : cps.entrySet()) {
			Map<UUID, Integer> mm = m.getValue();
			if (mm.get(player.getUniqueId()) != null) {
				mm.put(player.getUniqueId(), mm.get(player.getUniqueId()) + 1);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
			player.getInventory().clear();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				p.showPlayer(player);
			}
			for (ItemStack itemStack : StaffmodeCommand.getStaffmode().get(player.getUniqueId())) {
				if (itemStack != null) {
					player.getInventory().addItem(itemStack);
				}
			}
			StaffmodeCommand.getStaffmode().remove(player.getUniqueId());
			player.sendMessage(StringUtils.msg("&e&lSTAFF &6■  &7Staff mode has been &edisabled&7"));
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getEntity().getType() == EntityType.PLAYER && e.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getEntity();
			Player d = (Player) e.getDamager();
			if (StaffmodeCommand.getStaffmode().containsKey(p.getUniqueId()) || StaffmodeCommand.getStaffmode().containsKey(d.getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory() != null && e.getInventory().getName().equalsIgnoreCase("CONTROL PANEL")) {
			Player player = (Player) e.getWhoClicked();
			e.setCancelled(true);
			if (e.getSlot() == 11) {
				if (!main.getReportManager().getReports().isEmpty()) {
					main.getReportInventory().buildReportsMenu(player);
				} else {
					player.closeInventory();
					player.sendMessage(StringUtils.msg("&cThere are no active reports!"));
				}
			} else if (e.getSlot() == 13) {
				player.performCommand("randomtp");
			} else if (e.getSlot() == 15) {
				int i = 0;
				List<Player> below = new ArrayList<>();
				for (Player t : Bukkit.getOnlinePlayers()) {
					if (i < 54) {
						if (t.getLocation().getY() < 16.0) {
							below.add(t);
							i++;
						}
					} else {
						break;
					}
				}
				Inventory inv = Bukkit.createInventory(null, InventorySize.SIX_LINE.getSize(), "Miners Below Y 16");
				int ii = 0;
				for (Player p : below) {
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
					head.setDurability((short) 3);
					SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
					skullMeta.setOwner(p.getName());
					skullMeta.setDisplayName(p.getName());
					head.setItemMeta(skullMeta);
					inv.setItem(ii, head);
					ii++;
				}
				player.openInventory(inv);
			}
		} else if (e.getInventory() != null && e.getInventory().getName().equalsIgnoreCase("Miners Below Y 16")) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null) {
				ItemStack stack = e.getCurrentItem();
				if (stack.getItemMeta() != null) {
					Player player = (Player) e.getWhoClicked();
					String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
					Player t = Bukkit.getPlayer(name);
					if (t != null) {
						player.setGameMode(GameMode.SPECTATOR);
						player.teleport(t.getLocation());
					} else {
						player.sendMessage(StringUtils.msg("&cThat player is offline or does not exist!"));
					}
				}
			}
		} else if (e.getInventory() != null) {
			Player player = (Player) e.getWhoClicked();
			if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked().getType() == EntityType.PLAYER) {
			Player target = (Player) e.getRightClicked();
			Player player = e.getPlayer();
			if (player.getItemInHand() != null) {
				ItemStack stack = player.getItemInHand();
				if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
					if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("FREEZE TARGET")) {
						player.performCommand("freeze " + target.getName());
					} else if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("CPS CHECKER")) {
						player.sendMessage(StringUtils.msg("&e&lSTAFF &6■ &7Calculating… printing CPS in &e5&7"));
						timeRemaining.put(player.getUniqueId(), 5);
						Map<UUID, Integer> map = new HashMap<>();
						map.put(target.getUniqueId(), 0);
						cps.put(player.getUniqueId(), map);
					} else if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("OPEN INVENTORY")) {
						player.performCommand("invsee " + target.getName());
					}
				}
			}
		}
	}

}
