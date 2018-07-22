package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.objects.logging.Note;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.random.RandomUtils;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class NoteCommand {

    private final NetworkTools plugin;

    public NoteCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"note", "notes"}, permission = "group.trainee")
    public void base(CommandArgs args) {
        CommandSender sender = args.getSender();

        if (args.length() == 0) {
            sender.sendMessage(CC.translate("&e&lNOTES &6■ &7/notes <player>"));
            return;
        }

        String name = args.getArg(0);
        if (Bukkit.getPlayer(name) != null) {
            TSMCUser user = TSMCUser.fromPlayer(Bukkit.getPlayer(name));
            sendNotes(sender, user);

        } else {
            sender.sendMessage(CC.GRAY + "Loading...");
            plugin.getUUIDTranslator().getUUID(name, true).thenAccept(uuid -> plugin.getUserDatabase().getUser(uuid).thenAccept(user -> {
                if (user == null) {
                    sender.sendMessage(CC.RED + "Could not find " + name);
                    return;
                }

                sendNotes(sender, user);
            }));
        }
    }

    @Command(name = {"note add", "notes add"}, permission = "group.trainee", playerOnly = true)
    public void add(CommandArgs args) {
        Player sender = args.getPlayer();

        if (args.length() < 2) {
            sender.sendMessage(CC.translate("&e&lNOTES &6■ &7/notes add <player> <message>"));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args.getArgs(), 1, args.length()));
        Note note = new Note(RandomUtils.generateAlphaNumeric(5), sender, message);

        String name = args.getArg(0);
        if (Bukkit.getPlayer(name) != null) {
            TSMCUser user = TSMCUser.fromPlayer(Bukkit.getPlayer(name));

            user.addNote(note);
            sender.sendMessage(CC.translate("&e&lNOTES &6■ &7Added note: &e{0}", note.getNote()));

        } else {
            sender.sendMessage(CC.YELLOW + "Loading...");

            plugin.getUUIDTranslator().getUUID(name, true).thenAccept(uuid -> {

                plugin.getUserDatabase().getUser(uuid).thenAccept(user -> {
                    if (user == null) {
                        sender.sendMessage(CC.RED + "Could not find " + name);
                        return;
                    }

                    user.addNote(note);
                    TSMCUser.unloadUser(user, true);

                    sender.sendMessage(CC.translate("&e&lNOTES &6■ &7Added note: &e{0}", note.getNote()));
                });
            });
        }
    }

    private void sendNotes(CommandSender player, TSMCUser user) {
        if (user.getNotes().size() == 0) {
            player.sendMessage(CC.translate("&e&lNOTES &6■ &e&l" + user.getName() + " has no notes!"));
            return;
        }

        player.sendMessage(CC.translate("&e&lNOTES &6■  " + user.getName() + "'s Notes:"));
        user.getNotes().forEach(note -> player.sendMessage(CC.translate("&8[{0} ago] &7{1}&8: &e{2}", TimeUtils.getFormattedTime(System.currentTimeMillis() - note.getTimestamp().getTime()), note.getCreatorName(), note.getNote())));
    }
}
