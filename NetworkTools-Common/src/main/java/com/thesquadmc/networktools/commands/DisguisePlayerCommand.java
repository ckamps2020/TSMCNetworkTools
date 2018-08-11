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

public final class DisguisePlayerCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public DisguisePlayerCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
                if (args.length == 2) {
                    String user = args[0];
                    String name = args[1];
                    if (user.equalsIgnoreCase("all")) {
                        PlayerUtils.updateGlobalSkin(name);
                        Bukkit.getScheduler().runTaskLater(networkTools, () -> {
                            player.sendMessage(CC.translate("&e&lDISGUISE &6■ &7You have disguised the entire server to &e" + name));
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                PlayerUtils.setSameSkin(p);
                            }
                        }, 10L);
                    } else {
                        Player t = Bukkit.getPlayer(user);
                        if (t != null) {
                            PlayerUtils.setSkin(t, name);
                        } else {
                            player.sendMessage(CC.translate("&e&lDISGUISE &6■ &7That player is offline or doesnt exist!"));
                        }
                    }
                } else {
                    player.sendMessage(CC.translate("&e&lDISGUISE &6■ &7Usage: /disguiseplayer <player/all> <name>"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
