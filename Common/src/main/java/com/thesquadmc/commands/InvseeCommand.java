package com.thesquadmc.commands;

import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InvseeCommand implements CommandExecutor {

    private static Map<UUID, UUID> viewing = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
                if (Bukkit.getServerName().toUpperCase().contains("HUB")) {
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
