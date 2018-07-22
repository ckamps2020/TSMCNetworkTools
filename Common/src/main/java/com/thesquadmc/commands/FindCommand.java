package com.thesquadmc.commands;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.utils.command.Command;
import com.thesquadmc.utils.command.CommandArgs;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.msgs.StringUtils;
import com.thesquadmc.utils.msgs.Unicode;
import com.thesquadmc.utils.time.TimeUtils;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class FindCommand {

    private final NetworkTools plugin;

    public FindCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"find", "whereis", "locate"})
    public void find(CommandArgs args) {
        CommandSender sender = args.getSender();

        if (args.length() == 0) {
            sender.sendMessage(CC.RED + "/whereis <player>");
            return;
        }

        String name = args.getArg(0);

        sender.sendMessage(CC.translate("&e&lWHEREIS&6■ &7Trying to find &e" + name + "&7..."));
        sender.sendMessage(" ");
        plugin.getRedisManager().executeJedisAsync(jedis -> {
            plugin.getUUIDTranslator().getUUID(name, true).thenAcceptAsync(uuid -> {
                if (uuid == null) {
                    sender.sendMessage(CC.translate("&e&lWHEREIS&6■ &7Unable to find player &e" + name));
                    return;
                }

                Map<String, String> server = jedis.hgetAll("players:" + uuid.toString());
                if (server == null) {
                    sender.sendMessage(CC.translate("&e&lWHEREIS&6■ &7Something went wrong with getting &e" + name));
                    return;
                }

                long timestamp;
                if (server.containsKey("lastOnline")) {
                    timestamp = System.currentTimeMillis() - Long.parseLong(server.get("lastOnline"));

                    sender.sendMessage(CC.B_YELLOW + name);
                    sender.sendMessage(CC.GRAY + Unicode.SQUARE + " Status: " + CC.RED + "Offline");
                    sender.sendMessage(CC.GRAY + Unicode.SQUARE + " Offline for: " + CC.WHITE + TimeUtils.getFormattedTime(timestamp));

                } else if (server.containsKey("onlineSince")) {
                    timestamp = System.currentTimeMillis() - Long.parseLong(server.get("onlineSince"));

                    sender.sendMessage(CC.B_YELLOW + name);
                    sender.sendMessage(CC.GRAY + Unicode.SQUARE + " Status: " + CC.GREEN + "Online");
                    sender.sendMessage(CC.GRAY + Unicode.SQUARE + " Server: " + CC.WHITE + StringUtils.capitalize(server.get("server")));
                    sender.sendMessage(CC.GRAY + Unicode.SQUARE + " Online Since: " + CC.WHITE + TimeUtils.getFormattedTime(timestamp));

                } else {
                    sender.sendMessage(CC.translate("&e&lWHEREIS&6■ &7Unable to find player &e" + name));
                }
            });
        });
    }
}
