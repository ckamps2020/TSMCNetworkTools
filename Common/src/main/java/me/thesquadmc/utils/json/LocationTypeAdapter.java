package me.thesquadmc.utils.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.UUID;

public class LocationTypeAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter writer, Location location) throws IOException {
        writer.beginObject();
        writer.name("world").value(location.getWorld().getUID().toString());
        writer.name("x").value(location.getBlockX());
        writer.name("y").value(location.getBlockY());
        writer.name("z").value(location.getBlockZ());
        writer.name("pitch").value((long) location.getPitch());
        writer.name("yaw").value((long) location.getYaw());
        writer.endObject();
    }

    @Override
    public Location read(JsonReader reader) throws IOException {
        Location location = new Location(null, 0, 0, 0);
        reader.beginObject();
        String fieldname = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = reader.nextName();
            }

            if ("world".equals(fieldname)) {
                token = reader.peek();
                location.setWorld(Bukkit.getWorld(UUID.fromString(reader.nextString())));
            }

            if ("x".equals(fieldname)) {
                token = reader.peek();
                location.setX(reader.nextInt());
            }

            if ("y".equals(fieldname)) {
                token = reader.peek();
                location.setY(reader.nextInt());
            }

            if ("z".equals(fieldname)) {
                token = reader.peek();
                location.setZ(reader.nextInt());
            }

            if ("yaw".equals(fieldname)) {
                token = reader.peek();
                location.setYaw(reader.nextLong());
            }

            if ("pitch".equals(fieldname)) {
                token = reader.peek();
                location.setPitch(reader.nextLong());
            }
        }

        reader.endObject();
        return location;
    }

}


