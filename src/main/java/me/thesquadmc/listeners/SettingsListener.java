package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.StringUtils;
import me.thesquadmc.utils.enums.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SettingsListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getInventory() != null && e.getInventory().getName().equalsIgnoreCase("Friend Settings")) {
			Player player = (Player) e.getWhoClicked();
			ItemStack itemStack = e.getCurrentItem();
			e.setCancelled(true);
			if (itemStack != null) {
				if (e.getSlot() == 19) {
					if (itemStack.getData() != null && itemStack.getData().getData() == (short) 10) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.NOTIFICATIONS, false);
						e.getInventory().setItem(e.getSlot(), getOff("NOTIFICATIONS"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled notifications &eoff"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.NOTIFICATIONS, 0, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					} else if (itemStack.getData() != null && itemStack.getData().getData() == (short) 8) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.NOTIFICATIONS, true);
						e.getInventory().setItem(e.getSlot(), getOn("NOTIFICATIONS"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled notifications &eon"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.NOTIFICATIONS, 1, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					}
				} else if (e.getSlot() == 21) {
					if (itemStack.getData() != null && itemStack.getData().getData() == (short) 10) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.PMS, false);
						e.getInventory().setItem(e.getSlot(), getOff("PMS"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled pms &eoff"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.PMS, 0, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					} else if (itemStack.getData() != null && itemStack.getData().getData() == (short) 8) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.PMS, true);
						e.getInventory().setItem(e.getSlot(), getOn("PMS"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled pms &eon"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.PMS, 1, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					}
				} else if (e.getSlot() == 23) {
					if (itemStack.getData() != null && itemStack.getData().getData() == (short) 10) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.FRIENDCHAT, false);
						e.getInventory().setItem(e.getSlot(), getOff("FRIENDCHAT"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled friendchat &eoff"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.FRIENDCHAT, 0, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					} else if (itemStack.getData() != null && itemStack.getData().getData() == (short) 8) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.FRIENDCHAT, true);
						e.getInventory().setItem(e.getSlot(), getOn("FRIENDCHAT"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled friendchat &eon"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.FRIENDCHAT, 1, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					}
				} else if (e.getSlot() == 25) {
					if (itemStack.getData() != null && itemStack.getData().getData() == (short) 10) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.REQUESTS, false);
						e.getInventory().setItem(e.getSlot(), getOff("REQUESTS"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled requests &eoff"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.REQUESTS, 0, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					} else if (itemStack.getData() != null && itemStack.getData().getData() == (short) 8) {
						Main.getMain().getSettings().get(player.getUniqueId()).put(Settings.REQUESTS, true);
						e.getInventory().setItem(e.getSlot(), getOn("REQUESTS"));
						player.sendMessage(StringUtils.msg("&a&lFRIENDS &2■ &7You toggled requests &eon"));
						Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
							@Override
							public void run() {
								try {
									Main.getMain().getMySQL().updateSettings(Settings.REQUESTS, 1, player.getUniqueId().toString());
								} catch (Exception e) {
									System.out.println("[NetworkTools] Unable to execute mysql operation");
								}
							}
						});
					}
				}
			}
		}
	}

	private ItemStack getOff(String setting) {
		return new ItemBuilder(Material.INK_SACK, 8).name("&c&l" + setting + " On")
				.lore("&7" + setting + " are toggled off!", "&7Click to toggle on").build();
	}

	private ItemStack getOn(String setting) {
		return new ItemBuilder(Material.INK_SACK, 10).name("&a&l" + setting + " On")
				.lore("&7" + setting + " are toggled on!", "&7Click to toggle off").build();
	}

}
