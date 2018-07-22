package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.networking.redis.RedisMesage;
import com.thesquadmc.player.PlayerSetting;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.enums.Rank;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
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
                    if (!tsmcUser.getSetting(PlayerSetting.MANAGERCHAT_ENABLED)) {
                        tsmcUser.updateSetting(PlayerSetting.MANAGERCHAT_ENABLED, true);
                        player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eon&7!"));
                    } else {
                        tsmcUser.updateSetting(PlayerSetting.MANAGERCHAT_ENABLED, false);
                        player.sendMessage(CC.translate("&e&lMANAGER CHAT &6■ &7You toggled Manager Chat &eoff&7!"));
                    }
                } else {
                    if (!tsmcUser.getSetting(PlayerSetting.MANAGERCHAT_ENABLED)) {
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

                    /*Bukkit.getScheduler().runTaskAsynchronously(networkTools, new Runnable() {
                        @Override
                        public void run() {
                            Multithreading.runAsync(new Runnable() {
                                @Override
                                public void run() {
                                    try (Jedis jedis = networkTools.getPool().getResource()) {
                                        JedisTask.withName(UUID.randomUUID().toString())
                                                .withArg(RedisArg.MESSAGE.getArg(), finalMessage)
                                                .send(RedisChannels.MANAGERCHAT.getChannelName(), jedis);
                                    }
                                }
                            });
                        }
                    });*/
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
