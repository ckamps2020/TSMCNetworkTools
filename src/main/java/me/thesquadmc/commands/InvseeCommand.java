package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.*;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				if (Bukkit.getServerName().toUpperCase().contains("HUB") || Bukkit.getServerName().toUpperCase().startsWith("BW")) {
					player.sendMessage(CC.translate("&e&lINVSEE &6■ &7You are not allowed to use this command here!"));
					return true;
				}
				if (args.length == 1) {
					Player t = Bukkit.getPlayer(args[0]);
					if (t != null) {
						player.openInventory(t.getInventory());
					} else {
						player.sendMessage(CC.translate("&e&lINVSEE &6■ &7That player is offline or does not exist!"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lINVSEE &6■ &7Usage: /invsee <player>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

	public static Map<UUID, UUID> getViewing() {
		return viewing;
	}

}
