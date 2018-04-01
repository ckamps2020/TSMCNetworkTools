package me.thesquadmc.commands;

import com.google.common.base.Preconditions;
import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

public final class MOTDCommand implements CommandExecutor {

    private final Main main;
    private final int line;

    public MOTDCommand(Main main, int line) {
        this.main = main;
        Preconditions.checkState(line == 1 || line == 2, "Line must be 1 or 2");

        this.line = line;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("group.manager")) {
            sender.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            return false;
        }

        String motd = CC.translate(StringUtils.join(args, " "));

        Multithreading.runAsync(() -> {
            try (Jedis jedis = main.getPool().getResource()) {
                JedisTask.withName("a")
                        .withArg(String.valueOf(line), motd)
                        .send(RedisChannels.MOTD.getChannelName(), jedis);

                jedis.set("motd-line" + line, motd);
                sender.sendMessage(CC.translate("&aMOTD &6■ &7Set MOTD Line " + line + " to: " + motd));
            }
        });

        return true;
    }
}
