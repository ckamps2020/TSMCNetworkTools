package me.thesquadmc.commands;

import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class EssentialCommands {

    @Command(name = {"workbench", "wb"}, permission = "essentials.workbench", playerOnly = true)
    public void workbench(CommandArgs args) {
        Player player = args.getPlayer();

        Inventory inventory = Bukkit.createInventory(null, InventoryType.CRAFTING);
        if (args.length() > 0 && player.hasPermission("essentials.workbench.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPLAYER &6■ &7Cannot find " + args.getArg(0)));
                return;
            }

            target.openInventory(inventory);
            player.sendMessage(CC.translate("&e&lWORKBENCH &6■ &7Opened a workbench for " + target.getDisplayName()));


        } else {
            player.openInventory(inventory);
        }
    }

    @Command(name = {"top", "surface"}, permission = "essentials.top", playerOnly = true)
    public void top(CommandArgs args) {
        Player player = args.getPlayer();

        Location location;
        if (args.length() > 0 && player.hasPermission("essentials.top.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPLAYER &6■ &7Cannot find " + args.getArg(0)));
                return;
            }

            location = target.getLocation().clone();
            location.setY(target.getWorld().getHighestBlockYAt(location));

            target.teleport(location);
            player.sendMessage(CC.translate("&e&lSURFACE &6■ &7Surfaced " + target.getDisplayName()));

        } else {
            location = player.getLocation().clone();
            location.setY(player.getWorld().getHighestBlockYAt(location));

            player.teleport(location);
            player.sendMessage(CC.translate("&e&lSURFACE &6■ &7You surfaced to the top"));
        }
    }

    @Command(name = {"clearinventory", "ci"}, permission = "essentials.clearinventory", playerOnly = true)
    public void clear(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.clearinventory.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPLAYER &6■ &7Cannot find " + args.getArg(0)));
                return;
            }

            target.getInventory().clear();
            target.getInventory().setArmorContents(null);

            player.sendMessage(CC.translate("&e&lCLEAR &6■ &7Cleared " + target.getDisplayName() + "'s &7inventory"));
        } else {

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.sendMessage(CC.translate("&e&lCLEAR &6■ &7You cleared your inventory"));
        }
    }

    @Command(name = {"enderchest", "echest"}, permission = "essentials.enderchest", playerOnly = true)
    public void enderchest(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.enderchest.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPLAYER &6■ &7Cannot find " + args.getArg(0)));
                return;
            }

            player.openInventory(target.getEnderChest());

        } else {
            player.openInventory(player.getEnderChest());
        }
    }

    @Command(name = {"broadcast", "bc"}, permission = "essentials.broadcast", playerOnly = true)
    public void broadcast(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.translate("&e&lBROADCAST &6■ &7/broadcast <message>"));

        } else {
            String message = Arrays.toString(Arrays.copyOfRange(args.getArgs(), 0, args.length()));
            Bukkit.broadcastMessage(CC.translate(message));
        }
    }

    @Command(name = {"fly"}, permission = "essentials.fly", playerOnly = true)
    public void fly(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.fly.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPLAYER &6■ &7Cannot find " + args.getArg(0)));
                return;
            }

            toggleFlight(target);

        } else {
            toggleFlight(player);
        }
    }

    private void toggleFlight(Player player) {
        boolean flight = !player.getAllowFlight();

        player.setFallDistance(0);
        player.setFlying(flight);
        player.setAllowFlight(flight);

        String message;
        if (flight) {
            message = CC.translate("&e&lFLY &6■ &7Enabled flight");
        } else {
            message = CC.translate("&e&lFLY &6■ &7Disabled flight");
        }

        player.sendMessage(message);
    }
}
