package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.managers.PartyManager;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.objects.Party;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PartyChannel implements RedisChannel {

    private final Main plugin;

    public PartyChannel(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) { 
        if (channel.equalsIgnoreCase(RedisChannels.PARTY_JOIN_SERVER.getChannelName())) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Multithreading.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        Party party = Main.getMain().getGson().fromJson(object.get(RedisArg.PARTY.getName()).geta), Party.class);
                        if (party == null) return;

                        Main.getMain().getPartyManager().addParty(party);
                    }
                });
            }
        });
    } else if (channel.equalsIgnoreCase(RedisChannels.PARTY_UPDATE.getChannelName())) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Multithreading.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        Party party = Main.getMain().getGson().fromJson(String.valueOf(data.get(RedisArg.PARTY.getArg())), Party.class);
                        if (party == null) return;

                        // Looks ugly, but it works
                        PartyManager manager = Main.getMain().getPartyManager();
                        if (manager.removeParty(party)) {
                            manager.addParty(party);
                        }
                    }
                });
            }
        });
    } else if (channel.equalsIgnoreCase(RedisChannels.PARTY_DISBAND.getChannelName())) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Multithreading.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        Party party = Main.getMain().getGson().fromJson(String.valueOf(data.get(RedisArg.PARTY.getArg())), Party.class);
                        if (party == null) return;

                        Main.getMain().getPartyManager().removeParty(party);
                        for (OfflinePlayer member : party.getMembers()) {
                            if (!member.isOnline()) return;
                            member.getPlayer().sendMessage(CC.translate("&e&lPARTY &6■ &7Your &eparty &7has been &edisbanded&7!"));
                        }
                    }
                });
            }
        });
    }
}
