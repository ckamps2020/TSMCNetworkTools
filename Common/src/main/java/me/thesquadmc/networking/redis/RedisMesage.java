package me.thesquadmc.networking.redis;

import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

@SuppressWarnings("unchecked")
public class RedisMesage {

    private JSONObject message;

    private RedisMesage() {
        this.message = new JSONObject();
    }

    public static RedisMesage newMessage() {
        return new RedisMesage();
    }

    public RedisMesage set(String key, Object value){
        message.put(key, value);
        return this;
    }

    public RedisMesage set(RedisArg key, Object value){
        message.put(key.getName(), value);
        return this;
    }
}
