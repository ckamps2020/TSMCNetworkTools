package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StaffMenuCommand implements CommandExecutor {

	private final NetworkTools networkTools;

	public StaffMenuCommand(NetworkTools networkTools) {
		this.networkTools = networkTools;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
					networkTools.getStaffmodeInventory().buildStaffpanel(player);
				} else {
					player.sendMessage(CC.translate("&e&lSTAFFMODE &6■ &7You are not in staffmode!"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
