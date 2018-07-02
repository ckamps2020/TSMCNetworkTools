package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

        Player target = Bukkit.getPlayer(args.getArg(0));
        TSMCUser targetUser = TSMCUser.fromPlayer(player);

        plugin.getRedisManager().executeJedisAsync(jedis -> {
        });
    }
}
