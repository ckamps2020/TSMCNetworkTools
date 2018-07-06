package me.thesquadmc.networking.redis;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.json.JSONUtils;
import me.thesquadmc.utils.server.Multithreading;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
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

        config = new JedisPoolConfig();
        int maxConnections = 200;
        config.setMaxTotal(maxConnections);
        config.setMaxIdle(maxConnections);
        config.setMinIdle(50);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);

        this.pool = new JedisPool(config, host, port, 40 * 1000, pass);
        Thread.currentThread().setContextClassLoader(previous);

        Jedis redisSubscriber = pool.getResource();
        pubSub = new RedisPubSub();

        Multithreading.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.subscribe(pubSub, "networktools");
            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMain(), () -> System.out.println(getPoolCurrentUsage()), 0L, 20 * 60);
    }

    public void sendMessage(RedisChannels channel, RedisMesage message) {
        message.set("channel", channel.getName());

        executeJedisAsync(jedis -> jedis.publish(channel.getName(), JSONUtils.toJson(message)));
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

    public void registerChannel(RedisChannel redisChannel, RedisChannels... channels) {
        Preconditions.checkNotNull(redisChannel, "RedisChannel cannot be null!");

        String[] subscribing = Arrays.stream(channels).map(RedisChannels::getName).toArray(String[]::new);
        subscriberExecutor.submit(() -> pubSub.subscribe(redisChannel, subscribing));
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
