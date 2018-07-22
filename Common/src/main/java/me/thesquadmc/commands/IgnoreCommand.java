package me.thesquadmc.commands;

import me.thesquadmc.NetworkTools;
import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

public class IgnoreCommand {

    private static final Rank[] CANNOT_IGNORE = new Rank[]{Rank.TRAINEE, Rank.MOD, Rank.SRMOD};

    private final NetworkTools plugin;

    public IgnoreCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"ignore"}, playerOnly = true)
    public void ignore(CommandArgs args) {
        Player player = args.getPlayer();

        for (Rank rank : CANNOT_IGNORE) {
            if (PlayerUtils.doesRankMatch(player, rank)) {
                player.sendMessage(CC.RED + "You cannot ignore players!");
                return;
            }
        }

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/ignore <player name>");
            return;
        }

        String name = args.getArg(0);
        plugin.getUUIDTranslator().getUUID(name, true).thenAccept(uuid -> {
            if (uuid == null) {
                player.sendMessage(CC.RED + "Unable to find the information for " + name);
                return;
            }

            if (PlayerUtils.isEqualOrHigherThen(uuid, Rank.TRAINEE)) {
                player.sendMessage(CC.RED + "You cannot ignore staff members!");
                return;
            }

            TSMCUser.fromPlayer(player).addIgnoredPlayer(uuid);
            player.sendMessage(CC.translate("&e&lIGNORE &6■ &e{0} &7is now ignored!", name));
        });

    }
}
