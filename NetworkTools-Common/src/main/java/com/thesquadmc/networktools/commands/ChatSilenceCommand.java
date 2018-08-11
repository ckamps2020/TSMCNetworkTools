package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public final class ChatSilenceCommand implements CommandExecutor {

    private final NetworkTools plugin;

    public ChatSilenceCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                boolean toggle = !plugin.getChatManager().isSilenced();

                plugin.getChatManager().setSilenced(toggle);
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(CC.translate(MessageFormat.format("&e&lCHAT &6■ &7Chat silence has been &e{0} &7by &e{1}", (toggle ? "enabled" : "disabled"), player.getDisplayName())));
                Bukkit.broadcastMessage(" ");

            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
