package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.kit.Kit;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class KitCommand {

    private final NetworkTools plugin;

    public KitCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"kit", "kits", "viewkits"}, playerOnly = true)
    public void kit(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            Set<Kit> kits = plugin.getKitManager().getKits();

            if (kits.size() == 0) {
                player.sendMessage(CC.RED + "There are no kits!");
            } else {
                player.sendMessage(CC.RED + "/kit <name>");
                player.sendMessage(CC.translate("&cAvaiable kits for you: {0}", kits.stream()
                        .filter(kit -> player.hasPermission("essentials.kits." + kit))
                        .map(Kit::getName)
                        .collect(Collectors.joining(", "))));
            }
            return;
        }

        String name = args.getArg(0);
        Optional<Kit> optionalKit = plugin.getKitManager().getKit(name);
        if (!optionalKit.isPresent()) {
            player.sendMessage(CC.RED + name + " is not a kit!");
            return;
        }

        Kit kit = optionalKit.get();
        kit.giveKit(player);
    }

    @Command(name = {"addkit"}, playerOnly = true)
    public void addKit(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() < 2) {
            player.sendMessage(CC.RED + "/addkit <kit name> <cooldown>");
            return;
        }

        String name = args.getArg(0);
        Optional<Kit> kit = plugin.getKitManager().getKit(name);

        if (kit.isPresent()) {
            player.sendMessage(CC.RED + "There is already a kit with that name!");
            return;
        }

        long cooldown = TimeUtils.getTimeFromString(args.getArg(1));
        if (cooldown <= 0L) {
            player.sendMessage(CC.RED + "A kit must have a cooldown!");
            return;
        }

        Kit kit1 = new Kit(
                name,
                cooldown,
                Arrays.stream(player.getInventory().getContents())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
        plugin.getKitManager().addKit(kit1);

        player.sendMessage(CC.translate("&e&lKIT &6■ &7Added a kit with the name &e{0}", name));
    }

    @Command(name = "delkit", playerOnly = true)
    public void delKit(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "You have provide a kit name!");
            return;
        }

        String name = args.getArg(0);
        Optional<Kit> kit = plugin.getKitManager().getKit(name);

        if (!kit.isPresent()) {
            player.sendMessage(CC.RED + "There is no kit with that name!");
            return;
        }

        plugin.getKitManager().removeKit(kit.get());
        player.sendMessage(CC.translate("&e&lKIT &6■ &7You removed the &e{0} &7Kit!", name));
    }
}
