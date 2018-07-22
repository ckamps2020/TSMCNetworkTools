package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.networking.redis.RedisMesage;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StopCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public StopCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
                if (args.length >= 2) {
                    String server = args[0];
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        stringBuilder.append(args[i] + " ");
                    }
                    player.sendMessage(CC.translate("&e&lSTOP &6■ &7You have stopped &e" + server + " &7for &e" + stringBuilder.toString() + "&7"));

                    networkTools.getRedisManager().sendMessage(RedisChannels.STOP, RedisMesage.newMessage()
                            .set(RedisArg.SERVER, server.toUpperCase())
                            .set(RedisArg.MESSAGE, stringBuilder.toString()));

                } else {
                    player.sendMessage(CC.translate("&e&lSTOP &6■ &7Usage: /stop <servertype|server|all> <reason>"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        } else {
            Bukkit.shutdown();
        }
        return true;
    }

}
