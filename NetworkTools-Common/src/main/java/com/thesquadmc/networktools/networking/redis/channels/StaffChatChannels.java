package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import com.thesquadmc.networktools.utils.msgs.Unicode;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class StaffChatChannels implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        switch (channel) {
            case RedisChannels.STAFFCHAT:
                getPlayersWithRank(Rank.TRAINEE).forEach(player -> {
                    TSMCUser user = TSMCUser.fromPlayer(player);

                    if (user.getSetting(PlayerSetting.STAFFCHAT)) {
                        String message = object.get(RedisArg.MESSAGE).getAsString();
                        String server = object.get(RedisArg.SERVER).getAsString();

                        player.spigot().sendMessage(StringUtils.getHoverMessage(message, "&7Currently on &e" + server));
                    }
                });

                break;
            case RedisChannels.ADMINCHAT:
                getPlayersWithRank(Rank.ADMIN).forEach(player -> {
                    TSMCUser user = TSMCUser.fromPlayer(player);
                    if (user.getSetting(PlayerSetting.ADMINCHAT)) {
                        String message = object.get(RedisArg.MESSAGE).getAsString();

                        player.sendMessage(CC.translate(message));
                    }
                });

                break;
            case RedisChannels.MANAGERCHAT:
                getPlayersWithRank(Rank.MANAGER).forEach(player -> {
                    TSMCUser user = TSMCUser.fromPlayer(player);
                    if (user.getSetting(PlayerSetting.MANAGERCHAT)) {
                        String message = object.get(RedisArg.MESSAGE).getAsString();

                        player.sendMessage(CC.translate(message));
                    }
                });

                break;
            case RedisChannels.SLACK_TO_STAFFCHAT:
                String server = object.get(RedisArg.SERVER).getAsString();
                String p = object.get(RedisArg.PLAYER).getAsString();
                String message = object.get(RedisArg.MESSAGE).getAsString();

                getPlayersWithRank(Rank.TRAINEE).forEach(player -> {
                    TSMCUser user = TSMCUser.fromPlayer(player);
                    if (user.getSetting(PlayerSetting.STAFFCHAT)) {
                        player.spigot().sendMessage(StringUtils.getHoverMessage("&8[&a&lSTAFFCHAT&8] &9" + p + " &8" + Unicode.DOUBLE_ARROW_RIGHT + " &a" + message, "&7Currently on &e" + server));
                    }
                });
                break;
        }
    }

    private Set<Player> getPlayersWithRank(Rank rank) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(o -> PlayerUtils.isEqualOrHigherThen(o, rank))
                .collect(Collectors.toSet());
    }
}
