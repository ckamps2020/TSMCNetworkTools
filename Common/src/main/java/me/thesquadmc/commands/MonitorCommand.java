package me.thesquadmc.commands;

import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MonitorCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
				TSMCUser user = TSMCUser.fromPlayer(player);
				if (!user.hasMonitor()) {
					user.setMonitor(true);
					player.sendMessage(CC.translate("&e&lMONITOR &6■ &7You toggled Network Monitor &eon&7!"));
				} else {
					user.setMonitor(false);
					player.sendMessage(CC.translate("&e&lMONITOR &6■ &7You toggled Network Monitor &eoff&7!"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
