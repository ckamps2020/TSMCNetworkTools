package me.thesquadmc.networking.redis.channels;

import com.google.gson.JsonObject;
import me.thesquadmc.Main;
import me.thesquadmc.commands.FindCommand;
import me.thesquadmc.commands.StafflistCommand;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.networking.redis.RedisChannel;
import me.thesquadmc.networking.redis.RedisMesage;
import me.thesquadmc.objects.TSMCUser;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.server.Multithreading;
import me.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FindChannel implements RedisChannel {

    private final Main plugin;

    public FindChannel(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
        if (channel.equals(RedisChannels.FIND.getName())) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                String name = object.get(RedisArg.PLAYER.getName()).getAsString();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().equalsIgnoreCase(name)) {
                        TSMCUser user = TSMCUser.fromPlayer(p);

                        plugin.getRedisManager().sendMessage(RedisChannels.FOUND, RedisMesage.newMessage()
                                .set(RedisArg.SERVER, object.get(RedisArg.SERVER.getName()).getAsString())
                                .set(RedisArg.ORIGIN_SERVER, Bukkit.getServerName())
                                .set(RedisArg.PLAYER, name)
                                .set(RedisArg.ORIGIN_PLAYER, object.get(RedisArg.ORIGIN_PLAYER.getName()).getAsString())
                                .set(RedisArg.LOGIN, TimeUtils.getFormattedTime(System.currentTimeMillis() - user.getLoginTime())));

                        /*Multithreading.runAsync(() -> {
                            try (Jedis jedis = plugin.getPool().getResource()) {
                                JedisTask.withName(UUID.randomUUID().toString())
                                        .withArg(RedisArg.SERVER.getName(), object.get(RedisArg.SERVER.getName()).getAsString())
                                        .withArg(RedisArg.ORIGIN_SERVER.getName(), Bukkit.getServerName())
                                        .withArg(RedisArg.PLAYER.getName(), name)
                                        .withArg(RedisArg.ORIGIN_PLAYER.getName(), object.get(RedisArg.ORIGIN_PLAYER.getName()).getAsString())
                                        .withArg(RedisArg.LOGIN.getName(), TimeUtils.getFormattedTime(System.currentTimeMillis() - user.getLoginTime()))
                                        .send(RedisChannels.FOUND.getName(), jedis);
                            }
                        });*/
                    }
                }
            });

        } else if (channel.equalsIgnoreCase(RedisChannels.FOUND.getName())) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                String server = object.get(RedisArg.SERVER.getName()).getAsString();

                if (server.equals(Bukkit.getServerName())) {
                    String name = object.get(RedisArg.PLAYER.getName()).getAsString();
                    String origin = object.get(RedisArg.ORIGIN_PLAYER.getName()).getAsString();
                    String originServer = object.get(RedisArg.ORIGIN_SERVER.getName()).getAsString();
                    String time = object.get(RedisArg.LOGIN.getName()).getAsString();

                    Player p = Bukkit.getPlayer(origin);
                    if (p != null) {
                        FindCommand.getStillLooking().remove(p.getName());
                        p.sendMessage(" ");
                        p.sendMessage(CC.translate("&6&l" + name));
                        p.sendMessage(CC.translate("&8■ &7Server: &f" + originServer));
                        p.sendMessage(CC.translate("&8■ &7Online Since: &f" + time));
                    }
                }
            });

        } else if (channel.equalsIgnoreCase(RedisChannels.REQUEST_LIST.getName())) {
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

                plugin.getRedisManager().sendMessage(RedisChannels.RETURN_REQUEST_LIST, RedisMesage.newMessage()
                        .set(RedisArg.SERVER, object.get(RedisArg.SERVER.getName()).getAsString())
                        .set(RedisArg.PLAYER, object.get(RedisArg.PLAYER.getName()).getAsString())
                        .set(RedisArg.TRAINEE, listToString(trainee))
                        .set(RedisArg.HELPER, listToString(helper))
                        .set(RedisArg.MOD, listToString(mod))
                        .set(RedisArg.SRMOD, listToString(srmod))
                        .set(RedisArg.ADMIN, listToString(admin))
                        .set(RedisArg.MANAGER, listToString(manager))
                        .set(RedisArg.DEVELOPER, listToString(developer))
                        .set(RedisArg.OWNER, listToString(owner))
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

    private String listToString(List<String> list) {
        return list
                .stream()
                .collect(Collectors.joining(", "));
    }
}
