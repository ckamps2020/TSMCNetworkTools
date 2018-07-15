package me.thesquadmc.commands;

import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ForceFieldCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				TSMCUser user = TSMCUser.fromPlayer(player);
				if (!user.hasForcefield()) {
					player.sendMessage(CC.translate("&e&lFORCEFIELD &6■ &7Forcefield has been &eenabled"));
					user.setForcefield(true);
				} else {
					player.sendMessage(CC.translate("&e&lFORCEFIELD &6■ &7Forcefield has been &edisabled"));
					user.setForcefield(false);
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
