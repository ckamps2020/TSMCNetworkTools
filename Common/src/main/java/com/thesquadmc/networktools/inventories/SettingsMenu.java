package com.thesquadmc.networktools.inventories;

import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.inventory.InventorySize;
import com.thesquadmc.networktools.utils.inventory.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class SettingsMenu {

    public static void buildSettingsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, InventorySize.FOUR_LINE, "Friend Settings");
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

        TSMCUser user = TSMCUser.fromPlayer(player);
        for (PlayerSetting<?> setting : PlayerSetting.values()) {
            if (setting == PlayerSetting.FRIEND_NOTIFICATIONS) {
                if (user.getSetting(PlayerSetting.FRIEND_NOTIFICATIONS)) {
                    inventory.setItem(19, new ItemBuilder(Material.INK_SACK, 10).name("&a&lNOTIFICATIONS On")
                            .lore("&7NOTIFICATIONS are toggled on!", "&7Click to toggle off").build());
                } else {
                    inventory.setItem(19, new ItemBuilder(Material.INK_SACK, 8).name("&c&lNOTIFICATIONS On")
                            .lore("&7NOTIFICATIONS are toggled off!", "&7Click to toggle on").build());
                }
            } else if (setting == PlayerSetting.PRIVATE_MESSAGES) {
                if (user.getSetting(PlayerSetting.PRIVATE_MESSAGES)) {
                    inventory.setItem(21, new ItemBuilder(Material.INK_SACK, 10).name("&a&lPRIVATE MESSAGES On")
                            .lore("&7PRIVATE MESSAGES are toggled on!", "&7Click to toggle off").build());
                } else {
                    inventory.setItem(21, new ItemBuilder(Material.INK_SACK, 8).name("&c&lPRIVATE MESSAGES On")
                            .lore("&7PRIVATE MESSAGES are toggled off!", "&7Click to toggle on").build());
                }
            } else if (setting == PlayerSetting.FRIEND_CHAT) {
                if (user.getSetting(PlayerSetting.FRIEND_CHAT)) {
                    inventory.setItem(23, new ItemBuilder(Material.INK_SACK, 10).name("&a&lFRIEND CHAT On")
                            .lore("&7FRIEND CHAT are toggled on!", "&7Click to toggle off").build());
                } else {
                    inventory.setItem(23, new ItemBuilder(Material.INK_SACK, 8).name("&c&lFRIEND CHAT On")
                            .lore("&7FRIEND CHAT are toggled off!", "&7Click to toggle on").build());
                }
            } else if (setting == PlayerSetting.FRIEND_REQUESTS) {
                if (user.getSetting(PlayerSetting.FRIEND_REQUESTS)) {
                    inventory.setItem(25, new ItemBuilder(Material.INK_SACK, 10).name("&a&lFRIEND REQUESTS On")
                            .lore("&7FRIEND REQUESTS are toggled on!", "&7Click to toggle off").build());
                } else {
                    inventory.setItem(25, new ItemBuilder(Material.INK_SACK, 8).name("&c&lFRIEND REQUESTS On")
                            .lore("&7FRIEND REQUESTS are toggled off!", "&7Click to toggle on").build());
                }
            }
        }
        player.openInventory(inventory);
    }

}
