package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.objects.logging.Note;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.EnumUtil;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageChannel implements RedisChannel {

    private final NetworkTools plugin;

    public MessageChannel(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        switch (channel) {
            case RedisChannels.MESSAGE: {
                UUID targetUUID = UUID.fromString(object.get("target").getAsString());

                Player target = Bukkit.getPlayer(targetUUID);
                if (target == null) {
                    return;
                }

                UUID sender = UUID.fromString(object.get("sender").getAsString());
                String senderName = object.get("sender_name").getAsString();
                String message = object.get("message").getAsString();

                TSMCUser targetUser = TSMCUser.fromPlayer(target);
                RedisMesage response = RedisMesage.newMessage()
                        .set("sender", sender)
                        .set("target", targetUUID)
                        .set("target_name", targetUser.getName())
                        .set("message", message);

                if (targetUser.getIgnoredPlayers().contains(sender)) {
                    response.set("type", MessageResponse.IGNORED.name());

                } else if (!targetUser.getSetting(PlayerSetting.PRIVATE_MESSAGES)) {
                    response.set("type", MessageResponse.PM_DISABLED.name());

                } else {
                    response.set("type", MessageResponse.SUCCESSFUL.name());

                    target.sendMessage(CC.translate("&6{0} &7■ &6Me &8» &e{1}", senderName, message));
                    targetUser.setLastMessager(sender);
                    //TODO new SocialSpyEvent
                }

                plugin.getRedisManager().sendMessage(RedisChannels.MESSAGE_RESPONSE, response);
                break;
            }

            case RedisChannels.MESSAGE_RESPONSE: {
                UUID senderUUID = UUID.fromString(object.get("sender").getAsString());

                Player sender = Bukkit.getPlayer(senderUUID);
                if (sender == null) {
                    return;
                }

                UUID target = UUID.fromString(object.get("target").getAsString());
                String targetName = object.get("target_name").getAsString();
                String message = object.get("message").getAsString();
                MessageResponse response = EnumUtil.getEnum(MessageResponse.class, object.get("type").getAsString());

                if (response == null) {
                    plugin.getLogger().info("Could not parse MessageResponse!");
                    return;
                }

                switch (response) {
                    case IGNORED: {
                        sender.sendMessage(CC.translate("&c{0} has you on their ignore list!", targetName));
                        break;
                    }

                    case PM_DISABLED: {
                        sender.sendMessage(CC.translate("&c{0} has their private messages disabled!", targetName));
                        break;
                    }

                    case SUCCESSFUL: {
                        TSMCUser.fromPlayer(sender).setLastMessager(target);
                        sender.sendMessage(CC.translate("&6Me &7■ &6{0} &8» &e{1}", targetName, message));
                    }
                }


                break;
            }

            case RedisChannels.NOTES: {
                UUID targetUUID = UUID.fromString(object.get("target").getAsString());

                Player target = Bukkit.getPlayer(targetUUID);
                if (target == null) {
                    return;
                }

                Note note = JSONUtils.getGson().fromJson(object.get("note"), Note.class);
                TSMCUser.fromPlayer(target).addNote(note);
                break;
            }
        }
    }

    public enum MessageResponse {
        IGNORED, PM_DISABLED, SUCCESSFUL
    }
}
