package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.utils.*;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FindCommand implements CommandExecutor {

    private final Main main;
    private static List<String> stillLooking = new ArrayList<>();

    public FindCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                if (args.length == 1) {
                    String name = args[0];
                    Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                        player.sendMessage(CC.translate("&e&lFIND&6■ &7Trying to find &e" + name + "&7..."));

                        stillLooking.add(player.getName());

                        main.getRedisManager().sendMessage(RedisChannels.FIND, RedisMesage.newMessage()
                                .set(RedisArg.SERVER, Bukkit.getServerName())
                                .set(RedisArg.PLAYER, name)
                                .set(RedisArg.ORIGIN_PLAYER, player.getName()));

                        /*
                        Multithreading.runAsync(new Runnable() {
                            @Override
                            public void run() {
                                try (Jedis jedis = main.getPool().getResource()) {
                                    JedisTask.withName(UUID.randomUUID().toString())
                                            .withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
                                            .withArg(RedisArg.PLAYER.getArg(), name)
                                            .withArg(RedisArg.ORIGIN_PLAYER.getArg(), player.getName())
                                            .send(RedisChannels.FIND.getChannelName(), jedis);
                                }
                            }
                        });*/

                        Bukkit.getScheduler().runTaskLater(main, () -> {
                            if (stillLooking.contains(player.getName())) {
                                stillLooking.remove(player.getName());
                                player.sendMessage(CC.translate("&e&lFIND&6■ &7Unable to find player &e" + name));
                            }
                        }, 20L);
                    });
                } else {
                    player.sendMessage(CC.translate("&e&lFIND &6■ &7Usage: /find <player>"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

    public static List<String> getStillLooking() {
        return stillLooking;
    }

}
