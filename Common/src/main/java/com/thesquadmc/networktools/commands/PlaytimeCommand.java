package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.player.stats.Season;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import org.bukkit.entity.Player;

public class PlaytimeCommand {

    @Command(name = "playtime", playerOnly = true)
    public void playime(CommandArgs args) {
        Player player = args.getPlayer();
        TSMCUser user = TSMCUser.fromPlayer(player);

        long start = System.currentTimeMillis();
        for (Season season : user.getAllSeasons()) {

        }
    }
}
