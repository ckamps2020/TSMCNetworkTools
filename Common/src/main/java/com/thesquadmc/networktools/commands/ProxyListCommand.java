package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
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

public final class ProxyListCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public ProxyListCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                networkTools.getRedisManager().sendMessage(RedisChannels.PROXY_REQUEST, RedisMesage.newMessage()
                        .set(RedisArg.SERVER, Bukkit.getServerName())
                        .set(RedisArg.PLAYER, player.getName()));
                /*
                Bukkit.getScheduler().runTaskAsynchronously(networkTools, new Runnable() {
                    @Override
                    public void run() {
                        Multithreading.runAsync(new Runnable() {
                            @Override
                            public void run() {
                                try (Jedis jedis = networkTools.getPool().getResource()) {
                                    JedisTask.withName(UUID.randomUUID().toString())
                                            .withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
                                            .withArg(RedisArg.PLAYER.getArg(), player.getName())
                                            .send(RedisChannels.PROXY_REQUEST.getChannelName(), jedis);
                                }
                            }
                        });
                    }
                });*/
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
