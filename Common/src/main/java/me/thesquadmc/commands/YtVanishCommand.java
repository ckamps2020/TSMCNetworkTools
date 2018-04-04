package me.thesquadmc.commands;

import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class YtVanishCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				TSMCUser user = TSMCUser.fromPlayer(player);
				if (!user.isVanished()) {
					if (!user.isYtVanished()) {
						player.sendMessage(CC.translate("&e&lYT VANISH &6■ &7Vanish has been &eenabled"));
						user.setYtVanished(true);
						PlayerUtils.hidePlayerSpectatorYT(player);
					} else {
						player.sendMessage(CC.translate("&e&lYT VANISH &6■ &7Vanish has been &edisabled"));
						user.setYtVanished(false);
						PlayerUtils.showPlayerSpectator(player);
					}
				} else {
					player.sendMessage(CC.translate("&e&lYT VANISH &6■ &7Please disable normal vanish first!"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
