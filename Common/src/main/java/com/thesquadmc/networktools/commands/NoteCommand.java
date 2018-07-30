package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.objects.logging.Note;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.random.RandomUtils;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import me.lucko.luckperms.api.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            TSMCUser user = TSMCUser.fromPlayer(player);

            sendNotes(sender, user);

        } else {
            sender.sendMessage(CC.GRAY + "Loading...");
            plugin.getUUIDTranslator().getUUID(name, true).thenAccept(uuid -> {
                plugin.getUserDatabase().getUser(uuid).thenAccept(user -> {
                    if (user == null) {
                        sender.sendMessage(CC.RED + "Could not find " + name);
                        return;
                    }

                    sendNotes(sender, user);
                    TSMCUser.unloadUser(user, false);
                });
            });
        }
    }

    @Command(name = {"note.add", "notes.add"}, permission = "group.trainee", playerOnly = true)
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
            plugin.getUUIDTranslator().getUUID(name, true).thenAccept(target -> {
                plugin.getUserDatabase().getUser(target).thenAccept(user -> {
                    if (user == null) {
                        sender.sendMessage(CC.RED + "Could not find " + name);
                        return;
                    }

                    user.addNote(note);
                    TSMCUser.unloadUser(user, true);

                    plugin.getRedisManager().sendMessage(RedisChannels.NOTES, RedisMesage.newMessage()
                            .set("target", target)
                            .set("note", JSONUtils.getGson().toJsonTree(note)));

                    sender.sendMessage(CC.translate("&e&lNOTES &6■ &7Added note for &e{0}&7: &e{1}", user.getName(), note.getNote()));
                });
            });
        }
    }

    private void sendNotes(CommandSender player, TSMCUser user) {
        if (user.getNotes().size() == 0) {
            player.sendMessage(CC.translate("&e&lNOTES &6■ &e{0} &7 has no notes!", user.getName()));
            return;
        }

        player.sendMessage(CC.translate("&e&lNOTES &6■  " + user.getName() + "'s Notes:"));
        user.getNotes().stream()
                .sorted(Comparator.comparing(Note::getTimestamp).reversed())
                .forEachOrdered(note -> player.sendMessage(CC.translate("&8[{0} ago] &7{1}&8: &e{2}", TimeUtils.getFormattedTime(System.currentTimeMillis() - note.getTimestamp().getTime()), note.getCreatorName(), note.getNote())));
    }
}
