package com.thesquadmc.networktools.networking.redis;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.server.Multithreading;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RedisManager {

    private final ExecutorService subscriberExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Redis Subscriber").build());

    private final JedisPoolConfig config;
    private final JedisPool pool;
    private final RedisPubSub pubSub;

    public RedisManager(String host, int port, String pass) {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

        int maxConnections = 200;
        config = new JedisPoolConfig();
        config.setMaxTotal(maxConnections);
        config.setMaxIdle(maxConnections);
        config.setMinIdle(50);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);

        this.pool = new JedisPool(config, host, port, 40 * 1000, pass);
        Thread.currentThread().setContextClassLoader(previous);

        Jedis redisSubscriber = pool.getResource(); //Do not remove this line, it breaks Redis if you do!!!
        pubSub = new RedisPubSub();

        Multithreading.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.subscribe(pubSub, "networktools");
            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(NetworkTools.getInstance(), () -> System.out.println(getPoolCurrentUsage()), 0L, 20 * 60);
    }

    public void sendMessage(String channel, RedisMesage message) {
        message.set("channel", channel);

        executeJedisAsync(jedis -> jedis.publish(channel, JSONUtils.toJson(message)));
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

        subscriberExecutor.submit(() -> pubSub.subscribe(redisChannel, channels));
    }

    public Jedis getResource() {
        return pool.getResource();
    }

    private String getPoolCurrentUsage() {
        int active = pool.getNumActive();
        int idle = pool.getNumIdle();
        int total = active + idle;
        return String.format(
                "Active=%d, Idle=%d, Waiters=%d, total=%d, maxTotal=%d, minIdle=%d, maxIdle=%d, subCount=%d, subbed=%s",
                active,
                idle,
                pool.getNumWaiters(),
                total,
                config.getMaxTotal(),
                config.getMinIdle(),
                config.getMaxIdle(),
                pubSub.getSubscribedChannels(),
                pubSub.getListeners().toString()
        );
    }
}
