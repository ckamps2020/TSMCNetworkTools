package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.nms.EntityUtils;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class FireworkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
                EntityUtils.launchRandomFirework(player.getLocation(), false);
                player.sendMessage(CC.translate("&e&lFIREWORK &6■ &7BOOM!"));
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }
}
