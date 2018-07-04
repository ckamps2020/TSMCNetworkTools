package me.thesquadmc.commands;

import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.Unicode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NickCommand {

    @Command(name = {"nickname", "nick"}, playerOnly = true)
    public void on(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/nickname <name> [player]");
            return;
        }

        TSMCUser user = TSMCUser.fromPlayer(player);
        String name = args.getArg(0);

        user.setServerNickname(Bukkit.getServerName(), name);
        player.setDisplayName(name);

        player.sendMessage(CC.B_YELLOW + "NICK" + CC.GOLD + Unicode.SQUARE + CC.GRAY + " Set your nickname as " + name);

    }
}
