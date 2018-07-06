package me.thesquadmc.commands;

import me.thesquadmc.inventories.LogsInventory;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LogsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				if (args.length == 1) {
					String p = args[0];
					if (StringUtils.getLogs().containsKey(p)) {
						player.sendMessage(CC.translate("&e&lLOGS &6■ &7Showing you the most recent logging for &e" + p));
						LogsInventory.buildLogsInv(player, p);
					} else {
						player.sendMessage(CC.translate("&e&lLOGS &6■ &7That player doesn't not currently have any chat logging!"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lLOGS &6■ &7Usage: /logging <player>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
