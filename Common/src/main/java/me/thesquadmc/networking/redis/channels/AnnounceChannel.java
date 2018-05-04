package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.abstraction.Sounds;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AnnounceChannel implements RedisChannel {

    private final Main plugin;

    public AnnounceChannel(Main plugin) {
        this.plugin = plugin;
    }

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
