package com.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networking.redis.RedisChannel;
import com.thesquadmc.player.TSMCUser;
import com.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageChannel implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        UUID targetUUID = UUID.fromString(object.get("target").getAsString());

        Player target = Bukkit.getPlayer(targetUUID);
        if (target == null) {
            return;
        }

        UUID sender = UUID.fromString(object.get("sender").getAsString());
        String senderName = object.get("sender_name").getAsString();
        String message = object.get("message").getAsString();

        target.sendMessage(CC.translate("&6{0} &7■ &6Me &8» &e{1}", senderName, message));

        TSMCUser targetUser = TSMCUser.fromPlayer(target);
        targetUser.setLastMessager(sender);

        //TODO new SocialSpyEvent
    }
}
