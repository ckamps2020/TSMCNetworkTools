package com.thesquadmc.networktools.networking.redis;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import redis.clients.jedis.Client;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisPubSub extends JedisPubSub {

    private final Map<String, RedisChannel> listeners = new ConcurrentHashMap<>();

    private boolean connected = false;

    @Override
    public void onMessage(String channel, String message) {
        try {
            JsonObject object = JSONUtils.parseObject(message);

            RedisChannel redisChannel = listeners.get(channel);
            if (redisChannel != null) {
                redisChannel.handle(channel, object.getAsJsonObject("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribe(RedisChannel listener, String... channels) {
        Preconditions.checkState(connected, "PubSub has not been subscribed!");

        try {
            super.subscribe(channels);
            Arrays.stream(channels).forEach(s -> listeners.put(s, listener));
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unsubscribe(String... channels) {
        Preconditions.checkState(connected, "PubSub has not been subscribed!");

        Arrays.stream(channels).forEach(listeners::remove);
        super.unsubscribe(channels);
    }

    @Override
    public void unsubscribe() {
        Preconditions.checkState(connected, "PubSub has not been subscribed!");

        listeners.clear();
        super.unsubscribe();
    }

    @Override
    public void subscribe(String... channels) {
        throw new UnsupportedOperationException("A RedisChannel instance is needed");
    }

    @Override
    public void proceed(Client client, String... channels) {
        connected = true;

        super.proceed(client, channels);
    }

    public Collection<String> getListeners() {
        return listeners.keySet();
    }
}
