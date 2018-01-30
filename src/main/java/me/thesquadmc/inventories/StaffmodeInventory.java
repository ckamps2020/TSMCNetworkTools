package me.thesquadmc.inventories;

import me.thesquadmc.Main;
import me.thesquadmc.utils.InventorySize;
import me.thesquadmc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class StaffmodeInventory {

	private final Main main;

	public StaffmodeInventory(Main main) {
		this.main = main;
	}

	public void buildStaffpanel(Player player) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "CONTROL PANEL");
		inventory.setItem(11, new ItemBuilder(Material.PAPER).name("&e&lREPORTS").build());
		inventory.setItem(13, new ItemBuilder(Material.ENDER_PEARL).name("&e&lRANDOM TELEPORT").build());
		inventory.setItem(15, new ItemBuilder(Material.DIAMOND_ORE).name("&e&lPLAYERS MINING").build());
		player.openInventory(inventory);
	}

}
