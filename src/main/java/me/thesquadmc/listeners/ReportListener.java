package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.objects.Report;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.enums.UpdateType;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

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
		} else if (e.getUpdateType() == UpdateType.MIN) {
			if (Bukkit.getServerName().toUpperCase().contains("HUB")
					|| Bukkit.getServerName().toUpperCase().contains("SKYBLOCK")
					|| Bukkit.getServerName().toUpperCase().contains("FACTIONS")
					|| Bukkit.getServerName().toUpperCase().contains("PRISON")
					|| Bukkit.getServerName().toUpperCase().contains("TROLLWARS")) {
				main.setRestartTime(main.getRestartTime() + 1);
				if (main.getRestartTime() == 720) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.kickPlayer(CC.translate("&e&lRESTART &6■ &7Daily server restart"));
					}
					Bukkit.shutdown();
				} else if (main.getRestartTime() == 715) {
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(CC.translate("&e&lRESTART &6■ &7Daily server restart in &e5&7min"));
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
					}
				} else if (main.getRestartTime() == 719) {
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(CC.translate("&e&lRESTART &6■ &7Daily server restart in &e1&7min"));
					Bukkit.broadcastMessage(" ");
					Bukkit.broadcastMessage(" ");
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
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
						player.sendMessage(CC.translate("&e&lREPORT &6■ &7There are no active reports!"));
					}
				} else if (e.getSlot() == 15) {
					if (!main.getReportManager().getClosedReports().isEmpty()) {
						main.getReportInventory().buildClosedReportsMenu(player);
					} else {
						player.closeInventory();
						player.sendMessage(CC.translate("&e&lREPORT &6■ &7There are no active closed reports!"));
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
								player.sendMessage(CC.translate("&e&lREPORT &6■ &7There are no more pages of reports!"));
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
								player.sendMessage(CC.translate("&e&lREPORT &6■ &7Report " + report.getReportID() + " has been closed!"));
								main.getReportManager().newClosedReport(report.getReportID().toString(), player.getName());
							} else {
								player.sendMessage(CC.translate("&e&lREPORT &6■ &7This report already seems to be closed!"));
							}
						} else if (e.getClick() == ClickType.LEFT) {
							Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
								@Override
								public void run() {
									Multithreading.runAsync(new Runnable() {
										@Override
										public void run() {
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
												player.sendMessage(CC.translate("&e&lREPORT &6■ &7Sending you to &e" + report.getServer() + "..."));
												try (Jedis jedis = main.getPool().getResource()) {
													JedisTask.withName(UUID.randomUUID().toString())
															.withArg(RedisArg.PLAYER.getArg(), player.getName())
															.withArg(RedisArg.SERVER.getArg(), report.getServer())
															.send(RedisChannels.TRANSPORT.getChannelName(), jedis);
												}
											}
										}
									});
								}
							});
						}
					}
				}
			}
		}
	}

}
