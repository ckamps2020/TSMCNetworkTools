package com.thesquadmc.networktools.listeners;

import com.thesquadmc.networktools.commands.StaffmodeCommand;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.UpdateType;
import com.thesquadmc.networktools.utils.handlers.UpdateEvent;
import com.thesquadmc.networktools.utils.nms.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class VanishListener implements Listener {

    @EventHandler
    public void on(UpdateEvent e) {
        if (e.getUpdateType() == UpdateType.SEC) {

            for (Player player : Bukkit.getOnlinePlayers()) {
                TSMCUser user = TSMCUser.fromPlayer(player);

                if (user.getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                    if (user.isNicknamed()) {
                        TitleUtils.sendActionBarToPlayer("&e&lVanished &7and nicknamed as &e&l" + player.getName(), player);
                    } else {
                        TitleUtils.sendActionBarToPlayer("&7Vanish is &e&lenabled", player);
                    }
                } else if (user.isNicknamed()) {
                    TitleUtils.sendActionBarToPlayer("&7Nicked as &e&l" + player.getName() + "&7", player);
                } else if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                    TitleUtils.sendActionBarToPlayer("&7Staffmode is &e&lenabled", player);
                } else if (user.getSetting(PlayerSetting.VANISHED)) {
                    TitleUtils.sendActionBarToPlayer("&7Vanish is &e&lenabled", player);
                }
            }
        }
    }

}
