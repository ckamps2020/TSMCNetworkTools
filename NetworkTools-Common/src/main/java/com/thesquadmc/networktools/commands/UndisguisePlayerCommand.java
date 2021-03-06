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

import java.util.Random;

public final class UndisguisePlayerCommand implements CommandExecutor {

    private final NetworkTools networkTools;

    public UndisguisePlayerCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
                if (args.length == 1) {
                    String user = args[0];
                    if (user.equalsIgnoreCase("all")) {
                        if (!networkTools.getSig().equalsIgnoreCase("NONE")) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                Random random = new Random();
                                int n = random.nextInt(20) + 1;
                                Bukkit.getScheduler().runTaskLater(networkTools, () -> PlayerUtils.restorePlayerTextures(p), n);
                            }
                            networkTools.setSig("NONE");
                            networkTools.setValue("NONE");
                            player.sendMessage(CC.translate("&e&lDISGUISE &6■ &7You have undisguised the server!"));
                        } else {
                            player.sendMessage(CC.translate("&e&lDISGUISE &6■ &7The server is not disguised!"));
                        }
                    } else {
                        Player t = Bukkit.getPlayer(user);
                        if (t != null) {
                            player.sendMessage(CC.translate("&e&lDISGUISE &6■ &7You have undisguised &e" + t.getName()));
                            PlayerUtils.restorePlayerTextures(t);
                        } else {
                            player.sendMessage(CC.translate("&e&lDISGUISE &6■ &7That player is offline or doesnt exist!"));
                        }
                    }
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
