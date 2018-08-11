package com.thesquadmc.networktools.utils.player.uuid;

import com.google.gson.reflect.TypeToken;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NameFetcher {

    private static OkHttpClient httpClient;

    public static List<String> nameHistoryFromUuid(UUID uuid) throws IOException {
        String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
        Request request = new Request.Builder().url(url).get().build();
        ResponseBody body = httpClient.newCall(request).execute().body();
        String response = body.string();
        body.close();

        Type listType = new TypeToken<List<Name>>() {
        }.getType();
        List<Name> names = JSONUtils.getGson().fromJson(response, listType);

        List<String> humanNames = new ArrayList<>();
        for (Name name : names) {
            humanNames.add(name.name);
        }
        return humanNames;
    }

    public static class Name {
        private String name;
        private long changedToAt;
    }
}