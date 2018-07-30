package me.thesquadmc.commands;

import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.command.Completer;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public final class CreativeCommand {

    @Completer(name = {"gamemode", "gm"})
    public List<String> complete(CommandArgs args) {
        if (args.length() == 1) {
            return Arrays.asList("creative", "c", "survival", "s", "spectator", "sp", "a", "adventure");
        }

        return null;
    }

    @Command(name = {"gamemode", "gm"}, permission = "group.admin")
    public void gamemode(CommandArgs args) {
        if (args.length() == 0) {
            args.getPlayer().sendMessage(CC.RED + "/gamemode <creative:survival:spectator:adventure>");
        }
    }

    @Command(name = {"gamemode c", "gamemode creative", "gm c", "gmc"}, permission = "group.admin")
    public void creative(CommandArgs args) {
        handleArgs(args, GameMode.CREATIVE);
        args.getPlayer().setDisplayName("InNOutInUK");
    }

    @Command(name = {"gamemode s", "gamemode survival", "gm s", "gms"}, permission = "group.admin")
    public void survival(CommandArgs args) {
        handleArgs(args, GameMode.SURVIVAL);
    }

    @Command(name = {"gamemode sp", "gamemode spectator", "gm sp", "gmsp"}, permission = "group.admin")
    public void spectator(CommandArgs args) {
        handleArgs(args, GameMode.SPECTATOR);
    }

    @Command(name = {"gamemode a", "gamemode adventure", "gm a", "gma"}, permission = "group.admin")
    public void adventure(CommandArgs args) {
        handleArgs(args, GameMode.ADVENTURE);
    }

    private void handleArgs(CommandArgs args, GameMode gameMode) {
        if (args.length() == 0) {
            if (!args.isPlayer()) {
                args.getSender().sendMessage("Console cannot set their own GameMode!");
                return;
            }

            Player player = args.getPlayer();

            if (player.getGameMode() == gameMode) {
                player.sendMessage(CC.translate("&e&lGAMEMODE &6■ &7You are already in &e" + gameMode.name()));
            } else {
                player.setGameMode(gameMode);
                player.sendMessage(CC.translate("&e&lGAMEMODE &6■ &7You set &eyour &7gamemode to &e" + gameMode.name()));
            }

        } else {
            CommandSender sender = args.getSender();
            Player target = Bukkit.getPlayer(args.getArg(0));

            if (target == null) {
                sender.sendMessage(CC.translate("&e&lGAMEMODE &6■ &e" +  args.getArg(0) + " &7is not online!"));
                return;
            }

            if (target.getGameMode() == gameMode) {
                sender.sendMessage(CC.translate("&e&lGAMEMODE &6■ &e" + target.getName() + " is already in &e" + gameMode.name()));
                return;
            }

            target.setGameMode(gameMode);
            if (target != sender) {
                target.sendMessage(CC.translate("&e&lGAMEMODE &6■ &e" +  sender.getName() + " &7set your gamemode to &e" + gameMode.name()));
            }

            sender.sendMessage(CC.translate("&e&lGAMEMODE &6■ &7Set &e" + target.getDisplayName() + "'s &7gamemode to &e" + gameMode.name()));
        }
    }
}
