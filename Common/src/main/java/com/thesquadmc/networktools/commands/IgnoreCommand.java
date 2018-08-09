package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class IgnoreCommand {

    private static final Rank[] CANNOT_IGNORE = new Rank[]{Rank.TRAINEE, Rank.MOD, Rank.SRMOD, Rank.ADMIN};

    private final NetworkTools plugin;

    public IgnoreCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"ignore"}, playerOnly = true)
    public void ignore(CommandArgs args) {
        Player player = args.getPlayer();

        Stream.of(
                CC.translate("&e/ignore &8- &6Sends you this message"),
                CC.translate("&e/ignore add <player> &8- &6Adds the player to your ignored list"),
                CC.translate("&e/ignore remove <player> &8- &6Removes a player from your ignored list"),
                CC.translate("&e/ignore check <player> &8- &6Checks if you are ignoring a player")
        ).forEach(player::sendMessage);
    }

    @Command(name = "ignore.add", playerOnly = true)
    public void add(CommandArgs args) {
        Player player = args.getPlayer();
        TSMCUser user = TSMCUser.fromPlayer(player);

        for (Rank rank : CANNOT_IGNORE) {
            if (PlayerUtils.doesRankMatch(player, rank)) {
                player.sendMessage(CC.RED + "You cannot ignore players!");
                return;
            }
        }

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "You must specify a player!");
            return;
        }

        String name = args.getArg(0);
        plugin.getUUIDTranslator().getUUID(name, false).thenAccept(uuid -> {
            if (uuid == null) {
                player.sendMessage(CC.RED + "Could not find data " + name);
                return;
            }

            if (user.isIgnored(uuid)) {
                player.sendMessage(CC.translate("&e&lIGNORE &6■ &7You are already ignoring &e{0}", name));

            } else {
                if (PlayerUtils.isEqualOrHigherThen(uuid, Rank.TRAINEE)) {
                    player.sendMessage(CC.RED + "You cannot ignore staff members!");
                    return;
                }

                user.addIgnoredPlayer(uuid);
                player.sendMessage(CC.translate("&e&lIGNORE &6■ &7You are now ignoring &e{0}", name));
            }
        });
    }

    @Command(name = "ignore.remove", playerOnly = true)
    public void remove(CommandArgs args) {
        Player player = args.getPlayer();
        TSMCUser user = TSMCUser.fromPlayer(player);

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "You must specify a player!");
            return;
        }

        String name = args.getArg(0);
        plugin.getUUIDTranslator().getUUID(name, true).thenAccept(uuid -> {
            if (user.isIgnored(uuid)) {
                user.removeIgnoredPlayer(uuid);
                player.sendMessage(CC.translate("&e&lIGNORE &6■ &7You are no longer ignoring &e{0}", name));

            } else {
                player.sendMessage(CC.translate("&e&lIGNORE &6■ &7You are not ignoring &e{0}", name));
            }
        });
    }

    @Command(name = "ignore.check", playerOnly = true)
    public void list(CommandArgs args) {
        Player player = args.getPlayer();
        TSMCUser user = TSMCUser.fromPlayer(player);

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "You must specify a player!");
            return;
        }

        String name = args.getArg(0);

        plugin.getUUIDTranslator().getUUID(name, true).thenAccept(uuid -> {
            if (user.isIgnored(uuid)) {
                player.sendMessage(CC.translate("&e&lIGNORE &6■ &e{0} &7is ignored!", name));

            } else {
                player.sendMessage(CC.translate("&e&lIGNORE &6■ &e{0} &7is not ignored!t", name));
            }
        });
    }
}
