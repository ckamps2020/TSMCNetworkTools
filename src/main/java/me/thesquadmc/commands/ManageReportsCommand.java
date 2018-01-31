package me.thesquadmc.commands;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManageReportsCommand implements CommandExecutor {

	private final Main main;

	public ManageReportsCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.manager.management")) {
				main.getReportInventory().buildManagerReportsMenu(player);
				player.sendMessage(StringUtils.msg("&e&lREPORT &6■ &7Opening up the &eReport Management &7menu"));
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
