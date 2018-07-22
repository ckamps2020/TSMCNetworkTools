package com.thesquadmc.networktools.utils.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public final class JSONReader {

    private static String readAll(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        int cp;
        while ((cp = reader.read()) != -1) {
            builder.append((char) cp);
        }

        return builder.toString();
    }

    public static JSONObject readJsonFromUrl(String direction, String key, boolean array) throws IOException, JSONException {
        URL url = new URL(direction);
        URLConnection con = url.openConnection();
        con.setRequestProperty("X-Buycraft-Secret", key);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")))) {
            String jsonText = readAll(reader);
            return new JSONObject((array ? "{main: " : "") + jsonText + (array ? "}" : ""));
        }
    }

}
