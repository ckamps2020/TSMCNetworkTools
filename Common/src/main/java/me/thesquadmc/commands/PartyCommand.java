package me.thesquadmc.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import me.thesquadmc.Main;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.objects.Party;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.Unicode;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public final class PartyCommand implements CommandExecutor {

    private final Multimap<UUID, UUID> partyRequests = HashMultimap.create();
    private final Main main;

    public PartyCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            this.main.getLogger().info("Only players are permitted to use this command");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(CC.translate("&e&lPARTY &6■ &e/party <invite|accept|kick|disband|info>"));
            return true;
        }

        PartyManager partyManager = main.getPartyManager();

        if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7Who would you like to &einvite &7to your &eparty&7?"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7The player \"&e" + args[1] + "&7\" was not found! Are they &eonline&7?"));
                return true;
            }

            if (player == target) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You cannot &einvite &7yourself to your own &eparty&7!"));
                return true;
            }

            if (partyRequests.get(target.getUniqueId()).contains(player.getUniqueId())) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You have already sent an invitation to &e" + target.getName() + "&7!"));
                return true;
            }

            if (partyManager.hasParty(target)) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &e" + target.getName() + " &7is already in a party!"));
                return true;
            }

            this.partyRequests.put(target.getUniqueId(), player.getUniqueId());
            player.sendMessage(CC.translate("&e&lPARTY &6■ &7You have invited &e" + target.getName() + "&7 to your &eparty&7!"));

            target.spigot().sendMessage(buildMessageWithPreparedCommand(
                    CC.translate("&e&lPARTY &6■ &7You have been invited to &e\"" + player.getName() + "\"&7's party! Use &e/party accept \"" + player.getName() + "\"&7 to accept!"),
                    "/party accept " + player.getName()));
        } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("join")) {
            Collection<UUID> requests = partyRequests.get(player.getUniqueId());
            if (requests.isEmpty()) {
                player.spigot().sendMessage(buildMessageWithPreparedCommand(
                        CC.translate("&e&lPARTY &6■ &7You have no &einvitations &7to accept! Invite someone with &e/party invite <player>"),
                        "/party invite " + player.getName()));
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7Whose invite do you want to &eaccept&7?"));
                return true;
            }

            @SuppressWarnings("deprecation") // FFS Bukkit -,-
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (target == null || !target.isOnline()) {
                this.partyRequests.remove(player, target); // Just in case, expire the party request
                player.sendMessage(CC.translate("&e&lPARTY &6■ &e" + args[1] + " &7is not online. You cannot accept this request"));
                return true;
            }

            Party party = partyManager.getParty(target);

            // Create party if it doesn't exist
            if (party == null) {
                party = partyManager.createParty(target);
            }

            party.addMember(player);
            this.partyRequests.removeAll(player.getUniqueId());

            player.sendMessage(CC.translate("&e&lPARTY &6■ &7You have joined &e" + target.getName() + "&e's party!"));
            target.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &e" + player.getName() + " &7has joined your party!"));

            final Party p = party;

            // Update cross-server
            main.getRedisManager().sendMessage(RedisChannels.PARTY_UPDATE, RedisMesage.newMessage()
                    .set(RedisArg.PARTY, p)
                    .set(RedisArg.REASON, "JOIN"));
            /*
            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    Multithreading.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try (Jedis jedis = Main.getMain().getPool().getResource()) {
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.PARTY.getArg(), p)
                                        .withArg(RedisArg.REASON.getArg(), "JOIN")
                                        .send(RedisChannels.PARTY_UPDATE.getChannelName(), jedis);
                            }
                        }
                    });
                }
            });*/
        } else if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("remove")) {
            Party party = partyManager.getParty(player);
            if (party == null) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You do not have a &eparty &7from which to &ekick &7people!"));
                return true;
            }

            if (!party.isOwner(player)) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You cannot &ekick &7someone from a &eparty &7you do not own!"));
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7Who do you want to &ekick &7from your &eparty&7?"));
                return true;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7That &eplayer &7has never logged on before... are they in your &eparty&7?"));
                return true;
            }

            if (player == target) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You cannot &ekick &7yourself from your own &eparty&7!"));
                return true;
            }

            if (!party.isMember(target)) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7That &eplayer &7is not in your &eparty&7!"));
                return true;
            }

            party.removeMember(target);
            player.sendMessage(CC.translate("&e&lPARTY &6■ &e" + target.getName() + " &7has been kicked from your &eparty&7!"));
            if (target.isOnline())
                target.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &7You have been &ekicked &7from &e" + player.getName() + "&7's party!"));

            // Update cross-server
            main.getRedisManager().sendMessage(RedisChannels.PARTY_UPDATE, RedisMesage.newMessage()
                    .set(RedisArg.PARTY, party)
                    .set(RedisArg.REASON, "KICK"));

/*            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    Multithreading.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try (Jedis jedis = Main.getMain().getPool().getResource()) {
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.PARTY.getArg(), party)
                                        .withArg(RedisArg.REASON.getArg(), "KICK")
                                        .send(RedisChannels.PARTY_UPDATE.getChannelName(), jedis);
                            }
                        }
                    });
                }
            });
            */
        } else if (args[0].equalsIgnoreCase("leave")) {
            Party party = partyManager.getParty(player);
            if (party == null) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You do not have a &eparty &7to &eleave&7!"));
                return true;
            }

            party.removeMember(player);
            player.sendMessage(CC.translate("&e&lPARTY &6■ &7You have left your &eparty&7!"));

            // Update owner
            if (party.isOwner(player) && party.getMemberCount(false) >= 1) {
                party.setOwner(Iterables.get(party.getMembers(), 0));

                for (OfflinePlayer member : party.getMembers()) {
                    if (!member.isOnline()) continue;
                    member.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &7" + player.getName() + " has left the &eparty&7, therefore "
                            + "&e" + party.getOwner().getName() + " &7has become the new party &eleader&7!"));
                }
            }

            // Update party cross-server
            if (party.getMemberCount(true) == 0) {
                party.destroy();
                main.getRedisManager().sendMessage(RedisChannels.PARTY_DISBAND, RedisMesage.newMessage()
                        .set(RedisArg.PARTY, party));
            } else {
                main.getRedisManager().sendMessage(RedisChannels.PARTY_UPDATE, RedisMesage.newMessage()
                        .set(RedisArg.PARTY, party)
                        .set(RedisArg.REASON, "LEAVE"));
            }

/*            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    Multithreading.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try (Jedis jedis = Main.getMain().getPool().getResource()) {
                                if (party.getMemberCount(true) == 0) {
                                    party.destroy();
                                    JedisTask.withName(UUID.randomUUID().toString())
                                            .withArg(RedisArg.PARTY.getArg(), party)
                                            .send(RedisChannels.PARTY_DISBAND.getChannelName(), jedis);
                                } else {
                                    JedisTask.withName(UUID.randomUUID().toString())
                                            .withArg(RedisArg.PARTY.getArg(), party)
                                            .withArg(RedisArg.REASON.getArg(), "LEAVE")
                                            .send(RedisChannels.PARTY_UPDATE.getChannelName(), jedis);
                                }
                            }
                        }
                    });
                }
            }); */
        } else if (args[0].equalsIgnoreCase("disband") || args[0].equalsIgnoreCase("delete")) {
            Party party = partyManager.getParty(player);
            if (party == null) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You do not have a &eparty &7to &edisband&7!"));
                return true;
            }

            if (!party.isOwner(player)) {
                player.sendMessage(CC.translate("&e&lPARTY &6■ &7You cannot &edisband &7a &eparty &7you do not own!"));
                return true;
            }

            for (OfflinePlayer member : party.getMembers()) {
                if (!member.isOnline()) continue;
                member.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &7Your &eparty &7has been &edisbanded&7!"));
            }

            party.clearMembers();
            partyManager.removeParty(party);
            player.sendMessage(CC.translate("&e&lPARTY &6■ &7Your &eparty &7has been &edisbanded&7!"));

            // Update cross-server
            main.getRedisManager().sendMessage(RedisChannels.PARTY_DISBAND, RedisMesage.newMessage()
                    .set(RedisArg.PARTY, party));

            /*Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    Multithreading.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            try (Jedis jedis = Main.getMain().getPool().getResource()) {
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.PARTY.getArg(), party)
                                        .send(RedisChannels.PARTY_DISBAND.getChannelName(), jedis);
                            }
                        }
                    });
                }
            });*/
        } else if (args[0].equalsIgnoreCase("info")) {
            Party party = partyManager.getParty(player);
            if (party == null) {
                player.spigot().sendMessage(buildMessageWithPreparedCommand(
                        CC.translate("&e&lPARTY &6■ &7You do not have a &eparty&7! Make one by inviting another player with &e/party invite <player>"),
                        "/party invite player"));
                return true;
            }

            String lineBreak = CC.translate("&8&l&m" + StringUtils.repeat("-", 20));
            player.sendMessage(lineBreak);
            player.sendMessage(CC.B_GOLD + "Party Owner: " + CC.GRAY + party.getOwner().getName());
            player.sendMessage(CC.translate("&6■ &7Total members: &e" + party.getMemberCount(true)));

            for (OfflinePlayer member : party.getMembers()) {
                player.sendMessage(CC.BD_GRAY + "   " + Unicode.DOUBLE_ARROW_RIGHT + " " + (member.isOnline() ? CC.YELLOW : CC.RED) + member.getName());
            }

            player.sendMessage(lineBreak);
        } else {
            player.sendMessage(CC.translate("&e&lPARTY &6■ &e/party <invite|accept|kick|disband|info>"));
        }

        return true;
    }

    private BaseComponent[] buildMessageWithPreparedCommand(String message, String command) {
        return new ComponentBuilder(message).event(new ClickEvent(Action.SUGGEST_COMMAND, command)).create();
    }

}