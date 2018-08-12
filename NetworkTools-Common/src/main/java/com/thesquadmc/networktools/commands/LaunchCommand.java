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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class LaunchCommand implements CommandExecutor {

    private static List<UUID> launched = new ArrayList<>();
    private final NetworkTools networkTools;

    public LaunchCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    public static List<UUID> getLaunched() {
        return launched;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("all")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.setVelocity(new Vector(0, 10, 0));
                            p.sendMessage(CC.translate("&c&lWHOOSH!"));
                            launched.add(p.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(networkTools, () -> launched.remove(p.getUniqueId()), 30L);
                        }
                    } else {
                        Player t = Bukkit.getPlayer(args[0]);
                        if (t != null) {
                            player.sendMessage(CC.translate("&e&lLAUNCH &6■ &7You launched " + t.getName()));
                            t.setVelocity(new Vector(0, 10, 0));
                            t.sendMessage(CC.translate("&c&lWHOOSH!"));
                            launched.add(t.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(networkTools, () -> launched.remove(t.getUniqueId()), 8 * 20L);
                        } else {
                            player.sendMessage(CC.translate("&e&lLAUNCH &6■ &7That player is offline or does not exist!"));
                        }
                    }
                } else {
                    for (Player p : PlayerUtils.getNearbyPlayers(player.getLocation(), 300)) {
                        p.setVelocity(new Vector(0, 10, 0));
                        p.sendMessage(CC.translate("&c&lWHOOSH!"));
                        launched.add(p.getUniqueId());
                        Bukkit.getScheduler().runTaskLater(networkTools, () -> launched.remove(p.getUniqueId()), 8 * 20L);
                    }
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
