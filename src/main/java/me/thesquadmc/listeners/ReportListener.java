package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.objects.Report;
import me.thesquadmc.utils.StringUtils;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.handlers.UpdateType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public final class ReportListener implements Listener {

	private final Main main;

	public ReportListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.FIVE_MIN) {
			for (Report report : main.getReportManager().getClosedReports()) {
				if (report.getTimeAlive() != -1) {
					if (report.getTimeAlive() + 5 >= 300) {
						main.getReportManager().removeClosedReport(report);
					} else {
						report.setTimeAlive(report.getTimeAlive() + 5);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getInventory() != null) {
			if (e.getInventory().getName().toUpperCase().startsWith("CLOSED REPORTS") || e.getInventory().getName().toUpperCase().startsWith("REPORTS")) {
				main.getReportInventory().getMap().remove(e.getPlayer().getUniqueId());
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory() != null && e.getCurrentItem() != null) {
			if (e.getInventory().getName().equalsIgnoreCase("Report Manager")) {
				e.setCancelled(true);
				Player player = (Player) e.getWhoClicked();
				if (e.getSlot() == 11) {
					if (!main.getReportManager().getReports().isEmpty()) {
						main.getReportInventory().buildReportsMenu(player);
					} else {
						player.closeInventory();
						player.sendMessage(StringUtils.msg("&cThere are no active reports!"));
					}
				} else if (e.getSlot() == 15) {
					if (!main.getReportManager().getClosedReports().isEmpty()) {
						main.getReportInventory().buildClosedReportsMenu(player);
					} else {
						player.closeInventory();
						player.sendMessage(StringUtils.msg("&cThere are no active closed reports!"));
					}
				}
			} else if (e.getInventory().getName().toUpperCase().startsWith("CLOSED REPORTS") || e.getInventory().getName().toUpperCase().startsWith("REPORTS")) {
				e.setCancelled(true);
				Player player = (Player) e.getWhoClicked();
				ItemStack stack = e.getCurrentItem();
				if (stack.getType() == Material.ARROW) {
					if (stack.getItemMeta() != null) {
						try {
							int i = Integer.valueOf(stack.getItemMeta().getDisplayName()) - 1;
							if (main.getReportInventory().getMap().get(player.getUniqueId()).get(i) != null) {
								List<Inventory> list = main.getReportInventory().getMap().get(player.getUniqueId());
								Inventory inv = main.getReportInventory().getMap().get(player.getUniqueId()).get(i);
								player.openInventory(inv);
								main.getReportInventory().getMap().put(player.getUniqueId(), list);
							} else {
								player.sendMessage(StringUtils.msg("&cThere are no more pages of reports!"));
							}
						} catch (Exception ex) {
							player.closeInventory();
						}
					}
				} else if (stack.getType() == Material.SKULL_ITEM) {
					if (stack.getItemMeta() != null) {
						if (e.getClick() == ClickType.RIGHT) {
							List<String> lore = stack.getItemMeta().getLore();
							Report report = null;
							for (String s : lore) {
								String ss = ChatColor.stripColor(s);
								if (ss.toUpperCase().startsWith("ID")) {
									String regex = "[ ]+";
									String[] tokens = s.split(regex);
									if (tokens.length == 2) {
										report = main.getReportManager().getReportFromUUID(tokens[1]);
										break;
									}
								}
							}
							if (report != null) {
								player.closeInventory();
								player.sendMessage(StringUtils.msg("&aReport " + report.getReportID() + " has been closed!"));
								main.getReportManager().newClosedReport(report.getReportID().toString(), player.getName());
							} else {
								player.sendMessage(StringUtils.msg("&7This report already seems to be closed!"));
							}
						}
					}
				}
			}
		}
	}

}
