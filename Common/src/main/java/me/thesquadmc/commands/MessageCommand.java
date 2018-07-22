package me.thesquadmc.commands;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class MessageCommand {

    private final NetworkTools plugin;

    public MessageCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"message", "msg"}, playerOnly = true)
    public void messgae(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() < 2) {
            player.sendMessage(CC.RED + "/message <player> <message>");
            return;
        }

        String name = args.getArg(0);
        String message = String.join(" ", Arrays.copyOfRange(args.getArgs(), 1, args.length()));

        if (Bukkit.getPlayer(name) != null) {
            Player target = Bukkit.getPlayer(name);

            player.sendMessage(CC.translate("&6Me &7■ &6{0} &8» &e{1}", target.getName(), message));
            target.sendMessage(CC.translate("&6{0} &7■ &6Me &8» &e{1}", player.getName(), message));

            //TODO new SocialSpyEvent

            TSMCUser.fromPlayer(player).setLastMessager(target.getUniqueId());
            TSMCUser.fromPlayer(target).setLastMessager(player.getUniqueId());

        } else {
            plugin.getRedisManager().executeJedisAsync(jedis -> {
                UUID uuid = plugin.getUUIDTranslator().getUUID(name, true);

                if (uuid == null) {
                    player.sendMessage(CC.RED + "Could not find " + name);
                    return;
                }

                if (jedis.exists("players:" + uuid.toString())) {
                    plugin.getRedisManager().sendMessage(RedisChannels.MESSAGE, RedisMesage.newMessage()
                            .set("sender", player.getUniqueId())
                            .set("sender_name", player.getName())
                            .set("target", uuid)
                            .set("message", message));

                    player.sendMessage(CC.translate("&6Me &7■ &6{0} &8» &e{1}", name, message));
                    TSMCUser.fromPlayer(player).setLastMessager(uuid);

                } else {
                    player.sendMessage(CC.RED + "Could not find " + name);
                }
            });
        }
    }

    @Command(name = {"reply", "r"}, playerOnly = true)
    public void reply(CommandArgs args) {
        Player player = args.getPlayer();

        TSMCUser user = TSMCUser.fromPlayer(player);
        if (user.getLastMessager() == null) {
            player.sendMessage(CC.RED + "You have no one to reply to!");
            return;
        }

        Multithreading.runAsync(() -> {
            String name = plugin.getUUIDTranslator().getName(user.getLastMessager(), true);

            if (name == null) {
                player.sendMessage(CC.RED + "Could not find the last person you replied to!");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> player.chat("/msg " + name + " " + String.join(" ", args.getArgs())));
        });
    }
}
