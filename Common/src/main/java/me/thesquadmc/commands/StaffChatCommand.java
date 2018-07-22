package me.thesquadmc.commands;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import me.thesquadmc.NetworkTools;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.player.PlayerSetting;
import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
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
            User user = networkTools.getLuckPermsApi().getUser(player.getUniqueId());
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                TSMCUser tsmcUser = TSMCUser.fromPlayer(player);
                if (args.length == 0) {
                    if (!tsmcUser.getSetting(PlayerSetting.STAFFCHAT_ENABLED)) {
                        tsmcUser.updateSetting(PlayerSetting.STAFFCHAT_ENABLED, true);
                        player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7You toggled Staff Chat &eon&7!"));
                    } else {
                        tsmcUser.updateSetting(PlayerSetting.STAFFCHAT_ENABLED, false);
                        player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7You toggled Staff Chat &eoff&7!"));
                    }
                } else {
                    if (!tsmcUser.getSetting(PlayerSetting.STAFFCHAT_ENABLED)) {
                        player.sendMessage(CC.translate("&e&lSTAFF CHAT &6■ &7Please enable staffchat first!"));
                        return true;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : args) {
                        stringBuilder.append(s + " ");
                    }
                    UserData cachedData = user.getCachedData();
                    Contexts contexts = Contexts.allowAll();
                    MetaData metaData = cachedData.getMetaData(contexts);
                    String finalMessage = "&8[&a&lSTAFFCHAT&8] " + metaData.getPrefix() + "" + player.getName() + " &8» &a" + stringBuilder.toString();

                    networkTools.getRedisManager().sendMessage(RedisChannels.STAFFCHAT, RedisMesage.newMessage()
                            .set(RedisArg.MESSAGE, finalMessage)
                            .set(RedisArg.SERVER, Bukkit.getServerName()));

                    networkTools.getRedisManager().sendMessage(RedisChannels.DISCORD_STAFFCHAT_DISCORD, RedisMesage.newMessage()
                            .set(RedisArg.PLAYER, player.getName())
                            .set(RedisArg.MESSAGE, stringBuilder.toString())
                            .set(RedisArg.SERVER, Bukkit.getServerName()));

                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
