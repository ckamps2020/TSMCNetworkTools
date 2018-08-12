package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AlertCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public AlertCommand(NetworkTools networkTools) {
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
                        stringBuilder.append(args[i]).append(" ");
                    }
                    player.sendMessage(CC.translate("&e&lALERT &6■ &7You sent an alert:"));
                    player.sendMessage(CC.translate("  &7Server: &e" + server));
                    player.sendMessage(CC.translate("  &7Message: &e" + stringBuilder.toString()));

                    networkTools.getRedisManager().sendMessage(RedisChannels.ANNOUNCEMENT, RedisMesage.newMessage()
                            .set(RedisArg.SERVER, server.toUpperCase())
                            .set(RedisArg.MESSAGE, stringBuilder.toString()));
                } else {
                    player.sendMessage(CC.translate("&e&lALERT &6■ &7Usage: /alert <servertype|server|all> <message>"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
