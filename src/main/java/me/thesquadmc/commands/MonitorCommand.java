package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonitorCommand implements CommandExecutor {

	private final Main main;

	public MonitorCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
				TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
				if (!tempData.isMonitor()) {
					tempData.setMonitor(true);
					player.sendMessage(StringUtils.msg("&e&lMONITOR &6■ &7You toggled Network Monitor &eon&7!"));
				} else {
					tempData.setMonitor(false);
					player.sendMessage(StringUtils.msg("&e&lMONITOR &6■ &7You toggled Network Monitor &eoff&7!"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
