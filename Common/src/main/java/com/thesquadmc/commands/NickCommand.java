package com.thesquadmc.commands;

import com.thesquadmc.utils.command.Command;
import com.thesquadmc.utils.command.CommandArgs;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.msgs.Unicode;
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
