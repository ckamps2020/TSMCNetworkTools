package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.command.Completer;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class GamemodeCommand {

    private static final List<String> SUBCOMMANDS = Arrays.asList("creative", "c", "survival", "s", "spectator", "sp", "a", "adventure");

    @Completer(aliases = {"gamemode", "gm"})
    public List<String> complete(CommandArgs args) {
        if (args.length() == 1) {
            String string = args.getArg(0);

            return SUBCOMMANDS.stream()
                    .filter(string::startsWith)
                    .collect(Collectors.toList());
        }

        return null;
    }

    @Command(name = {"gamemode", "gm"}, permission = "essentials.gamemode")
    public void gamemode(CommandArgs args) {
        if (args.length() == 0) {
            args.getPlayer().sendMessage(CC.RED + "/gamemode <creative:survival:spectator:adventure>");
        }
    }

    @Command(name = {"gamemode.c", "gamemode creative", "gm.c", "gmc"}, permission = "essentials.gamemode.creative")
    public void creative(CommandArgs args) {
        handleArgs(args, GameMode.CREATIVE);
    }

    @Command(name = {"gamemode.s", "gamemode survival", "gm.s", "gms"}, permission = "essentials.gamemode.survival")
    public void survival(CommandArgs args) {
        handleArgs(args, GameMode.SURVIVAL);
    }

    @Command(name = {"gamemode.sp", "gamemode spectator", "gm.sp", "gmsp"}, permission = "essentials.gamemode.spectator")
    public void spectator(CommandArgs args) {
        handleArgs(args, GameMode.SPECTATOR);
    }

    @Command(name = {"gamemode.a", "gamemode adventure", "gm.a", "gma"}, permission = "essentials.gamemode.adventure")
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
                sender.sendMessage(CC.translate("&e&lGAMEMODE &6■ &e" + args.getArg(0) + " &7is not online!"));
                return;
            }

            if (target.getGameMode() == gameMode) {
                sender.sendMessage(CC.translate("&e&lGAMEMODE &6■ &e" + target.getName() + " is already in &e" + gameMode.name()));
                return;
            }

            target.setGameMode(gameMode);
            if (target != sender) {
                target.sendMessage(CC.translate("&e&lGAMEMODE &6■ &e" + sender.getName() + " &7set your gamemode to &e" + gameMode.name()));
            }

            sender.sendMessage(CC.translate("&e&lGAMEMODE &6■ &7Set &e" + target.getDisplayName() + "'s &7gamemode to &e" + gameMode.name()));
        }
    }
}
