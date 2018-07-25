package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class YtNickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
                TSMCUser user = TSMCUser.fromPlayer(player);

                if (!user.isNicknamed()) {
                    if (args.length == 1) {
                        String name = args[0];
                        player.sendMessage(CC.translate("&e&lYT NICK &6■ &7You are now nicked as &e" + name));

                        user.setNickname(name);

                    } else {
                        player.sendMessage(CC.translate("&e&lYT NICK &6■ &7Usage: /ytnick (name)"));

                    }
                } else {
                    player.sendMessage(CC.translate("&e&lYT NICK &6■ &7You are &eno longer &7nicked"));
                    user.unsetNickname(false);
                    PlayerUtils.setName(player, user.getName());
                }

            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }

        return true;
    }

}
