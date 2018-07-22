package com.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networking.redis.RedisChannel;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.msgs.CC;
import com.thesquadmc.utils.server.Multithreading;
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
