package com.thesquadmc.networktools.commands;

import com.sgtcaze.nametagedit.NametagEdit;
import com.sgtcaze.nametagedit.api.data.Nametag;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StaffChatCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public StaffChatCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
                return false;
            }

            TSMCUser tsmcUser = TSMCUser.fromPlayer(player);
            if (args.length == 0) {
                if (!tsmcUser.getSetting(PlayerSetting.STAFFCHAT)) {
                    tsmcUser.updateSetting(PlayerSetting.STAFFCHAT, true);
                    player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7You toggled Staff Chat &eon&7!"));
                } else {
                    tsmcUser.updateSetting(PlayerSetting.STAFFCHAT, false);
                    player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7You toggled Staff Chat &eoff&7!"));
                }
            } else {
                if (!tsmcUser.getSetting(PlayerSetting.STAFFCHAT)) {
                    player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7Please enable staffchat first!"));
                    return true;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : args) {
                    stringBuilder.append(s).append(" ");
                }

                Nametag nametag = NametagEdit.getApi().getNametag(player);
                String finalMessage = "&8[&a&lSTAFFCHAT&8] " + nametag.getPrefix() + "" + player.getName() + " &8» &a" + stringBuilder.toString();

                networkTools.getRedisManager().sendMessage(RedisChannels.STAFFCHAT, RedisMesage.newMessage()
                        .set(RedisArg.MESSAGE, finalMessage)
                        .set(RedisArg.SERVER, Bukkit.getServerName()));

                networkTools.getRedisManager().sendMessage(RedisChannels.DISCORD_STAFFCHAT_DISCORD, RedisMesage.newMessage()
                        .set(RedisArg.PLAYER, player.getName())
                        .set(RedisArg.MESSAGE, stringBuilder.toString())
                        .set(RedisArg.SERVER, Bukkit.getServerName()));

            }
        }
        return true;
    }

}
