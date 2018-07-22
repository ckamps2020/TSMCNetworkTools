package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.Unicode;
import org.bukkit.entity.Player;

public class NickCommand {

    @Command(name = {"nickname", "nick"}, playerOnly = true)
    public void on(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/nickname <name> [player]");
            return;
        }

        player.sendMessage(CC.B_YELLOW + "NICK" + CC.GOLD + Unicode.SQUARE + CC.GRAY + " Set your nickname as ");

    }
}
