package me.thesquadmc.commands;

import com.google.common.base.Preconditions;
import me.thesquadmc.NetworkTools;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public final class MOTDCommand implements CommandExecutor {

    private final NetworkTools networkTools;
    private final int line;

    public MOTDCommand(NetworkTools networkTools, int line) {
        this.networkTools = networkTools;
        Preconditions.checkState(line == 1 || line == 2, "Line must be 1 or 2");

        this.line = line;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("group.manager")) {
            sender.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            return false;
        }

        System.out.println(Arrays.toString(args));
        String motd = CC.translate(StringUtils.join(args, " "));
        System.out.println(line);

        networkTools.getRedisManager().sendMessage(RedisChannels.MOTD, RedisMesage.newMessage()
                .set(String.valueOf(line), motd));
        networkTools.getRedisManager().executeJedisAsync(jedis -> jedis.set(String.valueOf(line), motd));

        sender.sendMessage(CC.translate("&aMOTD &6■ &7Set MOTD Line " + line + " to: " + motd));
        return true;
    }
}
