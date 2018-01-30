package me.thesquadmc.commands;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class StaffmodeCommand implements CommandExecutor {

	private final Main main;
	private static Map<UUID, ItemStack[]> staffmode = new HashMap<>();

	public StaffmodeCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.staffmode")) {
				if (!staffmode.containsKey(player.getUniqueId())) {
					player.performCommand("vanish");
					staffmode.put(player.getUniqueId(), player.getInventory().getContents());
					player.getInventory().clear();
					player.getInventory().setItem(0, new ItemBuilder(Material.REDSTONE_COMPARATOR).name("&e&lControl Panel").lore("&7View more staff options to perform").build());
					player.getInventory().setItem(2, new ItemBuilder(Material.ICE).name("&e&lFreeze Target").lore("&7Freeze the player you are looking at").build());
					player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK, 10).name("&e&lToggle Vanish &7off").lore("&7Toggle vanish on or off").build());
					player.getInventory().setItem(6, new ItemBuilder(Material.DIAMOND_SWORD).name("&e&lCPS Checker").lore("&7Right-Click the target to check their CPS").build());
					player.getInventory().setItem(8, new ItemBuilder(Material.CHEST).name("&e&lOpen Inventory").lore("&7Open the player you're look at's inventory").build());
					player.sendMessage(StringUtils.msg("&e&lSTAFF &6■  &7Staff mode has been &eenabled&7"));
				} else {
					player.getInventory().clear();
					for (ItemStack itemStack : staffmode.get(player.getUniqueId())) {
						if (itemStack != null) {
							player.getInventory().addItem(itemStack);
						}
					}
					staffmode.remove(player.getUniqueId());
					player.performCommand("vanish");
					player.sendMessage(StringUtils.msg("&e&lSTAFF &6■  &7Staff mode has been &edisabled&7"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

	public static Map<UUID, ItemStack[]> getStaffmode() {
		return staffmode;
	}
}
