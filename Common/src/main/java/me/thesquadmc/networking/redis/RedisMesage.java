package me.thesquadmc.networking.redis;

import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

@SuppressWarnings("unchecked")
public class RedisMesage {

    private final String channel;
    private JSONObject message;

    private RedisMesage(String channel) {
        this.channel = channel;
        this.message = new JSONObject();

        message.put("channel", channel);
    }

    public static RedisMesage newMessage(String channel) {
        return new RedisMesage(channel);
    }

    public RedisMesage set(String key, Object value){
        message.put(key, value);
        return this;
    }

    public void send(Jedis jedis) {
        jedis.publish(channel, message.toJSONString());
    }
}
