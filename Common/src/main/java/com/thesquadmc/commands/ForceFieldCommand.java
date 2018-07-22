package com.thesquadmc.commands;

import com.thesquadmc.player.PlayerSetting;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
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
				if (!user.getSetting(PlayerSetting.FORCEFIELD)) {
					player.sendMessage(CC.translate("&e&lFORCEFIELD &6■ &7Forcefield has been &eenabled"));
					user.updateSetting(PlayerSetting.FORCEFIELD, true);
				} else {
					player.sendMessage(CC.translate("&e&lFORCEFIELD &6■ &7Forcefield has been &edisabled"));
					user.updateSetting(PlayerSetting.FORCEFIELD, false);
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
