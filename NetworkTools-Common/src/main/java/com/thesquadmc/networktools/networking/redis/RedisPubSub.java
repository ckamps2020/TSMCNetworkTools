package com.thesquadmc.networktools.networking.redis;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import redis.clients.jedis.Client;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisPubSub extends JedisPubSub {

    private final RedisManager redisManager;
    private boolean connected = false;

    RedisPubSub(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public void onMessage(String rChannel, String message) {
        try {
            JsonObject object = JSONUtils.parseObject(message);

            String channel = object.get("channel").getAsString();
            RedisChannel redisChannel = redisManager.getChannels().get(channel);
            if (redisChannel != null) {
                redisChannel.handle(channel, object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void subscribe(RedisChannel listener, String... channels) {
        Preconditions.checkState(connected, "PubSub has not been subscribed!");

        try {
            super.subscribe(channels);
        } catch (JedisConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unsubscribe(String... channels) {
        Preconditions.checkState(connected, "PubSub has not been subscribed!");

        super.unsubscribe(channels);
    }

    @Override
    public void unsubscribe() {
        Preconditions.checkState(connected, "PubSub has not been subscribed!");

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
}
