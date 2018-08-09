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

public final class ManagerChatCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public ManagerChatCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = networkTools.getLuckPermsApi().getUser(player.getUniqueId());
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
                TSMCUser tsmcUser = TSMCUser.fromPlayer(player);
                if (args.length == 0) {
                    if (!tsmcUser.getSetting(PlayerSetting.MANAGERCHAT)) {
                        tsmcUser.updateSetting(PlayerSetting.MANAGERCHAT, true);
                        player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eon&7!"));
                    } else {
                        tsmcUser.updateSetting(PlayerSetting.MANAGERCHAT, false);
                        player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eoff&7!"));
                    }
                } else {
                    if (!tsmcUser.getSetting(PlayerSetting.MANAGERCHAT)) {
                        player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7Please enable managerchat first!"));
                        return true;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : args) {
                        stringBuilder.append(s + " ");
                    }
                    UserData cachedData = user.getCachedData();
                    Contexts contexts = Contexts.allowAll();
                    MetaData metaData = cachedData.getMetaData(contexts);
                    String finalMessage = "&8[&c&lMANAGERCHAT&8] " + metaData.getPrefix() + "" + player.getName() + " &8» &c" + stringBuilder.toString();

                    networkTools.getRedisManager().sendMessage(RedisChannels.MANAGERCHAT, RedisMesage.newMessage()
                            .set(RedisArg.MESSAGE, finalMessage));

                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
