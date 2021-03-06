package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import com.thesquadmc.networktools.utils.msgs.Unicode;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class FindCommand {

    private final NetworkTools plugin;

    public FindCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"find", "whereis", "locate"}, permission = "group.trainee")
    public void find(CommandArgs args) {
        CommandSender sender = args.getSender();

        if (args.length() == 0) {
            sender.sendMessage(CC.RED + "/whereis <player>");
            return;
        }

        String name = args.getArg(0);

        sender.sendMessage(CC.translate("&e&lWHEREIS&6■ &7Trying to find &e" + name + "&7..."));
        sender.sendMessage(" ");
        plugin.getUUIDTranslator().getUUID(name, false).thenAcceptAsync(uuid -> {
            if (uuid == null) {
                sender.sendMessage(CC.translate("&e&lWHEREIS&6■ &7Unable to find player &e" + name));
                return;
            }

            try (Jedis jedis = plugin.getRedisManager().getResource()) {
                Map<String, String> server = jedis.hgetAll("player:" + uuid.toString());
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

                    sender.sendMessage(CC.translate("&e&lFound {0}:", name));
                    sender.sendMessage(CC.translate("&7■ &eStatus: &aOnline"));
                    sender.sendMessage(CC.translate("&7■ &eServer: &f{0}", StringUtils.capitalize(server.get("server"))));
                    sender.sendMessage(CC.translate("&7■ &eOnline since: &f{0} ago", TimeUtils.getFormattedTime(timestamp)));

                } else {
                    sender.sendMessage(CC.translate("&e&lWHEREIS&6■ &7Unable to find player &e" + name));
                }
            }
        });
    }
}
