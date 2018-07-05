package me.thesquadmc.commands;

import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import me.thesquadmc.Main;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.objects.PlayerSetting;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AdminChatCommand implements CommandExecutor {

    private final Main main;

    public AdminChatCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = main.getLuckPermsApi().getUser(player.getUniqueId());
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.ADMIN)) {
                TSMCUser tsmcUser = TSMCUser.fromPlayer(player);
                if (args.length == 0) {
                    if (!tsmcUser.getSetting(PlayerSetting.ADMINCHAT_ENABLED)) {
                        tsmcUser.updateSetting(PlayerSetting.ADMINCHAT_ENABLED, true);
                        player.sendMessage(CC.translate("&e&lADMIN CHAT &6■ &7You toggled Admin Chat &eon&7!"));
                    } else {
                        tsmcUser.updateSetting(PlayerSetting.ADMINCHAT_ENABLED, false);
                        player.sendMessage(CC.translate("&e&lADMIN CHAT &6■ &7You toggled Admin Chat &eoff&7!"));
                    }
                } else {
                    if (!tsmcUser.getSetting(PlayerSetting.ADMINCHAT_ENABLED)) {
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
                    main.getRedisManager().sendMessage(RedisChannels.ADMINCHAT, RedisMesage.newMessage()
                            .set(RedisArg.MESSAGE, finalMessage));

                    /*
                    Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                        @Override
                        public void run() {
                            Multithreading.runAsync(new Runnable() {
                                @Override
                                public void run() {
                                    try (Jedis jedis = main.getPool().getResource()) {
                                        JedisTask.withName(UUID.randomUUID().toString())
                                                .withArg(RedisArg.MESSAGE.getArg(), finalMessage)
                                                .send(RedisChannels.ADMINCHAT.getChannelName(), jedis);
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
