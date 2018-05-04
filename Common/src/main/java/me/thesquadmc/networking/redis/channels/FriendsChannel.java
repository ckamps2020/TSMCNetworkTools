package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.networking.redis.RedisChannel;

public class FriendsChannel implements RedisChannel {

    private final Main bot;

    public FriendsChannel(Main bot) {
        this.bot = bot;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equalsIgnoreCase(RedisChannels.LEAVE.getChannelName())) {
            Multithreading.runAsync(new Runnable() {
                @Override
                public void run() {
                    String player = String.valueOf(data.get(RedisArg.PLAYER.getArg()));
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (TSMCUser.fromPlayer(p).isFriend(offlinePlayer)) {
                            p.sendMessage(CC.translate("&d&lFRIENDS &5■ &d" + player + " &7has logged out!"));
                        }
                    }
                }
            });
    }
}
