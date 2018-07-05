package me.thesquadmc.commands;

import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.nms.EntityUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class FireworkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            EntityUtils.launchRandomFirework(player.getLocation(), false);
            player.sendMessage(CC.translate("&e&lFIREWORK &6■ &7BOOM!"));
        }
        return true;
    }

}
