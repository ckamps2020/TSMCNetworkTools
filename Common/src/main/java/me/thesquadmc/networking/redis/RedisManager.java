package me.thesquadmc.networking.redis;

import com.google.common.base.Preconditions;
import me.thesquadmc.Main;
import me.thesquadmc.utils.enums.RedisChannels;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RedisManager {

    private final String host;
    private final int port;
    private final String pass;

    private final JedisPoolConfig config;
    private JedisPool pool;
    private RedisPubSub pubSub;

    public RedisManager(String host, int port, String pass) {
        this.host = host;
        this.port = port;
        this.pass = pass;

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

        pubSub = new RedisPubSub();

        new Thread(this::subscribe, "Redis Subscriber Thread").run();

        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMain(), () -> System.out.println(getPoolCurrentUsage()), 0L, 20 * 60);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void subscribe() {
        long sleep = 1000;

        while (true) {
            try (Jedis jedis = getResource()) {
                jedis.subscribe(pubSub);

            } catch (JedisConnectionException e) {
                e.printStackTrace();

                Main.getMain().getLogger().severe("Redis connection dropped, attempting to connect in " + (sleep / 1000) + " secs..");
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                sleep += sleep;
            }

        }
    }

    public void close() {
        pool.close();
    }

    public void registerChannel(RedisChannel redisChannel, RedisChannels... channels) {
        Preconditions.checkNotNull(redisChannel, "RedisChannel cannot be null!");

        Arrays.stream(channels).map(RedisChannels::getName).forEach(s -> pubSub.subscribe(redisChannel, s));
    }

    public Jedis getResource() {
        return pool.getResource();
    }

    public String getPoolCurrentUsage() {
        int active = pool.getNumActive();
        int idle = pool.getNumIdle();
        int total = active + idle;
        return String.format(
                "Active=%d, Idle=%d, Waiters=%d, total=%d, maxTotal=%d, minIdle=%d, maxIdle=%d",
                active,
                idle,
                pool.getNumWaiters(),
                total,
                config.getMaxTotal(),
                config.getMinIdle(),
                config.getMaxIdle()
        );
    }
}
