package me.thesquadmc.utils.server;

import me.thesquadmc.Main;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.objects.Party;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.GameMsgs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ConnectionUtils {

    private static List<UUID> fetching = new ArrayList<>();

    public static void sendPlayer(Player player, String server, boolean sendParty) {
        player.sendMessage(CC.translate("&e&lTRANSPORT &6■ &7Sending you to &e" + server + "&7..."));

        Main.getMain().getRedisManager().sendMessage(RedisChannels.TRANSPORT, RedisMesage.newMessage()
                .set(RedisArg.PLAYER, player.getName())
                .set(RedisArg.SERVER, server));

        // Account for sending a party to a server
        PartyManager partyManager = Main.getMain().getPartyManager();
        Party party = partyManager.getOwnedParty(player);
        if (sendParty && party != null) {
            party.destroy();

            for (OfflinePlayer member : party.getMembers()) {
                if (!member.isOnline()) continue;
                ConnectionUtils.sendPlayer(member.getPlayer(), server); // WOO! Recursion!
            }

            Main.getMain().getRedisManager().sendMessage(RedisChannels.PARTY_JOIN_SERVER, RedisMesage.newMessage()
                    .set(RedisArg.PARTY, party));

            partyManager.removeParty(party); // Unregister the party from this instance of NetworkTools
        }

        /*Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
            @Override
            public void run() {
                Multithreading.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        try (Jedis jedis = Main.getMain().getPool().getResource()) {
                            JedisTask.withName(UUID.randomUUID().toString())
                                    .withArg(RedisArg.PLAYER.getArg(), player.getName())
                                    .withArg(RedisArg.SERVER.getArg(), server)
                                    .send(RedisChannels.TRANSPORT.getChannelName(), jedis);

                            // Account for sending a party to a server
                            PartyManager partyManager = Main.getMain().getPartyManager();
                            Party party = partyManager.getOwnedParty(player);
                            if (sendParty && party != null) {
                                party.destroy();

                                for (OfflinePlayer member : party.getMembers()) {
                                    if (!member.isOnline()) continue;
                                    ConnectionUtils.sendPlayer(member.getPlayer(), server); // WOO! Recursion!
                                }

                                // Send message for party through server
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.PARTY.getArg(), party)
                                        .send(RedisChannels.PARTY_JOIN_SERVER.getChannelName(), jedis);

                                partyManager.removeParty(party); // Unregister the party from this instance of NetworkTools
                            }
                        }
                    }
                });
            }
        }); */
    }

    public static void sendPlayer(Player player, String server) {
        sendPlayer(player, server, false);
    }

    public static void fetchGameServer(Player player, String serverType) {
        if (!fetching.contains(player.getUniqueId())) {
            player.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Finding you an open " + serverType + " server..."));

            int i = 1;
            if (Main.getMain().getPartyManager().hasParty(player.getUniqueId())) {
                i = Main.getMain().getPartyManager().getParty(player.getUniqueId()).getMemberCount();
            }

            fetching.add(player.getUniqueId());
            Main.getMain().getRedisManager().sendMessage(RedisChannels.REQUEST_SERVER, RedisMesage.newMessage()
                    .set(RedisArg.COUNT, i)
                    .set(RedisArg.ORIGIN_PLAYER, player.getName())
                    .set(RedisArg.ORIGIN_SERVER, Bukkit.getServerName())
                    .set(RedisArg.SERVER, serverType));

            Bukkit.getScheduler().runTaskLater(Main.getMain(), () -> {
                if (fetching.contains(player.getUniqueId())) {
                    fetching.remove(player.getUniqueId());
                    if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                        player.sendMessage(GameMsgs.GAME_PREFIX + "Unable to find you an open server!");
                    }
                }
            }, 3);

            /*Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
                @Override
                public void run() {
                    Multithreading.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            int i = 1;
                            if (Main.getMain().getPartyManager().hasParty(player.getUniqueId())) {
                                i = Main.getMain().getPartyManager().getParty(player.getUniqueId()).getMemberCount();
                            }
                            fetching.add(player.getUniqueId());
                            try (Jedis jedis = Main.getMain().getPool().getResource()) {
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.COUNT.getArg(), String.valueOf(i))
                                        .withArg(RedisArg.ORIGIN_PLAYER.getArg(), player.getName())
                                        .withArg(RedisArg.ORIGIN_SERVER.getArg(), Bukkit.getServerName())
                                        .withArg(RedisArg.SERVER.getArg(), serverType)
                                        .send(RedisChannels.REQUEST_SERVER.getChannelName(), jedis);
                            }
                            Bukkit.getScheduler().runTaskLater(Main.getMain(), new Runnable() {
                                @Override
                                public void run() {
                                    if (fetching.contains(player.getUniqueId())) {
                                        fetching.remove(player.getUniqueId());
                                        if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                                            player.sendMessage(GameMsgs.GAME_PREFIX + "Unable to find you an open server!");
                                        }
                                    }
                                }
                            }, 3);
                        }
                    });
                }
            }); */
        } else {
            player.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Whoa slow down there before queueing again!"));
        }
    }

    public static List<UUID> getFetching() {
        return fetching;
    }

}
