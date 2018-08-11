package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HealCommand {

    @Command(name = {"heal"}, permission = "essentials.heal", playerOnly = true)
    public void heal(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.heal.other")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lHEAL &6■ &e{0} &7is not online!", args.getArg(0)));
                return;
            }


            target.setHealth(player.getMaxHealth());
            if (target != player) {
                target.sendMessage(CC.translate("&e&lHEAL &6■ &e{0} &7healed you to full health!", player.getName()));
            }

            player.sendMessage(CC.translate("&e&lHEAL &6■ &7Healed &e{0} to full health", target.getName()));

        } else {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(CC.translate("&e&lHEAL &6■ &7You have been healed to full health!"));
        }
    }

    @Command(name = {"feed"}, permission = "essentials.feed", playerOnly = true)
    public void feed(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.feed.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));

            if (target == null) {
                player.sendMessage(CC.translate("&e&lFEED &6■ &e{0} &7is not online!", args.getArg(0)));
                return;
            }


            target.setHealth(player.getMaxHealth());
            if (target != player) {
                target.sendMessage(CC.translate("&e&lFEED &6■ &e{0} &7fed you to full hunger!", player.getName()));
            }

            player.sendMessage(CC.translate("&e&lFEED &6■ &7Fed &e{0} to full hunger", target.getName()));

        } else {
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(CC.translate("&e&lFEED &6■ &7You have been fed to full hunger!"));
        }
    }

}
