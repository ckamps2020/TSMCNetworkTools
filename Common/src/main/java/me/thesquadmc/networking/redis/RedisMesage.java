package me.thesquadmc.networking.redis;

import me.thesquadmc.utils.enums.RedisChannels;
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

    public static RedisMesage newMessage(RedisChannels channel) {
        return new RedisMesage(channel.getName());
    }

    public RedisMesage set(String key, Object value){
        message.put(key, value);
        return this;
    }

    public void send(Jedis jedis) {
        jedis.publish(channel, message.toJSONString());
    }
}
