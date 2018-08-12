package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RestartTimeCommand implements CommandExecutor {

    private final NetworkTools plugin;

    public RestartTimeCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int i = 720 - plugin.getRestartTime();
            player.sendMessage(CC.translate("&e&lRESTART &6■ &7Server restarts in &e" + TimeUtils.getFormattedTime(i * 60)));
        }
        return true;
    }

}
