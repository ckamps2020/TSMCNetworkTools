package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public final class RandomTPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MOD)) {
                if (Bukkit.getServerName().toUpperCase().contains("HUB")) {
                    player.sendMessage(CC.translate("&e&lRTP &6■ &7You are not allowed to use this command here!"));
                    return true;
                }
                if (Bukkit.getOnlinePlayers().size() > 10) {
                    int random = new Random().nextInt(Bukkit.getServer().getOnlinePlayers().size());
                    Player t = (Player) Bukkit.getServer().getOnlinePlayers().toArray()[random];
                    player.teleport(t.getLocation());
                    player.sendMessage(CC.translate("&e&lRTP &6■ &7You have been randomly teleported to &e" + t.getName() + "&7"));
                } else {
                    player.sendMessage(CC.translate("&e&lRTP &6■ &7There are &enot enough &7players online to do this"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
