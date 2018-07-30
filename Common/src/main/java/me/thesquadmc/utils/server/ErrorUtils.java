package me.thesquadmc.utils.server;

import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.Error;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ErrorUtils {

	public static void newPlayerError(Player errorPlayer, String error) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (PlayerUtils.doesRankMatch(player, Rank.DEVELOPER)) {
				player.sendMessage(CC.translate(Error.ERROR + error));
				player.sendMessage(CC.translate(Error.ERROR_2 + errorPlayer.getName()));
			}
		}
	}

	public static void newError(String error) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (PlayerUtils.doesRankMatch(player, Rank.DEVELOPER)) {
				player.sendMessage(CC.translate(Error.ERROR + error));
			}
		}
	}

}
