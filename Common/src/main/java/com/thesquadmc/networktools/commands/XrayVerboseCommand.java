package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class XrayVerboseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                TSMCUser user = TSMCUser.fromPlayer(player);
                if (Bukkit.getServerName().toUpperCase().contains("HUB")) {
                    player.sendMessage(CC.translate("&8[&4&lAntiCheat&8] &4[XRAY] &fYou are not allowed to use this command here!"));
                    return true;
                }
                if (!user.getSetting(PlayerSetting.XRAY_NOTIFICATION)) {
                    user.updateSetting(PlayerSetting.XRAY_NOTIFICATION, true);
                    player.sendMessage(CC.translate("&8[&4&lAntiCheat&8] &4[XRAY] &fYou enabled Xray Verbose"));
                } else {
                    user.updateSetting(PlayerSetting.XRAY_NOTIFICATION, false);
                    player.sendMessage(CC.translate("&8[&4&lAntiCheat&8] &4[XRAY] &fYou disabled Xray Verbose"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
