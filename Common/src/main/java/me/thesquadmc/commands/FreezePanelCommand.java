package me.thesquadmc.commands;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class FreezePanelCommand implements CommandExecutor {

	private final NetworkTools networkTools;

	public FreezePanelCommand(NetworkTools networkTools) {
		this.networkTools = networkTools;
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
					Player t = Bukkit.getPlayer(args[0]);
					if (t != null) {
						if (FreezeCommand.getFrozen().contains(t.getUniqueId())) {
							networkTools.getFrozenInventory().buildStaffGUI(player, t);
							networkTools.getFrozenInventory().getViewing().put(player.getUniqueId(), t.getUniqueId());
						} else {
							player.sendMessage(CC.translate("&e&lFREEZE &6■ &7That player is not frozen!"));
						}
					} else {
						player.sendMessage(CC.translate("&e&lFREEZE &6■ &7That player does not exist or is offline!"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lFREEZE &6■ &7Usage: /freezepanel <player>"));
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
