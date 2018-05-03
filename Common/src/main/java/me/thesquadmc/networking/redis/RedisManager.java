package me.thesquadmc.networking.redis;

import co.itseternity.common.ThreadUtil;
import com.google.common.collect.Maps;
import me.thesquadmc.utils.server.Multithreading;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.time.Duration;
import java.util.Map;

public class RedisManager {

    private final String host;
    private final int port;
    private final String pass;

    private final Map<String, RedisChannel> channels = Maps.newHashMap();

    private JedisPool pool;

    public RedisManager(String host, int port, String pass) {
        this.host = host;
        this.port = port;
        this.pass = pass;

        try {
            ClassLoader previous = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

            this.pool = new JedisPool(new JedisPoolConfig(), host, port, 40 * 1000, pass);

            Thread.currentThread().setContextClassLoader(previous);
        } catch (Exception e) {
            e.printStackTrace();
        }

        subscribe();
    }

    public void subscribe() {
        Multithreading.runAsync(() -> {
            Jedis subscriber;

            try {
                subscriber = new Jedis(host, port);
                subscriber.auth(pass);
                subscriber.connect();


                subscriber.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        try {
                            JSONObject object = (JSONObject) new JSONParser().parse(message);
                            String from = (String) object.get("channel");

                            RedisChannel redisChannel = channels.get(from);
                            if (redisChannel != null) {
                                redisChannel.processMessage(channel, object);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                }, "sync", "sync-update");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void registerChannel(String channel, RedisChannel redisChannel) {
        channels.put(channel, redisChannel);
    }

    public void close() {
        pool.close();
    }

    public Jedis getResource() {
        return pool.getResource();
    }
}
