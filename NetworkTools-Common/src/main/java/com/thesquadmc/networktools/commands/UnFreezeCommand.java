package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class UnFreezeCommand implements CommandExecutor {


    private final NetworkTools networkTools;

    public UnFreezeCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
                if (args.length == 1) {
                    String name = args[0];
                    Player t = Bukkit.getPlayer(name);
                    if (t != null) {
                        if (!PlayerUtils.isEqualOrHigherThen(t, Rank.MOD)) {
                            if (FreezeCommand.getFrozen().contains(t.getUniqueId())) {
                                PlayerUtils.unfreezePlayer(t);
                                networkTools.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
                                networkTools.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
                                networkTools.getFrozenInventory().getAdmitted().remove(player.getUniqueId());
                                networkTools.getFrozenInventory().getDenying().remove(player.getUniqueId());
                                networkTools.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
                                FreezeCommand.getFrozen().remove(t.getUniqueId());
                                t.closeInventory();
                                t.sendMessage(CC.translate("&e&lFREEZE &6■ &7You have been &eunfrozen&7. Thank you for your &epatience&7"));
                                player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You have unfrozen &e" + t.getName() + "&7!"));
                            } else {
                                player.sendMessage(CC.translate("&e&lFREEZE &6■ &7That player is not frozen!"));
                            }
                        } else {
                            player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You are not allowed to freeze another staff member!"));
                        }
                    } else {
                        player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You do not have permission to use this command!"));
                    }
                } else {
                    player.sendMessage(CC.translate("&e&lFREEZE &6■ &7Usage: /freeze <player>"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
