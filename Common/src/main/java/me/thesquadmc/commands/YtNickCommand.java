package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class YtNickCommand implements CommandExecutor {

	private final Main main;

	public YtNickCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				boolean t = true;
				if (t) {
					player.sendMessage(CC.translate("&e&lYT NICK &6■ &7Command disabled for the moment!"));
					return true;
				}
				if (!tempData.isNicknamed()) {
					if (args.length == 1) {
						String name = args[0];
						player.sendMessage(CC.translate("&e&lYT NICK &6■ &7You are now nicked as &e" + name));
						tempData.setNickname(true);
						PlayerUtils.setName(player, name);
					} else {
						player.sendMessage(CC.translate("&e&lYT NICK &6■ &7Usage: /ytnick (name)"));
					}
				} else {
					player.sendMessage(CC.translate("&e&lYT NICK &6■ &7You are &eno longer &7nicked"));
					tempData.setNickname(false);
					PlayerUtils.setName(player, tempData.getRealname());
				}
			} else {
				player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
			}
		}
		return true;
	}

}
