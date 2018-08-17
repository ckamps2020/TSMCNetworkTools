package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class OnlineCountCommand implements CommandExecutor {

    private final NetworkTools plugin;

    public OnlineCountCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                player.sendMessage(CC.translate("&7"));
                player.sendMessage(CC.translate("&c&lFACTIONS &c■ &7" + plugin.getCountManager().getFactionsCount()));
                player.sendMessage(CC.translate("&9&lCREATIVE &9■ &7" + plugin.getCountManager().getCreativeCount()));
                player.sendMessage(CC.translate("&d&lSKYBLOCK &d■ &7" + plugin.getCountManager().getSkyblockCount()));
                player.sendMessage(CC.translate("&d&lOP SKYBLOCK &d■ &7" + plugin.getCountManager().getOPSkyblockCount()));
                player.sendMessage(CC.translate("&b&lPRISON &b■ &7" + plugin.getCountManager().getPrisonCount()));
                player.sendMessage(CC.translate("&e&lEVENTS &e■ &7" + plugin.getCountManager().getEventCount()));
                player.sendMessage(CC.translate("&6&lHUB &6■ &7" + plugin.getCountManager().getHubCount()));
                player.sendMessage(CC.translate("&7"));
                player.sendMessage(CC.translate("&e" + plugin.getCountManager().getTotalOnlineCount() + "&8/&e4000"));
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
