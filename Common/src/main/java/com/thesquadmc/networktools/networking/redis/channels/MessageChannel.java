package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.objects.logging.Note;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageChannel implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equalsIgnoreCase(RedisChannels.MESSAGE)) {
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

        } else if (channel.equalsIgnoreCase(RedisChannels.NOTES)) {
            UUID targetUUID = UUID.fromString(object.get("target").getAsString());

            Player target = Bukkit.getPlayer(targetUUID);
            if (target == null) {
                return;
            }

            Note note = JSONUtils.getGson().fromJson(object.get("note"), Note.class);
            TSMCUser.fromPlayer(target).addNote(note);
        }
    }
}
