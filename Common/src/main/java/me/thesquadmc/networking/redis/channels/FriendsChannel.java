package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FriendsChannel implements RedisChannel {

    private final Main bot;

    public FriendsChannel(Main bot) {
        this.bot = bot;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.LEAVE.getChannelName())) {
            Multithreading.runAsync(() -> {
                String player = object.get(RedisArg.PLAYER.getArg()).getAsString();
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
