package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.objects.PlayerSetting;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.msgs.Unicode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class StaffChatChannels implements RedisChannel {

    private final Main plugin;

    public StaffChatChannels(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.STAFFCHAT.getName())) {
            getPlayersWithRank(Rank.TRAINEE).forEach(player -> {
                TSMCUser user = TSMCUser.fromPlayer(player);

                if (user.getSetting(PlayerSetting.STAFFCHAT_ENABLED)) {
                    String message = object.get(RedisArg.MESSAGE.getName()).getAsString();
                    String server = object.get(RedisArg.SERVER.getName()).getAsString();

                    player.spigot().sendMessage(StringUtils.getHoverMessage(message, "&7Currently on &e" + server));
                }
            });

        } else if (channel.equals(RedisChannels.ADMINCHAT.getName())) {
            getPlayersWithRank(Rank.ADMIN).forEach(player -> {
                TSMCUser user = TSMCUser.fromPlayer(player);
                if (user.getSetting(PlayerSetting.ADMINCHAT_ENABLED)) {
                    String message = object.get(RedisArg.MESSAGE.getName()).getAsString();

                    player.sendMessage(CC.translate(message));
                }
            });

        } else if (channel.equals(RedisChannels.MANAGERCHAT.getName())) {
            getPlayersWithRank(Rank.MANAGER).forEach(player -> {
                TSMCUser user = TSMCUser.fromPlayer(player);
                if (user.getSetting(PlayerSetting.MANAGERCHAT_ENABLED)) {
                    String message = object.get(RedisArg.MESSAGE.getName()).getAsString();

                    player.sendMessage(CC.translate(message));
                }
            });

        } else if (channel.equals(RedisChannels.DISCORD_STAFFCHAT_SERVER.getName())) {
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            String p = object.get(RedisArg.PLAYER.getName()).getAsString();
            String message = object.get(RedisArg.MESSAGE.getName()).getAsString();

            getPlayersWithRank(Rank.TRAINEE).forEach(player -> {
                TSMCUser user = TSMCUser.fromPlayer(player);
                if (user.getSetting(PlayerSetting.STAFFCHAT_ENABLED)) {
                    player.spigot().sendMessage(StringUtils.getHoverMessage("&8[&a&lSTAFFCHAT&8] &9" + p + " &8" + Unicode.DOUBLE_ARROW_RIGHT + " &a" + message, "&7Currently on &e" + server));
                }
            });
        }
    }

    private Set<Player> getPlayersWithRank(Rank rank) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(o -> PlayerUtils.isEqualOrHigherThen(o, rank))
                .collect(Collectors.toSet());
    }
}
