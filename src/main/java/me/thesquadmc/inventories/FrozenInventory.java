package me.thesquadmc.inventories;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.commands.FreezeCommand;
import me.thesquadmc.commands.UnFreezeCommand;
import me.thesquadmc.utils.InventorySize;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FrozenInventory implements Listener {

	//TODO clean up + listen for logout

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

	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (FreezeCommand.getFrozen().contains(player.getUniqueId())) {
			UnFreezeCommand.unfreezePlayer(player);
			screenshare.remove(player.getUniqueId());
			admitMenu.remove(player.getUniqueId());
			admitted.remove(player.getUniqueId());
			denying.remove(player.getUniqueId());
			for (Player t : Bukkit.getOnlinePlayers()) {
				User user = main.getLuckPermsApi().getUser(t.getUniqueId());
				if (main.hasPerm(user, "tools.staff.freeze")) {
					t.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &c" + player.getName() + " has logged out while frozen!"));
				}
			}
		}
	}

	public void buildFrozenInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FROZEN");
		for (int i = 0; i < InventorySize.THREE_LINE.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).data(8).name("&c&lYOU HAVE BEEN FROZEN BY STAFF!").build());
		}
		player.openInventory(inventory);
	}

	public void buildScreenshareInventory(Player player, String name) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FROZEN");
		for (int i = 0; i < InventorySize.THREE_LINE.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).data(3).name("&e&lCONTACT ON DISCORD: " + name).build());
		}
		player.openInventory(inventory);
	}

	public void buildAdmitInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FROZEN");
		for (int i = 0; i < InventorySize.THREE_LINE.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).data(7).name("&7&lDO YOU ADMIT TO BREAKING RULES?")
					.lore("&fLeft-Click to admit to break rules", "&fRight-Click to deny breaking the rules").build());
		}
		player.openInventory(inventory);
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getInventory().getName().equalsIgnoreCase("FROZEN")) {
			if (FreezeCommand.getFrozen().contains(e.getPlayer().getUniqueId())) {
				Player player = (Player) e.getPlayer();
				Bukkit.getScheduler().runTaskLater(main, () -> {
					if (screenshare.containsKey(e.getPlayer().getUniqueId())) {
						buildScreenshareInventory(player, screenshare.get(player.getUniqueId()));
					} else if (admitMenu.contains(e.getPlayer().getUniqueId())) {
						buildAdmitInventory(player);
					} else {
						buildFrozenInventory(player);
					}
				}, 2L);
			}
		} else if (e.getInventory().getName().toUpperCase().startsWith("FREEZE MENU FOR")) {
			viewing.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (typing.containsKey(e.getPlayer().getUniqueId())) {
			String discordName = e.getMessage();
			Player t = typing.get(e.getPlayer().getUniqueId());
			if (t != null) {
				typing.remove(e.getPlayer().getUniqueId());
				t.closeInventory();
				buildScreenshareInventory(t, discordName);
			} else {
				viewing.remove(e.getPlayer().getUniqueId());
				typing.remove(e.getPlayer().getUniqueId());
			}
		}
	}

	public void buildStaffGUI(Player player, Player target) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "FREEZE MENU FOR " + target.getName());
		inventory.setItem(12, new ItemBuilder(Material.STAINED_GLASS_PANE).data(4).name("&e&lScreenshare").lore("&7Will tell the user your discord name in their Freeze GUI").build());
		inventory.setItem(14, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5).name("&e&lUnfreeze " + target.getName()).lore("&7Will unfreeze the player upon clicking").build());
		if (admitMenu.contains(target.getUniqueId())) {
			inventory.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).data(6).name("&e&lAdmit Message").lore("&7You have asked the user if they want to admit").build());
		} else if (admitted.contains(target.getUniqueId())) {
			inventory.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).data(5).name("&e&lAdmit Message").lore("&7" + target.getName() + " has admitted to breaking the rules!").build());
		} else if (denying.contains(target.getUniqueId())) {
			inventory.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).data(14).name("&e&lAdmit Message").lore("&7" + target.getName() + " has denied to breaking the rules!").build());
		} else {
			inventory.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).data(7).name("&e&lAdmit Message").lore("&7Will ask the user if they admit to breaking the rules").build());
		}
		inventory.setItem(27, new ItemBuilder(Material.BARRIER).name("&e&lClose Freeze Panel").lore("&7Type /freezepanel " + target.getName() + " to open it again").build());
		player.openInventory(inventory);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (e.getInventory() != null && e.getInventory().getName().toUpperCase().startsWith("FREEZE MENU FOR ")) {
			e.setCancelled(true);
			Player t = Bukkit.getPlayer(viewing.get(player.getUniqueId()));
			if (e.getSlot() == 12) {
				player.closeInventory();
				player.sendMessage(StringUtils.msg("&ePlease enter your discord name in chat now:"));
				typing.put(player.getUniqueId(), t);
			} else if (e.getSlot() == 14) {
				//unfreeze
			} else if (e.getSlot() == 16) {
				ItemStack itemStack = e.getCurrentItem();
				if (itemStack != null) {
					if (itemStack.getData().getData() == (byte) 7) {
						buildAdmitInventory(t);
					}
				}
			} else if (e.getSlot() == 27) {
				player.closeInventory();
			}
		} else if (e.getInventory() != null && e.getInventory().getName().toUpperCase().startsWith("FREEZE MENU FOR ")) {
			e.setCancelled(true);
			ItemStack itemStack = e.getCurrentItem();
			if (itemStack != null) {
				if (itemStack.getData().getData() == (byte) 7) {
					if (e.getClick() == ClickType.RIGHT) {
						//deny
					} else if (e.getClick() == ClickType.LEFT) {
						//accept
					}
				}
			}
		}
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
