package me.thesquadmc.commands;

import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.command.Completer;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public final class CreativeCommand {

    @Completer(name = "gamemode", aliases = {"gm"})
    public List<String> complete(CommandArgs args) {
        return Arrays.asList("creative", "c", "survival", "s", "spectator", "sp", "a", "adventure");
    }

    @Command(name = "gamemode", aliases = {"gm"}, permission = "group.admin", playerOnly = true)
    public void gamemode(CommandArgs args) {
        if (args.length() == 0) {
            args.getPlayer().sendMessage(CC.RED + "/gamemode <creative:survival:spectator:adventure>");
        }
    }

    @Command(name = "gamemode c", aliases = {"gamemode creative", "gm.c", "gmc"}, permission = "group.admin", playerOnly = true)
    public void creative(CommandArgs args) {
        handleArgs(args, GameMode.CREATIVE);
    }

    @Command(name = "gamemode s", aliases = {"gamemode survival", "gm.s", "gms"}, permission = "group.admin", playerOnly = true)
    public void survival(CommandArgs args) {
        handleArgs(args, GameMode.SURVIVAL);
    }

    @Command(name = "gamemode sp", aliases = {"gamemode spectator", "gm.sp", "gmsp"}, permission = "group.admin", playerOnly = true)
    public void spectator(CommandArgs args) {
        handleArgs(args, GameMode.SPECTATOR);
    }

    @Command(name = "gamemode a", aliases = {"gamemode adventure", "gm.a", "gma"}, permission = "group.admin", playerOnly = true)
    public void adventure(CommandArgs args) {
        handleArgs(args, GameMode.ADVENTURE);
    }

    private void handleArgs(CommandArgs args, GameMode gameMode) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            if (player.getGameMode() == gameMode) {
                player.sendMessage(CC.RED + "You are already in " + gameMode.name() + "!");
            } else {
                player.setGameMode(gameMode);
                player.sendMessage(CC.GREEN + "Set your gamemode to " + gameMode.name() + "!");
            }

        } else {
            Player target = Bukkit.getPlayer(args.getArg(0));

            if (target == null) {
                player.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                return;
            }

            target.setGameMode(gameMode);
            if (target != player) {
                target.sendMessage(CC.GREEN + player.getDisplayName() + " set your gamemode to " + gameMode.name() + "!");
            }

            player.sendMessage(CC.GREEN + "Set " + target.getDisplayName() + "'s gamemode to " + gameMode.name() + "!");
        }
    }
}
