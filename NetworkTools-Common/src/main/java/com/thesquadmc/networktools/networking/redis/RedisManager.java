package com.thesquadmc.networktools.networking.redis;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.server.Multithreading;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class RedisManager {


    private final JedisPoolConfig config;
    private final JedisPool pool;

    private final String host;
    private final String pass;
    private final int port;

    private final Map<String, RedisChannel> channels = new ConcurrentHashMap<>();

    public RedisManager(String host, int port, String pass) {
        this.host = host;
        this.pass = pass;
        this.port = port;

        int maxConnections = 200;
        config = new JedisPoolConfig();
        config.setMaxTotal(maxConnections);
        config.setMaxIdle(maxConnections);
        config.setMinIdle(50);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);

        this.pool = new JedisPool(config, host, port, 40 * 1000, pass);

        new Thread(this::run, "Redis Subscriber Thread").start();

        Bukkit.getScheduler().runTaskTimerAsynchronously(NetworkTools.getInstance(), () -> System.out.println(getPoolCurrentUsage()), 0L, 20 * 60 * 5);
    }

    private void run() {
        int secs = 1;
        while (true) {
            Jedis subscriber = null;
            try {
                subscriber = new Jedis(host, port);
                subscriber.auth(pass);

                secs = 1;
                subscriber.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        try {
                            JsonObject object = JSONUtils.parseObject(message).getAsJsonObject("message");

                            if (object.has("channel")) {
                                String subchannel = object.get("channel").getAsString();

                                RedisChannel rc = channels.get(subchannel);
                                if (rc != null) {
                                    rc.handle(subchannel, object);
                                }

                            } else {
                                NetworkTools.getInstance().getLogger().info("No channel: " + message);
                            }

                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }, "networktools");

            } catch (JedisConnectionException e) {
                if (subscriber != null) {
                    subscriber.close();
                }

                NetworkTools.getInstance().getLogger().severe("Lost Redis connection, going to retry in " + secs + " seconds...");
                Bukkit.broadcast(CC.B_RED + " ", "group.trainee");
                Bukkit.broadcast(CC.B_RED + " ", "group.trainee");
                Bukkit.broadcast(CC.B_RED + "Lost Redis connection, going to retry in " + secs + " seconds...", "group.trainee");
                Bukkit.broadcast(CC.B_RED + "Server: " + Bukkit.getServerName(), "group.trainee");

                Bukkit.broadcast(CC.B_RED + " ", "group.trainee");
                Bukkit.broadcast(CC.B_RED + " ", "group.trainee");
                try {
                    Thread.sleep(secs * 1000);
                } catch (InterruptedException e1) {
                    return;
                }
                secs += secs;
            }
        }
    }
    public void sendMessage(String channel, RedisMesage message) {
        message.set("channel", channel);

        executeJedisAsync(jedis -> jedis.publish("networktools", JSONUtils.toJson(message)));
    }

    public void executeJedisAsync(Consumer<Jedis> consumer) {
        Multithreading.runAsync(() -> executeJedis(consumer));
    }

    private void executeJedis(Consumer<Jedis> consumer) {
        try (Jedis jedis = pool.getResource()) {
            consumer.accept(jedis);
        }
    }

    public void close() {
        pool.close();
    }

    public void registerChannel(RedisChannel redisChannel, String... channels) {
        Preconditions.checkNotNull(redisChannel, "RedisChannel cannot be null!");

        Arrays.stream(channels).forEach(s -> this.channels.put(s, redisChannel));
    }

    public Set<String> getChannels() {
        return channels.keySet();
    }

    public Jedis getResource() {
        return pool.getResource();
    }

    private String getPoolCurrentUsage() {
        int active = pool.getNumActive();
        int idle = pool.getNumIdle();
        int total = active + idle;
        return String.format(
                "Active=%d, Idle=%d, Waiters=%d, total=%d, maxTotal=%d, minIdle=%d, maxIdle=%d, subbed=%s",
                active,
                idle,
                pool.getNumWaiters(),
                total,
                config.getMaxTotal(),
                config.getMinIdle(),
                config.getMaxIdle(),
                channels.keySet()
        );
    }
}
