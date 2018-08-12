package com.thesquadmc.networktools.utils.server;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ConnectionUtils {

    private static List<UUID> fetching = new ArrayList<>();

    public static void sendPlayer(Player player, String server) {
        player.sendMessage(CC.translate("&e&lTRANSPORT &6■ &7Sending you to &e" + server + "&7..."));

        NetworkTools.getInstance().getRedisManager().sendMessage(RedisChannels.TRANSPORT, RedisMesage.newMessage()
                .set(RedisArg.PLAYER, player.getName())
                .set(RedisArg.SERVER, server));
    }

    public static List<UUID> getFetching() {
        return fetching;
    }

}
