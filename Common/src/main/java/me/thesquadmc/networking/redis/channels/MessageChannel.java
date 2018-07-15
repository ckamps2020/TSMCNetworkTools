package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.Unicode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageChannel implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        UUID sender = UUID.fromString(object.get("sender").getAsString());
        UUID target = UUID.fromString(object.get("target").getAsString());

        String senderName = object.get("senderName").getAsString();
        String message = object.get("senderName").getAsString();

        Player player = Bukkit.getPlayer(target);
        if (player == null) {
            return;
        }

        TSMCUser targetUser = TSMCUser.fromPlayer(player);
        targetUser.setLastMessager(sender);

        player.sendMessage(String.format("%s Me %s %s %s %s %s %s", CC.GOLD, Unicode.SQUARE, senderName, CC.D_GRAY, Unicode.DOUBLE_ARROW_RIGHT, CC.YELLOW, message));
    }
}
