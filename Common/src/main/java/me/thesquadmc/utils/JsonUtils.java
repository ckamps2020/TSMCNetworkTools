package me.thesquadmc.utils;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class JsonUtils {

    private JsonUtils() {}

    private static final JsonParser PARSER = new JsonParser();
    private static final Gson GSON = new Gson();

    public static JsonElement parse(String data) {
        Preconditions.checkNotNull(data);
        return PARSER.parse(data);
    }

    public static String toJson(Object source) {
        return GSON.toJson(source);
    }

    public static JsonObject parseObject(String data) {
        return parse(data).getAsJsonObject();
    }

    public static Gson getGson() {
        return GSON;
    }

    public static JsonParser getParser() {
        return PARSER;
    }
}