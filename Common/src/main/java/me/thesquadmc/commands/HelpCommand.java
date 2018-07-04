package me.thesquadmc.commands;

import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(CC.translate("&8&l&m------------------------------------"));
            player.sendMessage(CC.translate("&e&lHelp message here"));
            player.sendMessage(CC.translate("&8&l&m------------------------------------"));
        }
        return true;
    }

}
