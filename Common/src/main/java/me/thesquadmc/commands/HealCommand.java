package me.thesquadmc.commands;

import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HealCommand {

    @Command(name = {"heal"}, permission = "essentials.heal", playerOnly = true)
    public void heal(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(CC.translate("&e&lHEAL &6■ &7You have been healed to full health!"));
        }

        if (!player.hasPermission("essentials.heal.other")) {
            player.sendMessage(CC.RED + "You cannot heal other players!");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if (target == null) {
            player.sendMessage(CC.translate("&e&lHEAL &6■ &e" +  args.getArg(0) + " &7is not online!"));
            return;
        }


        target.setHealth(player.getMaxHealth());
        if (target != player) {
            target.sendMessage(CC.translate("&e&lHEAL &6■ &e" +  player.getName() + " &7healed you to full health!"));
        }

        player.sendMessage(CC.translate("&e&lHEAL &6■ &7Healed &e" + target.getDisplayName() + " to full health"));
    }

    @Command(name = {"feed"}, permission = "essentials.feed", playerOnly = true)
    public void feed(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(CC.translate("&e&lFEED &6■ &7You have been fed to full hunger!"));
            return;
        }

        if (!player.hasPermission("essentials.feed.other")) {
            player.sendMessage(CC.RED + "You cannot feed other players!");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));

        if (target == null) {
            player.sendMessage(CC.translate("&e&lFEED &6■ &e" +  args.getArg(0) + " &7is not online!"));
            return;
        }


        target.setHealth(player.getMaxHealth());
        if (target != player) {
            target.sendMessage(CC.translate("&e&lFEED &6■ &e" +  player.getName() + " &fed you to full hunger!"));
        }

        player.sendMessage(CC.translate("&e&lFEED &6■ &7Fed &e" + target.getDisplayName() + " to full hunger"));
    }

}
