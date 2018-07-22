package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FriendsChannel implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.LEAVE.getName())) {
            Multithreading.runAsync(() -> {
                String player = object.get(RedisArg.PLAYER.getName()).getAsString();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (TSMCUser.fromPlayer(p).isFriend(offlinePlayer)) {
                        p.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player + " &7has logged out!"));
                    }
                }
            });
        }
    }
}
