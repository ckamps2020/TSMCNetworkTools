package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.networking.redis.RedisMesage;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.command.Command;
import com.thesquadmc.utils.command.CommandArgs;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.player.PlayerUtils;
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

            if (TSMCUser.fromPlayer(target).isIgnored(player.getUniqueId())) {
                player.sendMessage(CC.RED + "You cannot send messages to this player!");
                return;
            }

            player.sendMessage(CC.translate("&6Me &7■ &6{0} &8» &e{1}", target.getName(), message));
            target.sendMessage(CC.translate("&6{0} &7■ &6Me &8» &e{1}", player.getName(), message));

            //TODO new SocialSpyEvent

            TSMCUser.fromPlayer(player).setLastMessager(target.getUniqueId());
            TSMCUser.fromPlayer(target).setLastMessager(player.getUniqueId());

        } else {
            plugin.getUUIDTranslator().getUUID(name, true).thenAccept(uuid -> {
                if (uuid == null) {
                    player.sendMessage(CC.RED + "Could not find " + name);
                    return;
                }

                plugin.getUserDatabase().getUser(uuid).thenAccept(user -> {
                    if (user != null && user.isIgnored(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You cannot send messages to this player!");
                        return;
                    }


                    PlayerUtils.isOnline(uuid).thenAccept(online -> {
                        if (online) {
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
                });
            });
        }
    }

    @Command(name = {"reply", "r"}, playerOnly = true)
    public void reply(CommandArgs args) {
        Player player = args.getPlayer();

        TSMCUser user = TSMCUser.fromPlayer(player);
        UUID uuid = user.getLastMessager();

        if (uuid == null) {
            player.sendMessage(CC.RED + "You have no one to reply to!");
            return;
        }

        PlayerUtils.isOnline(uuid).whenComplete((online, throwable) -> {
            if (online) {
                String message = String.join(" ", Arrays.copyOfRange(args.getArgs(), 0, args.length()));

                plugin.getRedisManager().sendMessage(RedisChannels.MESSAGE, RedisMesage.newMessage()
                        .set("sender", player.getUniqueId())
                        .set("sender_name", player.getName())
                        .set("target", uuid)
                        .set("message", message));

                TSMCUser.fromPlayer(player).setLastMessager(uuid);

                plugin.getUUIDTranslator().getName(uuid, true).thenAccept(name -> {
                    player.sendMessage(CC.translate("&6Me &7■ &6{0} &8» &e{1}", name, message));
                });

            } else {
                player.sendMessage(CC.RED + "Could not find the last player you messaged!");
            }
        });
    }
}
