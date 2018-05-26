package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.inventory.ItemBuilder;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class StaffmodeCommand implements CommandExecutor {

	private static final Map<UUID, ItemStack[]> staffmode = new HashMap<>();
	private static final Map<UUID, Location> locations = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				TSMCUser user = TSMCUser.fromPlayer(player);
				if (Bukkit.getServerName().toUpperCase().contains("HUB") || Bukkit.getServerName().toUpperCase().startsWith("BW") || Bukkit.getServerName().toUpperCase().startsWith("CREATIVE")) {
					player.sendMessage(CC.translate("&e&lSTAFFMODE &6■ &7You are not allowed to use this command here!"));
					return true;
				}
				if (!staffmode.containsKey(player.getUniqueId())) {
					PlayerUtils.hidePlayerSpectatorStaff(player);
					user.setVanished(true);
					player.sendMessage(CC.translate("&e&lVANISH &6■ &7You toggled vanish &eon&7! No one will be able to see you"));
					staffmode.put(player.getUniqueId(), player.getInventory().getContents());
					locations.put(player.getUniqueId(), player.getLocation());
					player.getInventory().clear();
					player.getInventory().setArmorContents(null);
					player.getInventory().setItem(0, new ItemBuilder(Material.REDSTONE_COMPARATOR).name("&e&lControl Panel").lore("&7Right Click to open the Control Panel", "&7Left-Click to random teleport").build());

					if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
						player.getInventory().setItem(2, new ItemBuilder(Material.ICE).name("&e&lFreeze Target").lore("&7Freeze the player you are looking at").build());
					}
					player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK, 10).name("&e&lToggle Vanish &7off").lore("&7Toggle vanish on or off").build());
					player.getInventory().setItem(6, new ItemBuilder(Material.DIAMOND_SWORD).name("&e&lCPS Checker").lore("&7Right-Click the target to check their CPS").build());
					player.getInventory().setItem(8, new ItemBuilder(Material.CHEST).name("&e&lPlace Chest").lore("&7Place a chest down at location").build());
					player.sendMessage(CC.translate("&e&lSTAFFMODE &6■  &7Staff mode has been &eenabled&7"));
				} else {
					player.getInventory().clear();
					player.getInventory().setContents(staffmode.get(player.getUniqueId()));

					player.teleport(locations.remove(player.getUniqueId()));
					staffmode.remove(player.getUniqueId());
					PlayerUtils.showPlayerSpectator(player);
					user.setVanished(false);
					player.sendMessage(CC.translate("&e&lVANISH &6■ &7You toggled vanish &eoff&7! Everyone will be able to see you"));
					player.setGameMode(GameMode.SURVIVAL);
					player.sendMessage(CC.translate("&e&lSTAFFMODE &6■  &7Staff mode has been &edisabled&7"));

				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

	public static Map<UUID, ItemStack[]> getStaffmode() {
		return staffmode;
	}

	public static Map<UUID, Location> getLocations() {
		return locations;
	}
}
