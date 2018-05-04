package me.thesquadmc.networking.redis;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import me.thesquadmc.utils.JsonUtils;
import me.thesquadmc.utils.json.JSONUtils;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;
import java.util.Map;

public class RedisPubSub extends JedisPubSub {

    private final Map<String, RedisChannel> listeners = Maps.newConcurrentMap();

    @Override
    public void onMessage(String channel, String message) {
        try {
            JsonObject object = JSONUtils.parseObject(message);

            RedisChannel redisChannel = listeners.get(channel);
            if (redisChannel != null) {
                redisChannel.handle(channel, object);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(RedisChannel listener, String... channels) {
        Arrays.stream(channels).forEach(s -> listeners.put(s, listener));

        // Send SUBSCRIBE message
        super.subscribe(channels);
    }

    @Override
    public void unsubscribe(String... channels) {
        Arrays.stream(channels).forEach(listeners::remove);

        super.unsubscribe(channels);
    }

    @Override
    public void unsubscribe() {
        listeners.clear();
        super.unsubscribe();
    }

    @Override
    public void subscribe(String... channels) {
        throw new UnsupportedOperationException("A RedisChannel instance is needed");
    }
}
