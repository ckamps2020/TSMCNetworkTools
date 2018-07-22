package com.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.NetworkTools;
import com.thesquadmc.managers.PartyManager;
import com.thesquadmc.networking.redis.RedisChannel;
import com.thesquadmc.objects.Party;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.json.JSONUtils;
import com.thesquadmc.utils.msgs.CC;
import org.bukkit.OfflinePlayer;

public class PartyChannel implements RedisChannel {

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.PARTY_JOIN_SERVER.getName())) {
            Party party = JSONUtils.getGson().fromJson(object.get(RedisArg.PARTY.getName()).getAsJsonObject(), Party.class);
            if (party == null) {
                return;
            }

            NetworkTools.getInstance().getPartyManager().addParty(party);

        } else if (channel.equals(RedisChannels.PARTY_UPDATE.getName())) {
            Party party = JSONUtils.getGson().fromJson(object.get(RedisArg.PARTY.getName()).getAsJsonObject(), Party.class);
            if (party == null) {
                return;
            }

            // Looks ugly, but it works
            PartyManager manager = NetworkTools.getInstance().getPartyManager();
            if (manager.removeParty(party)) {
                manager.addParty(party);
            }

        } else if (channel.equals(RedisChannels.PARTY_DISBAND.getName())) {
            Party party = JSONUtils.getGson().fromJson(object.get(RedisArg.PARTY.getName()).getAsJsonObject(), Party.class);
            if (party == null) {
                return;
            }

            NetworkTools.getInstance().getPartyManager().removeParty(party);
            for (OfflinePlayer member : party.getMembers()) {
                if (!member.isOnline()) {
                    return;
                }

                member.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &7Your &eparty &7has been &edisbanded&7!"));
            }
        }
    }
}
