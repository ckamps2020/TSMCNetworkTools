package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class OpAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
                player.sendMessage(CC.translate("&e&lFAKE OP &6■ &7You have fake opped the server"));
                Bukkit.broadcastMessage(CC.translate("&e&lOP &6■ &7You have been opped"));
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
