package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import com.thesquadmc.networktools.utils.nms.TitleUtils;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TitleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                String string = StringUtils.buildMessage(args, 0);
                String regex = "[~]+";
                String[] tokens = string.split(regex);
                int i = 0;
                String title = null;
                String subtitle = null;
                for (String s : tokens) {
                    if (i == 0) {
                        title = s;
                        i++;
                    } else if (i == 1) {
                        subtitle = s;
                        i++;
                    } else {
                        break;
                    }
                }
                if (title != null && subtitle != null) {
                    sender.sendMessage(CC.translate("&e&lTITLE &6■ &7Title sent!"));
                    TitleUtils.sendTitleToServer(title, subtitle, 20, 40, 20);
                } else {
                    sender.sendMessage(CC.translate("&e&lTITLE &6■ &7Please supply a valid title and subtitle!"));
                }
            } else {
                sender.sendMessage(CC.translate("&e&lTITLE &6■ &7Usage: /title <msg>~<msg>"));
            }
        } else {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
                if (args.length >= 1) {
                    String string = StringUtils.buildMessage(args, 0);
                    String regex = "[~]+";
                    String[] tokens = string.split(regex);
                    int i = 0;
                    String title = null;
                    String subtitle = null;
                    for (String s : tokens) {
                        if (i == 0) {
                            title = s;
                            i++;
                        } else if (i == 1) {
                            subtitle = s;
                            i++;
                        } else {
                            break;
                        }
                    }
                    if (title != null && subtitle != null) {
                        player.sendMessage(CC.translate("&e&lTITLE &6■ &7Title sent!"));
                        TitleUtils.sendTitleToServer(title, subtitle, 20, 40, 20);
                    } else {
                        player.sendMessage(CC.translate("&e&lTITLE &6■ &7Please supply a valid title and subtitle!"));
                    }
                } else {
                    player.sendMessage(CC.translate("&e&lTITLE &6■ &7Usage: /title <msg>~<msg>"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
