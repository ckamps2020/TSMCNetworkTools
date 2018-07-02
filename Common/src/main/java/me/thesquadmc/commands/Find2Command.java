package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.msgs.Unicode;
import me.thesquadmc.utils.server.WorldUtils;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class Find2Command {

    private final Main plugin;

    public Find2Command(Main plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"find2", "whereis2"}, permission = "group.helper")
    public void find(CommandArgs args) {
        CommandSender sender = args.getSender();

        if (args.length() == 0) {
            sender.sendMessage(CC.RED + "/find <player>");
            return;
        }
        String name = args.getArg(0);

        sender.sendMessage(CC.translate("&e&lFIND&6■ &7Trying to find &e" + name + "&7..."));
        sender.sendMessage(" ");
        plugin.getRedisManager().executeJedisAsync(jedis -> {
            UUID uuid = plugin.getUUIDTranslator().getUUID(name, true);

            if (uuid == null) {
                sender.sendMessage(CC.translate("&e&lFIND&6■ &7Unable to find player &e" + name));
                return;
            }

            Map<String, String> server = jedis.hgetAll("players:" + uuid.toString());
            if (server == null) {
                sender.sendMessage(CC.translate("&e&lFIND&6■ &7Something went wrong with getting &e" + name));
                return;
            }

            long timestamp;
            System.out.println(server);
            if (server.containsKey("lastOnline")) {
                timestamp = System.currentTimeMillis() - Long.parseLong(server.get("lastOnline"));

                sender.sendMessage(CC.B_YELLOW + name);
                sender.sendMessage(CC.GRAY + Unicode.SQUARE  + " Status: " + CC.RED + "Offline");
                sender.sendMessage(CC.GRAY + Unicode.SQUARE  + " Offline for: " + CC.WHITE + TimeUtils.getFormattedTime(timestamp));

            } else if (server.containsKey("onlineSince")) {
                timestamp = System.currentTimeMillis() - Long.parseLong(server.get("onlineSince"));

                sender.sendMessage(CC.B_YELLOW + name);
                sender.sendMessage(CC.GRAY + Unicode.SQUARE  + " Status: " + CC.GREEN + "Online");
                sender.sendMessage(CC.GRAY + Unicode.SQUARE  + " Server: " + CC.WHITE + StringUtils.capitalize(server.get("server")));
                sender.sendMessage(CC.GRAY + Unicode.SQUARE +  " Online Since: " + CC.WHITE + TimeUtils.getFormattedTime(timestamp));

            } else {
                sender.sendMessage(CC.translate("&e&lFIND&6■ &7Unable to find player &e" + name));
            }
        });
    }
}
