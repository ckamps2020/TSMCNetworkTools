package me.thesquadmc.commands;

import me.thesquadmc.player.PlayerSetting;
import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
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
                if (!user.getSetting(PlayerSetting.VANISHED)) {
                    if (!user.getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
						player.sendMessage(CC.translate("&e&lYT VANISH &6■ &7Vanish has been &eenabled"));
                        user.updateSetting(PlayerSetting.YOUTUBE_VANISHED, true);
						PlayerUtils.hidePlayerSpectatorYT(player);
					} else {
						player.sendMessage(CC.translate("&e&lYT VANISH &6■ &7Vanish has been &edisabled"));
                        user.updateSetting(PlayerSetting.YOUTUBE_VANISHED, false);
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
