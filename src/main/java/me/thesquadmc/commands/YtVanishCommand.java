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

public final class YtVanishCommand implements CommandExecutor {

	private final Main main;

	public YtVanishCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
				if (!tempData.isYtVanishEnabled()) {
					player.sendMessage(StringUtils.msg("&e&lYT VANISH &6■ &7Vanish has been &eenabled"));
					tempData.setYtVanishEnabled(true);
					PlayerUtils.hidePlayerSpectatorYT(player);
				} else {
					player.sendMessage(StringUtils.msg("&e&lYT VANISH &6■ &7Vanish has been &edisabled"));
					tempData.setYtVanishEnabled(false);
					PlayerUtils.showPlayerSpectator(player);
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
