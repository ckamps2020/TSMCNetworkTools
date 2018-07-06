package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.objects.Party;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.json.JSONUtils;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.OfflinePlayer;

public class PartyChannel implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.PARTY_JOIN_SERVER.getName())) {
            Party party =JSONUtils.getGson().fromJson(object.get(RedisArg.PARTY.getName()).getAsJsonObject(), Party.class);
            if (party == null) {
                return;
            }

            Main.getMain().getPartyManager().addParty(party);

        } else if (channel.equals(RedisChannels.PARTY_UPDATE.getName())) {
            Party party =JSONUtils.getGson().fromJson(object.get(RedisArg.PARTY.getName()).getAsJsonObject(), Party.class);
            if (party == null) {
                return;
            }

            // Looks ugly, but it works
            PartyManager manager = Main.getMain().getPartyManager();
            if (manager.removeParty(party)) {
                manager.addParty(party);
            }

        } else if (channel.equals(RedisChannels.PARTY_DISBAND.getName())) {
            Party party =JSONUtils.getGson().fromJson(object.get(RedisArg.PARTY.getName()).getAsJsonObject(), Party.class);
            if (party == null) {
                return;
            }

            Main.getMain().getPartyManager().removeParty(party);
            for (OfflinePlayer member : party.getMembers()) {
                if (!member.isOnline()) {
                    return;
                }

                member.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &7Your &eparty &7has been &edisbanded&7!"));
            }
        }
    }
}
