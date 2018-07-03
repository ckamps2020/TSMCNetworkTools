package me.thesquadmc.commands;

import com.google.common.base.Joiner;
import me.thesquadmc.Main;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class MessageCommand {

    private final Main plugin;

    public MessageCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"message", "msg"}, playerOnly = true)
    public void messgae(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() < 2) {
            player.sendMessage(CC.RED + "/message <player> <message>");
            return;
        }

        plugin.getRedisManager().executeJedisAsync(jedis -> {
            UUID uuid = plugin.getUUIDTranslator().getUUID(args.getArg(0), true);

            if (uuid == null) {
                player.sendMessage(CC.RED + "Could not find " + args.getArg(0));
                return;
            }

            if (jedis.exists("players:" + uuid.toString())) {
                String message = Joiner.on(" ").join(Arrays.copyOfRange(args.getArgs(), 0, args.length()));

                plugin.getRedisManager().sendMessage(RedisChannels.MESSAGE, RedisMesage.newMessage()
                        .set("sender", player.getUniqueId())
                        .set("sender_name", player.getName())
                        .set("target", uuid)
                        .set("message", message));

                TSMCUser.fromPlayer(player).setLastMessager(uuid);
            }

        });
    }
}
