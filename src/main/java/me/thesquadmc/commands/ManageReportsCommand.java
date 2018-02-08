package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ManageReportsCommand implements CommandExecutor {

	private final Main main;

	public ManageReportsCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
				if (args.length == 0) {
					main.getReportInventory().buildManagerReportsMenu(player);
					player.sendMessage(StringUtils.msg("&e&lREPORT &6■ &7Opening up the &eReport Management &7menu"));
				} else if (args.length == 1) {
					String option = args[0];
					TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
					if (option.equalsIgnoreCase("on")) {
						tempData.setReportsEnabled(true);
						player.sendMessage(StringUtils.msg("&e&lREPORT &6■ &7You have toggle reports &eon"));
					} else if (option.equalsIgnoreCase("off")) {
						tempData.setReportsEnabled(false);
						player.sendMessage(StringUtils.msg("&e&lREPORT &6■ &7You have toggle reports &eoff"));
					} else {
						player.sendMessage(StringUtils.msg("&cUsage: /reports (on/off)"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /reports (on/off)"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
