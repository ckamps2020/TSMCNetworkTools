package com.thesquadmc.networktools.networking.redis;

import com.google.gson.JsonObject;

public interface RedisChannel {

    void handle(String channel, JsonObject object);
}
