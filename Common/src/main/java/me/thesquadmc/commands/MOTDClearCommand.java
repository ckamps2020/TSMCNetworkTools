package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.Jedis;

public final class MOTDClearCommand implements CommandExecutor {

    private final Main main;

    public MOTDClearCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("group.manager")) {
            sender.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            return false;
        }

        main.getRedisManager().sendMessage(RedisChannels.MOTD, RedisMesage.newMessage()
                .set(String.valueOf(1), " ")
                .set(String.valueOf(2), " "));

        main.getRedisManager().executeJedisAsync(jedis -> {
            jedis.set("motd-line1", "");
            jedis.set("motd-line2", "");

            sender.sendMessage(CC.translate("&aMOTD &6■ &7Cleared the MOTD!"));
        });

        return true;
    }
}
