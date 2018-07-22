package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.commands.StafflistCommand;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class FindChannel implements RedisChannel {

    private final NetworkTools plugin;

    public FindChannel(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equalsIgnoreCase(RedisChannels.REQUEST_LIST.getName())) { //TODO Move us to something better than this
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                ArrayList<String> trainee = new ArrayList<>();
                ArrayList<String> helper = new ArrayList<>();
                ArrayList<String> mod = new ArrayList<>();
                ArrayList<String> srmod = new ArrayList<>();
                ArrayList<String> admin = new ArrayList<>();
                ArrayList<String> manager = new ArrayList<>();
                ArrayList<String> developer = new ArrayList<>();
                ArrayList<String> owner = new ArrayList<>();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (PlayerUtils.doesRankMatch(p, Rank.TRAINEE)) {
                        trainee.add(p.getName());
                    } else if (PlayerUtils.doesRankMatch(p, Rank.HELPER)) {
                        helper.add(p.getName());
                    } else if (PlayerUtils.doesRankMatch(p, Rank.MOD)) {
                        mod.add(p.getName());
                    } else if (PlayerUtils.doesRankMatch(p, Rank.SRMOD)) {
                        srmod.add(p.getName());
                    } else if (PlayerUtils.doesRankMatch(p, Rank.ADMIN)) {
                        admin.add(p.getName());
                    } else if (PlayerUtils.doesRankMatch(p, Rank.MANAGER)) {
                        manager.add(p.getName());
                    } else if (PlayerUtils.doesRankMatch(p, Rank.DEVELOPER)) {
                        developer.add(p.getName());
                    } else if (PlayerUtils.doesRankMatch(p, Rank.OWNER)) {
                        owner.add(p.getName());
                    }
                }

                StringBuilder tSB = new StringBuilder();
                StringBuilder hSB = new StringBuilder();
                StringBuilder mSB = new StringBuilder();
                StringBuilder srSB = new StringBuilder();
                StringBuilder aSB = new StringBuilder();
                StringBuilder manSB = new StringBuilder();
                StringBuilder dSB = new StringBuilder();
                StringBuilder oSB = new StringBuilder();

                if (!trainee.isEmpty()) {
                    for (String s : trainee) {
                        tSB.append(" ").append(s);
                    }
                }
                if (!helper.isEmpty()) {
                    for (String s : helper) {
                        hSB.append(" ").append(s);
                    }
                }
                if (!mod.isEmpty()) {
                    for (String s : mod) {
                        mSB.append(" ").append(s);
                    }
                }
                if (!srmod.isEmpty()) {
                    for (String s : srmod) {
                        srSB.append(" ").append(s);
                    }
                }
                if (!admin.isEmpty()) {
                    for (String s : admin) {
                        aSB.append(" ").append(s);
                    }
                }
                if (!manager.isEmpty()) {
                    for (String s : manager) {
                        manSB.append(" ").append(s);
                    }
                }
                if (!developer.isEmpty()) {
                    for (String s : developer) {
                        dSB.append(" ").append(s);
                    }
                }
                if (!owner.isEmpty()) {
                    for (String s : owner) {
                        oSB.append(" ").append(s);
                    }
                }

                plugin.getRedisManager().sendMessage(RedisChannels.RETURN_REQUEST_LIST, RedisMesage.newMessage()
                        .set(RedisArg.SERVER, object.get(RedisArg.SERVER.getName()).getAsString())
                        .set(RedisArg.PLAYER, object.get(RedisArg.PLAYER.getName()).getAsString())
                        .set(RedisArg.TRAINEE, tSB.toString())
                        .set(RedisArg.HELPER, hSB.toString())
                        .set(RedisArg.MOD, mSB.toString())
                        .set(RedisArg.SRMOD, srSB.toString())
                        .set(RedisArg.ADMIN, aSB.toString())
                        .set(RedisArg.MANAGER, mSB.toString())
                        .set(RedisArg.DEVELOPER, dSB.toString())
                        .set(RedisArg.OWNER, oSB.toString())
                );

                /*Multithreading.runAsync(() -> {
                    try (Jedis jedis = plugin.getRedisManager().getResource()) {
                        JedisTask.withName(UUID.randomUUID().toString())
                                .withArg(RedisArg.SERVER.getName(), object.get(RedisArg.SERVER.getName()).getAsString())
                                .withArg(RedisArg.PLAYER.getName(), object.get(RedisArg.PLAYER.getName()).getAsString())
                                .withArg(RedisArg.TRAINEE.getName(), listToString(trainee))
                                .withArg(RedisArg.HELPER.getName(), listToString(helper))
                                .withArg(RedisArg.MOD.getName(), listToString(mod))
                                .withArg(RedisArg.SRMOD.getName(), listToString(srmod))
                                .withArg(RedisArg.ADMIN.getName(), listToString(admin))
                                .withArg(RedisArg.MANAGER.getName(), listToString(manager))
                                .withArg(RedisArg.DEVELOPER.getName(), listToString(developer))
                                .withArg(RedisArg.OWNER.getName(), listToString(owner))
                                .send(RedisChannels.RETURN_REQUEST_LIST.getName(), jedis);
                    }
                });*/
            });

        } else if (channel.equalsIgnoreCase(RedisChannels.RETURN_REQUEST_LIST.getName())) {
            String server = object.get(RedisArg.SERVER.getName()).getAsString();
            String name = object.get(RedisArg.PLAYER.getName()).getAsString();

            if (server.equalsIgnoreCase(Bukkit.getServerName())) {
                Map<UUID, Map<RedisArg, String>> map = StafflistCommand.getStafflist();
                for (Map.Entry<UUID, Map<RedisArg, String>> m : map.entrySet()) {
                    Player p = Bukkit.getPlayer(m.getKey());
                    if (p.getName().equalsIgnoreCase(name)) {
                        String trainee = object.get(RedisArg.TRAINEE.getName()).getAsString();
                        String helper = object.get(RedisArg.HELPER.getName()).getAsString();
                        String mod = object.get(RedisArg.MOD.getName()).getAsString();
                        String srmod = object.get(RedisArg.SRMOD.getName()).getAsString();
                        String admin = object.get(RedisArg.ADMIN.getName()).getAsString();
                        String manager = object.get(RedisArg.MANAGER.getName()).getAsString();
                        String developer = object.get(RedisArg.DEVELOPER.getName()).getAsString();
                        String owner = object.get(RedisArg.OWNER.getName()).getAsString();

                        if (m.getValue().get(RedisArg.TRAINEE) != null) {
                            m.getValue().put(RedisArg.TRAINEE, m.getValue().get(RedisArg.TRAINEE) + trainee);
                        } else {
                            m.getValue().put(RedisArg.TRAINEE, trainee);
                        }
                        if (m.getValue().get(RedisArg.HELPER) != null) {
                            m.getValue().put(RedisArg.HELPER, m.getValue().get(RedisArg.HELPER) + helper);
                        } else {
                            m.getValue().put(RedisArg.HELPER, helper);
                        }
                        if (m.getValue().get(RedisArg.MOD) != null) {
                            m.getValue().put(RedisArg.MOD, m.getValue().get(RedisArg.MOD) + mod);
                        } else {
                            m.getValue().put(RedisArg.MOD, mod);
                        }
                        if (m.getValue().get(RedisArg.SRMOD) != null) {
                            m.getValue().put(RedisArg.SRMOD, m.getValue().get(RedisArg.SRMOD) + srmod);
                        } else {
                            m.getValue().put(RedisArg.SRMOD, srmod);
                        }
                        if (m.getValue().get(RedisArg.ADMIN) != null) {
                            m.getValue().put(RedisArg.ADMIN, m.getValue().get(RedisArg.ADMIN) + admin);
                        } else {
                            m.getValue().put(RedisArg.ADMIN, admin);
                        }
                        if (m.getValue().get(RedisArg.MANAGER) != null) {
                            m.getValue().put(RedisArg.MANAGER, m.getValue().get(RedisArg.MANAGER) + manager);
                        } else {
                            m.getValue().put(RedisArg.MANAGER, manager);
                        }
                        if (m.getValue().get(RedisArg.DEVELOPER) != null) {
                            m.getValue().put(RedisArg.DEVELOPER, m.getValue().get(RedisArg.DEVELOPER) + developer);
                        } else {
                            m.getValue().put(RedisArg.DEVELOPER, developer);
                        }
                        if (m.getValue().get(RedisArg.OWNER) != null) {
                            m.getValue().put(RedisArg.OWNER, m.getValue().get(RedisArg.OWNER) + owner);
                        } else {
                            m.getValue().put(RedisArg.OWNER, owner);
                        }
                    }
                }
            }
        }
    }
}
