package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.abstraction.Sounds;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnnounceChannel implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        String server = object.get(RedisArg.SERVER.getName()).getAsString();
        if (server.equalsIgnoreCase("ALL") || Bukkit.getServerName().toUpperCase().contains(server)) {
            String msg = object.get(RedisArg.MESSAGE.getName()).getAsString();

            Bukkit.broadcastMessage(CC.translate("&7"));
            Bukkit.broadcastMessage(CC.translate("&7"));
            Bukkit.broadcastMessage(CC.translate("&8[&4&lALERT&8] &c" + msg));
            Bukkit.broadcastMessage(CC.translate("&7"));
            Bukkit.broadcastMessage(CC.translate("&7"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sounds.ENDERDRAGON_GROWL.bukkitSound(), 1.0f, 1.0f);
            }
        }
    }
}
