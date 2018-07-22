package com.thesquadmc.commands;

import com.thesquadmc.player.PlayerSetting;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.inventory.ItemBuilder;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.HELPER)) {
                TSMCUser user = TSMCUser.fromPlayer(player);
                if (!user.getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                    if (!user.getSetting(PlayerSetting.VANISHED)) {
                        PlayerUtils.hidePlayerSpectatorStaff(player);
                        user.updateSetting(PlayerSetting.VANISHED, true);
                        if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                            player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK, 10).name("&e&lToggle Vanish &7off").lore("&7Toggle vanish on or off").build());
                        }
                        player.sendMessage(CC.translate("&e&lVANISH &6■ &7You toggled vanish &eon&7! No one will be able to see you"));
                    } else {
                        PlayerUtils.showPlayerSpectator(player);
                        user.updateSetting(PlayerSetting.VANISHED, false);
                        if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                            player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK, 8).name("&e&lToggle Vanish &7on").lore("&7Toggle vanish on or off").build());
                        }
                        player.sendMessage(CC.translate("&e&lVANISH &6■ &7You toggled vanish &eoff&7! Everyone will be able to see you"));
                    }
                } else {
                    player.sendMessage(CC.translate("&e&lVANISH &6■ &7Please disabled YT Vanish first!"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
