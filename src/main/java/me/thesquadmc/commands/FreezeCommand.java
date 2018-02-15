package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public final class FreezeCommand implements CommandExecutor {

	private final Main main;
	private static List<UUID> frozen = new ArrayList<>();

	public FreezeCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (Bukkit.getServerName().toUpperCase().contains("HUB")) {
					player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You are not allowed to use this command here!"));
					return true;
				}
				if (args.length == 1) {
					String name = args[0];
					Player t = Bukkit.getPlayer(name);
					if (t != null) {
						if (!PlayerUtils.isEqualOrHigherThen(t, Rank.MOD)) {
							if (!frozen.contains(t.getUniqueId())) {
								PlayerUtils.freezePlayer(t);
								t.sendMessage(CC.translate("&c&lYou have been frozen by staff, do not log out at all. Please follow staffs instructions at all time"));
								frozen.add(t.getUniqueId());
								main.getFrozenInventory().buildFrozenInventory(t);
								main.getFrozenInventory().buildStaffGUI(player, t);
								main.getFrozenInventory().getViewing().put(player.getUniqueId(), t.getUniqueId());
								player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You have frozen &e" + t.getName() + "&7!"));
							} else {
								player.sendMessage(CC.translate("&e&lFREEZE &6■ &7That player is already frozen!"));
							}
						} else {
							player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You are not allowed to freeze another staff member!"));
						}
					} else {
						player.sendMessage(CC.translate("&e&lFREEZE &6■ &7That player does not exist or is offline!"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lFREEZE &6■ &7Usage: /freeze <player>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

	public static List<UUID> getFrozen() {
		return frozen;
	}

}
