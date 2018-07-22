package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.logging.Note;
import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.random.RandomUtils;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class NoteCommand {

    private final Main plugin;

    public NoteCommand(Main plugin) {
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
            Multithreading.runAsync(() -> {
                UUID uuid = plugin.getUUIDTranslator().getUUID(name, true);

                plugin.getUserDatabase().getUser(uuid).thenApply(user -> {
                    if (user == null) {
                        sender.sendMessage(CC.RED + "Could not find " + name);
                        return false;
                    }

                    sendNotes(sender, user);
                    return true;
                });
            });
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

            Multithreading.runAsync(() -> {
                UUID uuid = plugin.getUUIDTranslator().getUUID(name, true);

                plugin.getUserDatabase().getUser(uuid).thenApply(user -> {
                    if (user == null) {
                        sender.sendMessage(CC.RED + "Could not find " + name);
                        return false;
                    }

                    user.addNote(note);
                    TSMCUser.unloadUser(user, true);

                    sender.sendMessage(CC.translate("&e&lNOTES &6■ &7Added note: &e{0}", note.getNote()));
                    return true;
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
