package com.thesquadmc.networking.redis;

import com.google.gson.JsonObject;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.json.JSONUtils;

@SuppressWarnings("unchecked")
public class RedisMesage {

    private JsonObject message;

    private RedisMesage() {
        this.message = new JsonObject();
    }

    public static RedisMesage newMessage() {
        return new RedisMesage();
    }

    public RedisMesage set(String key, Object value) {
        message.add(key, JSONUtils.getGson().toJsonTree(value));
        return this;
    }

    public RedisMesage set(RedisArg key, Object value) {
        set(key.getName(), value);
        return this;
    }
}
