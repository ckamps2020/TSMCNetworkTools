package me.thesquadmc.inventories;

import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.InventorySize;
import me.thesquadmc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public final class FrozenInventory {

	private final Main main;
	private Map<UUID, UUID> viewing = new HashMap<>();
	private Map<UUID, Player> typing = new HashMap<>();
	private Map<UUID, String> screenshare = new HashMap<>();
	private List<UUID> admitMenu = new ArrayList<>();
	private List<UUID> admitted = new ArrayList<>();
	private List<UUID> denying = new ArrayList<>();

	public FrozenInventory(Main main) {
		this.main = main;
	}

	public void buildFrozenInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FROZEN");
		for (int i = 0; i < InventorySize.THREE_LINE.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 8).name("&c&lYOU HAVE BEEN FROZEN BY STAFF!").build());
		}
		player.openInventory(inventory);
	}

	public void buildScreenshareInventory(Player player, String name) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FROZEN");
		for (int i = 0; i < InventorySize.THREE_LINE.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 3).name("&e&lCONTACT ON DISCORD: " + name).build());
		}
		player.openInventory(inventory);
	}

	public void buildAdmitInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FROZEN");
		for (int i = 0; i < InventorySize.THREE_LINE.getSize(); i++) {
			if (admitMenu.contains(player.getUniqueId())) {
				inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 7).name("&7&lDO YOU ADMIT TO BREAKING RULES?")
						.lore("&fLeft-Click to admit to break rules", "&fRight-Click to deny breaking the rules").build());
			} else if (admitted.contains(player.getUniqueId())) {
				inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 5).name("&a&lYOU ADMITTED TO BREAKING THE RULES").build());
			} else if (denying.contains(player.getUniqueId())) {
				inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 14).name("&c&lYOU DENIED BREAKING THE RULES").build());
			} else {
				inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, 7).name("&7&lDO YOU ADMIT TO BREAKING RULES?")
						.lore("&fLeft-Click to admit to break rules", "&fRight-Click to deny breaking the rules").build());
			}
		}
		player.openInventory(inventory);
	}

	public void buildStaffGUI(Player player, Player target) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FREEZE MENU FOR " + target.getName());
		inventory.setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE, 4).name("&e&lScreenshare").lore("&7Will tell the user your discord name in their Freeze GUI").build());
		inventory.setItem(13, new ItemBuilder(Material.STAINED_GLASS_PANE, 5).name("&e&lUnfreeze " + target.getName()).lore("&7Will unfreeze the player upon clicking").build());
		if (admitMenu.contains(target.getUniqueId())) {
			inventory.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE, 6).name("&e&lAdmit Message").lore("&7You have asked the user if they want to admit").build());
		} else if (admitted.contains(target.getUniqueId())) {
			inventory.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE, 5).name("&e&lAdmit Message").lore("&7" + target.getName() + " has admitted to breaking the rules!").build());
		} else if (denying.contains(target.getUniqueId())) {
			inventory.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE, 14).name("&e&lAdmit Message").lore("&7" + target.getName() + " has denied to breaking the rules!").build());
		} else {
			inventory.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE, 7).name("&e&lAdmit Message").lore("&7Will ask the user if they admit to breaking the rules").build());
		}
		inventory.setItem(26, new ItemBuilder(Material.BARRIER).name("&e&lClose Freeze Panel").lore("&7Type /freezepanel " + target.getName() + " to open it again").build());
		player.openInventory(inventory);
	}

	public Map<UUID, UUID> getViewing() {
		return viewing;
	}

	public Map<UUID, Player> getTyping() {
		return typing;
	}

	public Map<UUID, String> getScreenshare() {
		return screenshare;
	}

	public List<UUID> getAdmitMenu() {
		return admitMenu;
	}

	public List<UUID> getAdmitted() {
		return admitted;
	}

	public List<UUID> getDenying() {
		return denying;
	}

}
