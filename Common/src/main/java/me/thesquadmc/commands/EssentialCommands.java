package me.thesquadmc.commands;

import com.google.common.primitives.Ints;
import me.thesquadmc.utils.command.Command;
import me.thesquadmc.utils.command.CommandArgs;
import me.thesquadmc.utils.message.ClickableMessage;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.Unicode;
import me.thesquadmc.utils.player.ExpUtil;
import me.thesquadmc.utils.player.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class EssentialCommands {

    @Command(name = {"workbench", "wb"}, permission = "essentials.workbench", playerOnly = true)
    public void workbench(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.workbench.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPLAYER &6■ &7Cannot find " + args.getArg(0)));
                return;
            }

            target.openWorkbench(null, true);
            player.sendMessage(CC.translate("&e&lWORKBENCH &6■ &7Opened a workbench for " + target.getDisplayName()));


        } else {
            new ClickableMessage(player, CC.YELLOW + "Click to open a workbench", null, p -> p.openWorkbench(null, true));
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
            final int topX = player.getLocation().getBlockX();
            final int topZ = player.getLocation().getBlockZ();
            final float pitch = player.getLocation().getPitch();
            final float yaw = player.getLocation().getYaw();

            final Location loc = LocationUtil.getSafeDestination(new Location(player.getWorld(), topX, player.getWorld().getMaxHeight(), topZ, yaw, pitch));
            if (loc == null) {
                player.sendMessage(CC.RED + "Unable to teleport you to the surface!");
                return;
            }


            player.teleport(loc);
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

            clearInventory(target);
            player.sendMessage(CC.translate("&e&lCLEAR &6■ &7Cleared " + target.getDisplayName() + "'s &7inventory"));

        } else {
            clearInventory(player);

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
            Bukkit.broadcastMessage(CC.B_YELLOW + "INFO " + CC.D_GRAY + Unicode.SQUARE + CC.GRAY + " " + CC.translate(String.join(" ", args.getArgs())));
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
            player.sendMessage(CC.translate("&e&lFLY &6■ &7" + (player.getAllowFlight() ? "Enabled" : "Disabled") + " flight for " + target.getName()));

        } else {
            toggleFlight(player);
        }
    }

    @Command(name = {"feed"}, permission = "essentials.feed", playerOnly = true)
    public void feed(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.feed.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPLAYER &6■ &7Cannot find " + args.getArg(0)));
                return;
            }

            target.setFoodLevel(20);
            target.setSaturation(20);

            player.sendMessage(CC.translate("&e&lFEED &6■ &7 You fed  " + target.getName()));
            target.sendMessage(CC.translate("&e&lFEED &6■ &7 " + player.getDisplayName() + " fed you"));

        } else {
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(CC.translate("&e&lFEED &6■ &7 You fed yourself"));
        }
    }

    @Command(name = {"exp", "xp"}, permission = "essentials.exp", playerOnly = true)
    public void xp(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(getXPMessage(player, player));
            return;
        }

        Player target = Bukkit.getPlayer(args.getArg(0));
        if (target == null) {
            player.sendMessage(CC.RED + args.getArg(0) + " is not online!");
            return;
        }

        player.sendMessage(getXPMessage(target, player));
    }

    @Command(name = {"exp set", "xp set"}, permission = "essentials.exp.set")
    public void setEXP(CommandArgs args) {
        CommandSender sender = args.getSender();

        if (args.length() < 2) {
            sender.sendMessage(CC.RED + "/exp set <player> <exp>");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArg(0));
        if (player == null) {
            sender.sendMessage(CC.RED + args.getArg(0) + " is not online!");
            return;
        }

        Integer exp = Ints.tryParse(args.getArg(1));
        if (exp == null) {
            sender.sendMessage(CC.RED + args.getArg(1) + " cannot be parsed as a number");
            return;
        }

        ExpUtil.resetEXP(player);
        player.giveExp(exp);

        sender.sendMessage(CC.translate(MessageFormat.format("&e&lEXP &6■ &7Set &e{0}'s &7exp to &e{1}", player.getName(), exp)));
    }

    @Command(name = {"exp give", "xp give"}, permission = "essentials.exp.give")
    public void giveEXP(CommandArgs args) {
        CommandSender sender = args.getSender();

        if (args.length() < 2) {
            sender.sendMessage(CC.RED + "/exp give <player> <exp>");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArg(0));
        if (player == null) {
            sender.sendMessage(CC.RED + args.getArg(0) + " is not online!");
            return;
        }

        Integer exp = Ints.tryParse(args.getArg(1));
        if (exp == null) {
            sender.sendMessage(CC.RED + args.getArg(1) + " cannot be parsed as a number");
            return;
        }

        int expToGive = ExpUtil.getEXP(player) + exp;

        ExpUtil.resetEXP(player);
        player.giveExp(expToGive);

        sender.sendMessage(CC.translate(MessageFormat.format("&e&lEXP &6■ &7You gave &e{0} {1} &7exp", player.getName(), exp)));
    }

    private void clearInventory(Player player) {
        new ClickableMessage(player, CC.B_YELLOW + "Are sure you want to clear your inventory", CC.GRAY + "Click to clear your inventory", p -> {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);

            p.sendMessage(CC.translate("&e&lCLEAR &6■ &7Your inventory was cleared"));
        });
    }

    private void toggleFlight(Player player) {
        boolean flight = !player.getAllowFlight();

        player.setAllowFlight(flight);
        player.setFallDistance(0);
        player.setFlying(flight);

        String message;
        if (flight) {
            message = CC.translate("&e&lFLY &6■ &7Enabled flight");
        } else {
            message = CC.translate("&e&lFLY &6■ &7Disabled flight");
        }

        player.sendMessage(message);
    }

    private String getXPMessage(Player player, Player checking) {
        boolean self = player == checking;
        return String.format(CC.translate("&e&lXP &e■ %s &7%s &e%,d &7exp (level &e%,d&7) and %s &e%,d&7 more exp to level up."),
                (self ? "You" : player.getDisplayName()),
                (self ? "have" : "has"),
                ExpUtil.getEXP(player),
                player.getLevel(),
                (self ? "need" : "needs"),
                player.getExpToLevel());
    }
}
