package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.inventories.InvseeInventory;
import me.thesquadmc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InvseeCommand implements CommandExecutor {

	private final Main main;
	private static Map<UUID, UUID> viewing = new HashMap<>();

	public InvseeCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (Bukkit.getServerName().toUpperCase().contains("HUB")) {
					player.sendMessage(StringUtils.msg("&cYou are not allowed to use this command here!"));
					return true;
				}
				if (args.length == 1) {
					Player t = Bukkit.getPlayer(args[0]);
					if (t != null) {
						player.openInventory(t.getInventory());
					} else {
						player.sendMessage(StringUtils.msg("&cThat player is offline or does not exist!"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /invsee <player>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

	public static Map<UUID, UUID> getViewing() {
		return viewing;
	}

}
