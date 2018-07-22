package com.thesquadmc.utils.server;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.managers.PartyManager;
import com.thesquadmc.networking.redis.RedisMesage;
import com.thesquadmc.objects.Party;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.msgs.GameMsgs;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ConnectionUtils {

    private static List<UUID> fetching = new ArrayList<>();

    public static void sendPlayer(Player player, String server, boolean sendParty) {
        player.sendMessage(CC.translate("&e&lTRANSPORT &6■ &7Sending you to &e" + server + "&7..."));

        NetworkTools.getInstance().getRedisManager().sendMessage(RedisChannels.TRANSPORT, RedisMesage.newMessage()
                .set(RedisArg.PLAYER, player.getName())
                .set(RedisArg.SERVER, server));

        // Account for sending a party to a server
        PartyManager partyManager = NetworkTools.getInstance().getPartyManager();
        Party party = partyManager.getOwnedParty(player);
        if (sendParty && party != null) {
            party.destroy();

            for (OfflinePlayer member : party.getMembers()) {
                if (!member.isOnline()) continue;
                ConnectionUtils.sendPlayer(member.getPlayer(), server); // WOO! Recursion!
            }

            NetworkTools.getInstance().getRedisManager().sendMessage(RedisChannels.PARTY_JOIN_SERVER, RedisMesage.newMessage()
                    .set(RedisArg.PARTY, party));

            partyManager.removeParty(party); // Unregister the party from this instance of NetworkTools
        }
    }

    public static void sendPlayer(Player player, String server) {
        sendPlayer(player, server, false);
    }

    public static void fetchGameServer(Player player, String serverType) {
        if (!fetching.contains(player.getUniqueId())) {
            player.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Finding you an open " + serverType + " server..."));

            int i = 1;
            if (NetworkTools.getInstance().getPartyManager().hasParty(player.getUniqueId())) {
                i = NetworkTools.getInstance().getPartyManager().getParty(player.getUniqueId()).getMemberCount();
            }

            fetching.add(player.getUniqueId());
            NetworkTools.getInstance().getRedisManager().sendMessage(RedisChannels.REQUEST_SERVER, RedisMesage.newMessage()
                    .set(RedisArg.COUNT, i)
                    .set(RedisArg.ORIGIN_PLAYER, player.getName())
                    .set(RedisArg.ORIGIN_SERVER, Bukkit.getServerName())
                    .set(RedisArg.SERVER, serverType));

            Bukkit.getScheduler().runTaskLater(NetworkTools.getInstance(), () -> {
                if (fetching.contains(player.getUniqueId())) {
                    fetching.remove(player.getUniqueId());
                    if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                        player.sendMessage(GameMsgs.GAME_PREFIX + "Unable to find you an open server!");
                    }
                }
            }, 3);
        } else {
            player.sendMessage(CC.translate(GameMsgs.GAME_PREFIX + "Whoa slow down there before queueing again!"));
        }
    }

    public static List<UUID> getFetching() {
        return fetching;
    }

}
