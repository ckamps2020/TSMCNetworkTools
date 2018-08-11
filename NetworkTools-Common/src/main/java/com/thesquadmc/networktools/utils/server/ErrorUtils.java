package com.thesquadmc.networktools.utils.server;

import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.Error;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
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
