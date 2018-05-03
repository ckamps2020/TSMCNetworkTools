package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class NoteCommand {

    private final Main plugin;

    public NoteCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"note", "notes"})
    public void base(CommandArgs args) {
        CommandSender sender = args.getSender();

        if (args.length() == 0) {
            sender.sendMessage(CC.translate("&e&lNOTES &6■ &7/notes <player>"));
            return;
        }

        TSMCUser user;
        String name = args.getArg(1);

        if (Bukkit.getPlayer(name) != null) {
            user = TSMCUser.fromPlayer(Bukkit.getPlayer(name));
            sendNotes(sender, user);
        } else {
            sender.sendMessage(CC.YELLOW + "Loading...");
           // plugin.getMongo().getUUID(name).thenApply(uuid -> {
          //      plugin.getMongoDatabase().getUser(uuid).thenApply(target -> {

        //        });
         //   });
        }
    }

    private void sendNotes(CommandSender player, TSMCUser user) {
        if (user.getNotes().size() == 0) {
            player.sendMessage(CC.translate("&e&lNOTES &6■  " + user.getName() + " has no notes!"));
            return;
        }

        player.sendMessage(CC.translate("&e&lNOTES &6■  " + user.getName() + "'s Notes:"));
        user.getNotes().forEach(note -> {
            player.sendMessage(String.format(CC.YELLOW + "%s %s: %s", TimeUtils.getFormattedTime(System.currentTimeMillis() - note.getTimestamp()), note.getCreatorName(), note.getNote()));
        });
    }
}
