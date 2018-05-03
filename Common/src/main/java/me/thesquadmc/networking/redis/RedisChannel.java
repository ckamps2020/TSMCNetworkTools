package me.thesquadmc.networking.redis;

import org.json.simple.JSONObject;

public interface RedisChannel {

    void handle(String channel, JSONObject object);

}
