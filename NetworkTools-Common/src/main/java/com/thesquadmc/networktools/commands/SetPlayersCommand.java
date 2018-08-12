package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.server.ServerProperty;
import com.thesquadmc.networktools.utils.server.ServerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SetPlayersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
                if (args.length == 1) {
                    try {
                        int amount = Integer.valueOf(args[0]);
                        ServerUtils.setServerProperty(ServerProperty.MAX_PLAYERS, amount);
                        ServerUtils.savePropertiesFile();
                        player.sendMessage(CC.translate("&e&lPLAYERS &6■ &7Server player cap set to &e{0}", amount));
                    } catch (Exception e) {
                        player.sendMessage(CC.translate("&e&lPLAYERS &6■ &e{0} &7is not a valid number", args[0]));
                    }
                } else {
                    player.sendMessage(CC.translate("&e&lSMITE &6■ &7Usage: /setplayers <amount>"));
                }
            } else {
                player.sendMessage(CC.translate("&cYou do not have permission to use this command!"));
            }
        }
        return true;
    }

}