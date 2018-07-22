package me.thesquadmc.commands;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class MOTDClearCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public MOTDClearCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("group.manager")) {
            sender.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            return false;
        }

        networkTools.getRedisManager().sendMessage(RedisChannels.MOTD, RedisMesage.newMessage()
                .set(String.valueOf(1), " ")
                .set(String.valueOf(2), " "));

        networkTools.getRedisManager().executeJedisAsync(jedis -> {
            jedis.set("motd-line1", "");
            jedis.set("motd-line2", "");

            sender.sendMessage(CC.translate("&aMOTD &6■ &7Cleared the MOTD!"));
        });

        return true;
    }
}
