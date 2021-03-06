package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AdminChatCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public AdminChatCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = networkTools.getLuckPermsApi().getUser(player.getUniqueId());
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
                TSMCUser tsmcUser = TSMCUser.fromPlayer(player);
                if (args.length == 0) {
                    if (!tsmcUser.getSetting(PlayerSetting.ADMINCHAT)) {
                        tsmcUser.updateSetting(PlayerSetting.ADMINCHAT, true);
                        player.sendMessage(CC.translate("&e&lADMIN CHAT &6■ &7You toggled Admin Chat &eon&7!"));
                    } else {
                        tsmcUser.updateSetting(PlayerSetting.ADMINCHAT, false);
                        player.sendMessage(CC.translate("&e&lADMIN CHAT &6■ &7You toggled Admin Chat &eoff&7!"));
                    }

                } else {
                    if (!tsmcUser.getSetting(PlayerSetting.ADMINCHAT)) {
                        player.sendMessage(CC.translate("&e&lADMIN CHAT &6■ &7Please enable adminchat first!"));
                        return true;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : args) {
                        stringBuilder.append(s).append(" ");
                    }

                    UserData cachedData = user.getCachedData();
                    Contexts contexts = Contexts.allowAll();
                    MetaData metaData = cachedData.getMetaData(contexts);
                    String finalMessage = "&8[&c&lADMINCHAT&8] " + metaData.getPrefix() + "" + player.getName() + " &8» &c" + stringBuilder.toString();
                    networkTools.getRedisManager().sendMessage(RedisChannels.ADMINCHAT, RedisMesage.newMessage()
                            .set(RedisArg.MESSAGE, finalMessage));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
