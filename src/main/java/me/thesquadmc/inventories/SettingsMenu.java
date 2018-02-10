package me.thesquadmc.inventories;

import me.thesquadmc.Main;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.enums.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class SettingsMenu {

	public static void buildSettingsMenu(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 36, "Friend Settings");
		inventory.setItem(10, new ItemBuilder(Material.SIGN).name("&d&lFriend Notifications").lore(
				"&7",
				"&7Friend Notifications are the &djoin & leave",
				"&7messages given",
				"&7",
				"&5&o**Click to toggle**"
		).build());
		inventory.setItem(12, new ItemBuilder(Material.BOOK_AND_QUILL).name("&d&lPrivate Messages").lore(
				"&7",
				"&7Receive or don't receive &dprivate messages",
				"&7sent through &d/friend msg&7",
				"&7",
				"&5&o**Click to toggle**"
		).build());
		inventory.setItem(14, new ItemBuilder(Material.PAPER).name("&d&lFriend Chat").lore(
				"&7",
				"&7Receive or don’t receive &dfriend chat",
				"&7messages sent through &d/friend chat&7",
				"&7",
				"&5&o**Click to toggle**"
		).build());
		inventory.setItem(16, new ItemBuilder(Material.DIAMOND).name("&d&lFriend Requests").lore(
				"&7",
				"&7Stop or allow players attempting to be",
				"&7your &dfriend&7",
				"&7",
				"&5&o**Click to toggle**"
		).build());
		for (Settings settings : Settings.values()) {
			if (settings == Settings.NOTIFICATIONS) {
				if (Main.getMain().getSettings().get(player.getUniqueId()).get(Settings.NOTIFICATIONS)) {
					inventory.setItem(19, new ItemBuilder(Material.INK_SACK, 10).name("&a&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled on!", "&7Click to toggle off").build());
				} else {
					inventory.setItem(19, new ItemBuilder(Material.INK_SACK, 8).name("&c&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled off!", "&7Click to toggle on").build());
				}
			} else if (settings == Settings.PMS) {
				if (Main.getMain().getSettings().get(player.getUniqueId()).get(Settings.PMS)) {
					inventory.setItem(21, new ItemBuilder(Material.INK_SACK, 10).name("&a&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled on!", "&7Click to toggle off").build());
				} else {
					inventory.setItem(21, new ItemBuilder(Material.INK_SACK, 8).name("&c&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled off!", "&7Click to toggle on").build());
				}
			} else if (settings == Settings.FRIENDCHAT) {
				if (Main.getMain().getSettings().get(player.getUniqueId()).get(Settings.FRIENDCHAT)) {
					inventory.setItem(23, new ItemBuilder(Material.INK_SACK, 10).name("&a&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled on!", "&7Click to toggle off").build());
				} else {
					inventory.setItem(23, new ItemBuilder(Material.INK_SACK, 8).name("&c&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled off!", "&7Click to toggle on").build());
				}
			} else if (settings == Settings.REQUESTS) {
				if (Main.getMain().getSettings().get(player.getUniqueId()).get(Settings.REQUESTS)) {
					inventory.setItem(25, new ItemBuilder(Material.INK_SACK, 10).name("&a&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled on!", "&7Click to toggle off").build());
				} else {
					inventory.setItem(25, new ItemBuilder(Material.INK_SACK, 8).name("&c&l" + settings.name() + " On")
							.lore("&7" + settings.name() + " are toggled off!", "&7Click to toggle on").build());
				}
			}
		}
		player.openInventory(inventory);
	}

}
