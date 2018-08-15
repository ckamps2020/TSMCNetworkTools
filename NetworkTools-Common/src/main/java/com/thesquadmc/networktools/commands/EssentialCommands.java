package com.thesquadmc.networktools.commands;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.message.ClickableMessage;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.Unicode;
import com.thesquadmc.networktools.utils.player.ExpUtil;
import com.thesquadmc.networktools.utils.player.LocationUtil;
import com.thesquadmc.networktools.utils.server.Mob;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EssentialCommands {

    private final NetworkTools plugin;

    public EssentialCommands(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"workbench", "wb", "craft"}, permission = "essentials.workbench", playerOnly = true)
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
            player.openWorkbench(null, true);
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

    @Command(name = {"extinguish", "ext"}, permission = "essentials.ext", playerOnly = true)
    public void ext(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && player.hasPermission("essentials.ext.others")) {
            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                player.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                return;
            }

            target.setFireTicks(0);
            player.sendMessage(CC.translate("&e&lEXT &6■ &7Extinguished &e{0}", target.getName()));

        } else {
            player.setFireTicks(0);
            player.sendMessage(CC.translate("&e&lEXT &6■ &7You extinguished yourself"));
        }
    }

    @Command(name = {"fireball"}, permission = "essentials.fireball", playerOnly = true)
    public void fireball(CommandArgs args) {
        Player player = args.getPlayer();

        Vector vector = player.getEyeLocation().getDirection().multiply(2);

        Projectile projectile = player.getWorld().spawn(player.getEyeLocation().add(vector.getX(), vector.getY(), vector.getZ()), Fireball.class);
        projectile.setShooter(player);
        projectile.setVelocity(vector);
    }

    @Command(name = {"jump"}, permission = "essentials.jump", playerOnly = true)
    public void jump(CommandArgs args) {
        Player player = args.getPlayer();

        try {
            Location location = LocationUtil.getTarget(player);
            Location playerLoc = player.getLocation();

            location.setYaw(playerLoc.getYaw());
            location.setPitch(playerLoc.getPitch());
            location.setY(location.getY() + 1);

            player.teleport(location);
        } catch (Exception e) {
            player.sendMessage(CC.RED + "Could not find a free space!");
        }
    }

    // /give <player> <item> [amount]
    // /give <item> [amount]

    @Command(name = {"i", "give"}, permission = "essentials.give")
    public void give(CommandArgs args) {
        if (args.length() == 0) {
            args.getSender().sendMessage(CC.RED + "/give <item> [amount]");
            args.getSender().sendMessage(CC.RED + "/give <player> <item> [amount]");
            return;
        }

        if (args.length() > 2 && args.getSender().hasPermission("essentials.give.others")) {
            CommandSender sender = args.getSender();

            Player target = Bukkit.getPlayer(args.getArg(0));
            if (target == null) {
                sender.sendMessage(CC.RED + args.getArg(0) + " is not online!");
                return;
            }

            if (target.getInventory().firstEmpty() == -1) {
                sender.sendMessage(CC.RED + target.getName() + "'s inventory is full!");
                return;
            }

            Integer amount = Ints.tryParse(args.getArg(2));
            if (amount == null) {
                sender.sendMessage(CC.RED + "Cannot parse " + args.getArg(2) + " as a number!");
                return;
            }

            Optional<ItemStack> itemStack = plugin.getItemManager().getItem(args.getArg(1), amount);
            if (itemStack.isPresent()) {
                target.getInventory().addItem(itemStack.get());
                sender.sendMessage(CC.translate("&e&lGIVE &6■ &7You gave {0} a {1}", target.getName(), args.getArg(1)));

            } else {
                sender.sendMessage(CC.RED + "Could not parse " + args.getArg(1) + " as an item!");
            }

        } else {
            if (!args.isPlayer()) {
                args.getSender().sendMessage(CC.RED + "You must be a player to do this!");
                return;
            }

            Player player = args.getPlayer();
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.RED + "Your inventory is full!");
                return;
            }

            Integer amount = 1;
            if (args.length() > 1) {
                amount = Ints.tryParse(args.getArg(1));
                if (amount == null) {
                    amount = 1;
                }
            }

            Optional<ItemStack> itemStack = plugin.getItemManager().getItem(args.getArg(0), amount);
            if (itemStack.isPresent()) {
                player.getInventory().addItem(itemStack.get());
                player.sendMessage(CC.translate("&e&lGIVE &6■ &7You gave yourself a {0}", args.getArg(0)));

            } else {
                player.sendMessage(CC.RED + "Could not parse " + args.getArg(1) + " as an item!");
            }

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

            player.openInventory(target.getEnderChest()); //TODO Listen for edits

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
            Bukkit.broadcastMessage(CC.translate("&e&lINFO &8{0} &e{1}", Unicode.SQUARE, String.join(" ", args.getArgs())));
        }
    }

    @Command(name = {"near"}, permission = "essentials.near")
    public void near(CommandArgs args) {
        Player player = args.getPlayer();

        player.sendMessage(CC.translate("&e&lNEAR &6■ &7Players near you: {0}", getNearestPlayers(player, 200)));
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
            player.sendMessage(CC.translate("&e&lFLY &6■ &e{0} &7flight for &e{1}", (player.getAllowFlight() ? "Enabled" : "Disabled"), target.getName()));

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

            player.sendMessage(CC.translate("&e&lFEED &6■ &7 You fed &e{0}", target.getName()));
            target.sendMessage(CC.translate("&e&lFEED &6■ &7 &e{0} &7 fed you", player.getName()));

        } else {
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(CC.translate("&e&lFEED &6■ &7 You fed yourself"));
        }
    }

    @Command(name = {"spawnmob"}, permission = "essentials.spawnmob", playerOnly = true)
    public void spawnMob(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/spawnmob <mob>");
            return;
        }

        Integer amount = 1;
        if (args.length() > 1) {
            amount = Ints.tryParse(args.getArg(1));
            if (amount == null) {
                amount = 1;
            }
        }

        Mob mob = Mob.fromName(args.getArg(0));
        if (mob == null) {
            player.sendMessage(CC.RED + args.getArg(0) + " is not a valid mob!");
            player.sendMessage(CC.RED + "Valid mobs: " + String.join(", ", Mob.getMobList()));
            return;
        }

        try {
            for (int x = 1; x <= amount; x++) {
                mob.spawn(player.getWorld(), LocationUtil.getSafeDestination(LocationUtil.getTarget(player)));
            }
        } catch (Mob.MobException e) {
            player.sendMessage(CC.RED + "Something went wrong with spawning the mobs in, do they exist?");

        } catch (Exception ignored) {
        }
    }

    @Command(name = {"walkspeed"}, permission = "essentials.speed", playerOnly = true)
    public void walkSpeed(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/walkspeed <speed>");
            return;
        }

        Integer speed = Ints.tryParse(args.getArg(0));
        if (speed == null) {
            player.sendMessage(CC.RED + "Could not parse " + args.getArg(0) + " as an integer!");
            return;
        }

        if (speed < 0) {
            player.sendMessage(CC.RED + "Speed cannot be less than 0!");
            return;
        }

        // No more than 10
        if (speed > 10) {
            speed = 10;
        }

        player.setWalkSpeed(speed.floatValue() / 10);
        player.sendMessage(CC.translate("&e&lSPEED &6■ &7Set your walk speed to {0}", speed));
    }

    @Command(name = {"flyspeed"}, permission = "essentials.speed", playerOnly = true)
    public void flySpeed(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.sendMessage(CC.RED + "/flyspeed <speed>");
            return;
        }

        Integer speed = Ints.tryParse(args.getArg(0));
        if (speed == null) {
            player.sendMessage(CC.RED + "Could not parse " + args.getArg(0) + " as an integer!");
            return;
        }

        if (speed < 0) {
            player.sendMessage(CC.RED + "Speed cannot be less than 0!");
            return;
        }

        // No more than 10
        if (speed > 10) {
            speed = 10;
        }

        player.setFlySpeed(speed.floatValue() / 10);
        player.sendMessage(CC.translate("&e&lSPEED &6■ &7Set your flight speed to {0}", speed));
    }

    @Command(name = {"repair"}, permission = "essentials.repair", playerOnly = true)
    public void repair(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() > 0 && args.getArg(0).equalsIgnoreCase("all") && player.hasPermission("essentials.repair.all")) {
            List<ItemStack> items = Lists.newArrayList();
            Collections.addAll(items, player.getInventory().getContents());
            Collections.addAll(items, player.getInventory().getArmorContents());

            for (ItemStack itemStack : items) {
                if (itemStack == null || itemStack.getType() == Material.AIR) return;

                itemStack.setDurability((short) 0);
            }

            player.sendMessage(CC.translate("&e&lREPAIR &6■ &7Repaired all your items"));

        } else {
            ItemStack held = player.getInventory().getItemInHand();
            if (held == null || held.getType() == Material.AIR) {
                player.sendMessage(CC.RED + "You must be holding an item!");
                return;
            }

            String name = formatName(held.getType());
            if (held.hasItemMeta() && held.getItemMeta().hasDisplayName()) {
                name = held.getItemMeta().getDisplayName();
            }

            if (held.getDurability() == 0) {
                player.sendMessage(name.trim() + CC.RED + " is already at full durability!");
                return;
            }

            held.setDurability((short) 0);
            player.sendMessage(CC.translate("&e&lREPAIR &6■ &7Repaired your &e{0}", name));
        }
    }

    @Command(name = {"hat"}, permission = "essentials.hat", playerOnly = true)
    public void hat(CommandArgs args) {
        Player player = args.getPlayer();

        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage(CC.RED + "You must have an item to put on your head!");
            return;
        }

        ItemStack head = player.getInventory().getHelmet();
        player.getInventory().setItemInHand(head);
        player.getInventory().setHelmet(itemStack);

        player.sendMessage(CC.translate("&e&lHAT &6■ &7You have set your hat"));
    }

    @Command(name = {"more"}, permission = "essentials.more", playerOnly = true)
    public void more(CommandArgs args) {
        Player player = args.getPlayer();

        ItemStack held = player.getInventory().getItemInHand();
        if (held == null || held.getType() == Material.AIR) {
            player.sendMessage(CC.RED + "You must be holding an item!");
            return;
        }

        held.setAmount(64);
        player.updateInventory();

        player.sendMessage(CC.translate("&e&lMORE &6■ &7Set item amount to 64!"));
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

    @Command(name = {"exp.set", "xp.set"}, permission = "essentials.exp.set")
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

        sender.sendMessage(CC.translate("&e&lEXP &6■ &7Set &e{0}'s &7exp to &e{1}", player.getName(), exp));
    }

    @Command(name = {"exp.give", "xp.give"}, permission = "essentials.exp.give")
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

    @Command(name = {"channels"}, permission = "group.trainee")
    public void channels(CommandArgs args) {
        Collection<String> channels = plugin.getRedisManager().getChannels();

        args.getSender().sendMessage(CC.translate("&e&lCHANNELS &6■ &7Channels ({0}): &e{1}", channels.size(), String.join(", ", channels)));
    }

    @Command(name = {"chatreload"}, permission = "group.manager")
    public void chatreload(CommandArgs args) {
        plugin.getChatManager().reloadFormats();
        args.getSender().sendMessage(CC.GREEN + "Reloaded chat formats");
    }

    private String getNearestPlayers(final Player player, final long radius) {
        final Location loc = player.getLocation();

        final StringBuilder output = new StringBuilder();
        final long radiusSquared = radius * radius;

        for (Entity entity : player.getWorld().getEntities()) {
            if (!(entity instanceof Player)) {
                continue;
            }

            Player p = (Player) entity;
            if (player == p || p.hasMetadata("data")) {
                continue;
            }

            final Location playerLoc = p.getLocation();
            final long delta = (long) playerLoc.distanceSquared(loc);
            if (delta < radiusSquared) {
                if (output.length() > 0) {
                    output.append(", ");
                }

                output.append(CC.YELLOW).append(p.getName()).append("§7(§4").append((long) Math.sqrt(delta)).append("m§7)");
            }
        }

        return output.length() > 1 ? output.toString() : "No one";
    }

    private String formatName(Material material) {
        String[] names = material.name().split("_");

        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append(StringUtils.capitalize(name.toLowerCase())).append(" ");
        }

        sb.trimToSize();
        return sb.toString();
    }

    private void clearInventory(Player player) {
        new ClickableMessage(player, CC.B_GREEN + "Are you sure you want to clear your inventory?", CC.GRAY + "Click to clear your inventory", p -> {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);

            p.sendMessage(CC.translate("&e&lCLEAR &6■ &7Your inventory was cleared"));
        }).send();
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
