package me.thesquadmc.inventories;

import me.thesquadmc.utils.InventorySize;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InvseeInventory {

	public static void buildInvseeMenu(Player player, Player target) {
		player.closeInventory();
		Inventory inventory = Bukkit.createInventory(null, InventorySize.SIX_LINE.getSize(), "INVSEE");
		int i = 0;
		for (ItemStack s : target.getInventory().getContents()) {
			if (s != null) {
				inventory.setItem(i, s);
				i++;
			}
		}
		if (target.getInventory().getHelmet() != null) {
			inventory.setItem(45, target.getInventory().getHelmet());
		} else {
			inventory.setItem(45, new ItemBuilder(Material.BARRIER).name("&cNo Head").build());
		}
		if (target.getInventory().getChestplate() != null) {
			inventory.setItem(46, target.getInventory().getChestplate());
		} else {
			inventory.setItem(46, new ItemBuilder(Material.BARRIER).name("&cNo Chestplate").build());
		}
		if (target.getInventory().getLeggings() != null) {
			inventory.setItem(47, target.getInventory().getLeggings());
		} else {
			inventory.setItem(47, new ItemBuilder(Material.BARRIER).name("&cNo Leggings").build());
		}
		if (target.getInventory().getBoots() != null) {
			inventory.setItem(48, target.getInventory().getBoots());
		} else {
			inventory.setItem(48, new ItemBuilder(Material.BARRIER).name("&cNo Boots").build());

		}
		for (int ii = 36; ii < 51; ii++) {
			if (ii != 45 && ii != 46 && ii != 47 && ii != 48) {
				inventory.setItem(ii, new ItemBuilder(Material.STAINED_GLASS_PANE, 7).build());
			}
		}
		inventory.setItem(51, new ItemBuilder(Material.IRON_CHESTPLATE).name("&e&lArmor Bar").lore("&7" + PlayerUtils.getArmorLevel(target) + "&8/&71").build());
		inventory.setItem(52, new ItemBuilder(Material.INK_SACK).data(1).name("&e&lHealth Bar").lore("&7" + target.getHealth() + "&8/&720.0").build());
		inventory.setItem(53, new ItemBuilder(Material.GRILLED_PORK).name("&e&lHunger Bar").lore("&7" + target.getFoodLevel() + "&8/&720").build());
		player.openInventory(inventory);
	}

}
