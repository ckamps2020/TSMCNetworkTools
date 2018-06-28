package me.thesquadmc.networking.redis;

import com.google.gson.JsonObject;
import me.thesquadmc.utils.JsonUtils;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.json.JSONUtils;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

@SuppressWarnings("unchecked")
public class RedisMesage {

    private JsonObject message;

    private RedisMesage() {
        this.message = new JsonObject();
    }

    public static RedisMesage newMessage() {
        return new RedisMesage();
    }

    public RedisMesage set(String key, Object value){
        message.add(key, JSONUtils.getGson().toJsonTree(value));
        return this;
    }

    public RedisMesage set(RedisArg key, Object value){
        set(key.getName(), value);
        return this;
    }
}
