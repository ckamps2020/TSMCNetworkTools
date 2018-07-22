package me.thesquadmc.utils.player.uuid;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import me.thesquadmc.Main;
import me.thesquadmc.utils.json.JSONUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Credits to RedisBungee
 */
public final class UUIDTranslator {
    private static final Pattern UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    private static final Pattern MOJANGIAN_UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{32}");
    private final Main plugin;
    private final Map<String, CachedUUIDEntry> nameToUuidMap = new ConcurrentHashMap<>(128, 0.5f, 4);
    private final Map<UUID, CachedUUIDEntry> uuidToNameMap = new ConcurrentHashMap<>(128, 0.5f, 4);

    public UUIDTranslator(Main plugin) {
        this.plugin = plugin;
    }

    private void addToMaps(String name, UUID uuid) {
        // This is why I like LocalDate...

        // Cache the entry for three days.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);

        // Create the entry and populate the local maps
        CachedUUIDEntry entry = new CachedUUIDEntry(name, uuid, calendar);
        nameToUuidMap.put(name.toLowerCase(), entry);
        uuidToNameMap.put(uuid, entry);
    }

    public final UUID getUUID(String player, boolean expensiveLookups) {
        // If the player is online, give them their UUID.
        if (Bukkit.getPlayer(player) != null)
            return Bukkit.getPlayer(player).getUniqueId();

        // Check if we already have it cached locally
        CachedUUIDEntry cachedUUIDEntry = nameToUuidMap.get(player.toLowerCase());
        if (cachedUUIDEntry != null) {
            if (!cachedUUIDEntry.expired()) {
                return cachedUUIDEntry.getUuid();
            } else
                nameToUuidMap.remove(player);
        }

        // Check if it is a UUID
        if (UUID_PATTERN.matcher(player).find()) {
            return UUID.fromString(player);
        }

        // Check if it is a Mojang UUID (without the -'s)
        if (MOJANGIAN_UUID_PATTERN.matcher(player).find()) {
            // Reconstruct the UUID
            return UUIDFetcher.getUUID(player);
        }

        // Nothing local has worked, let's check in Redis
        try (Jedis jedis = plugin.getRedisManager().getResource()) {
            String stored = jedis.hget("name-cache", player.toLowerCase());
            if (stored != null) {
                // Found an entry value. Deserialize it.
                CachedUUIDEntry entry = JSONUtils.getGson().fromJson(stored, CachedUUIDEntry.class);

                // Check for expiry. If expired, delete values from Redis
                if (entry.expired()) {
                    jedis.hdel("name-cache", player.toLowerCase());
                    jedis.hdel("name-cache", entry.getUuid().toString());

                } else {
                    nameToUuidMap.put(player.toLowerCase(), entry);
                    uuidToNameMap.put(entry.getUuid(), entry);

                    return entry.getUuid(); //TODO Put this in a CompleteableFuture so that this does not need to be ran async
                }
            }

            // Not in Redis, let's check Mojang's API
            if (!expensiveLookups || !Bukkit.getServer().getOnlineMode())
                return null;

            Map<String, UUID> uuidMap1;
            try {
                uuidMap1 = new UUIDFetcher(Collections.singletonList(player)).call();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Unable to fetch UUID from Mojang for " + player, e);
                return null;
            }

            for (Map.Entry<String, UUID> entry : uuidMap1.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(player)) {
                    persistInfo(entry.getKey(), entry.getValue(), jedis);

                    return entry.getValue();
                }
            }
        } catch (JedisException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to fetch UUID for " + player, e);
        }

        return null; // Couldn't find it :(
    }

    public final String getName(UUID player, boolean expensiveLookups) {
        // If the player is online, give them their UUID.
        // Remember, local data > remote data.
        if (Bukkit.getPlayer(player) != null)
            return Bukkit.getPlayer(player).getName();

        // Check if it exists in the map
        CachedUUIDEntry cachedUUIDEntry = uuidToNameMap.get(player);
        if (cachedUUIDEntry != null) {
            if (!cachedUUIDEntry.expired())
                return cachedUUIDEntry.getName();
            else
                uuidToNameMap.remove(player);
        }

        // Okay, it wasn't locally cached. Let's try Redis.
        try (Jedis jedis = plugin.getRedisManager().getResource()) {
            String stored = jedis.hget("name-cache", player.toString());
            if (stored != null) {
                // Found an entry value. Deserialize it.
                CachedUUIDEntry entry = JSONUtils.getGson().fromJson(stored, CachedUUIDEntry.class);

                // Check for expiry:
                if (entry.expired()) {
                    jedis.hdel("name-cache", player.toString());
                    // Doesn't hurt to also remove the named entry as well.
                    // TODO: Since UUIDs are fixed, we could look up the name and see if the UUID matches.
                    jedis.hdel("name-cache", entry.getName());
                } else {
                    nameToUuidMap.put(entry.getName().toLowerCase(), entry);
                    uuidToNameMap.put(player, entry);
                    return entry.getName();
                }
            }

            if (!expensiveLookups || !Bukkit.getServer().getOnlineMode())
                return null;

            // That didn't work. Let's ask Mojang. This call may fail, because Mojang is insane.
            String name;
            try {
                List<String> nameHist = NameFetcher.nameHistoryFromUuid(player);
                name = Iterables.getLast(nameHist, null);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Unable to fetch name from Mojang for " + player, e);
                return null;
            }

            if (name != null) {
                persistInfo(name, player, jedis);
                return name;
            }

            return null;
        } catch (JedisException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to fetch name for " + player, e);
            return null;
        }
    }

    private final void persistInfo(String name, UUID uuid, Jedis jedis) {
        addToMaps(name, uuid);
        String json = JSONUtils.getGson().toJson(uuidToNameMap.get(uuid));
        jedis.hmset("name-cache", ImmutableMap.of(name.toLowerCase(), json, uuid.toString(), json));
    }

    public final void persistInfo(String name, UUID uuid, Pipeline jedis) {
        addToMaps(name, uuid);
        String json = JSONUtils.getGson().toJson(uuidToNameMap.get(uuid));
        jedis.hmset("name-cache", ImmutableMap.of(name.toLowerCase(), json, uuid.toString(), json));
    }

    private class CachedUUIDEntry {
        private final String name;
        private final UUID uuid;
        private final Calendar expiry;

        private CachedUUIDEntry(String name, UUID uuid, Calendar expiry) {
            this.name = name;
            this.uuid = uuid;
            this.expiry = expiry;
        }

        public String getName() {
            return name;
        }

        public UUID getUuid() {
            return uuid;
        }

        boolean expired() {
            return Calendar.getInstance().after(expiry);
        }
    }
}
