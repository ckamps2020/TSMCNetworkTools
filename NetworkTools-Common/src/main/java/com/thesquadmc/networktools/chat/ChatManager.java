package com.thesquadmc.networktools.chat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.chat.listener.ChatListener;
import com.thesquadmc.networktools.utils.file.FileUtils;
import com.thesquadmc.networktools.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatManager {

    private static final int MAX_WRITE = 100;

    private final NetworkTools plugin;
    private final Queue<ChatMessage> chatMessages = new ConcurrentLinkedDeque<>();

    private final Map<Integer, ChatFormat> formats = Maps.newTreeMap();
    private YamlConfiguration chatConfig;

    private boolean silenced = false;
    private int chatDelay = 0;

    public ChatManager(NetworkTools plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(new ChatListener(plugin), plugin);

        chatConfig = FileUtils.getConfig("chat");

        int loaded = reloadFormats();
        plugin.getLogger().info(MessageFormat.format("Loaded in {0} formats", loaded));

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            List<UpdateOneModel<Document>> bulk = Lists.newArrayList();

            ChatMessage message;
            while ((message = chatMessages.poll()) != null) {
                if (bulk.size() >= MAX_WRITE) {
                    break;
                }

                Document document = message.toDocument();

                bulk.add(new UpdateOneModel<>(
                        Filters.eq("_id", message.getPlayer().getUniqueId()),
                        new Document("$addToSet", new Document(message.getType().name(), document)),
                        new UpdateOptions().upsert(true)
                ));
            }

            Multithreading.runAsync(() -> {
                if (bulk.size() > 0) {
                    MongoCollection<Document> collection = plugin.getMongoManager().getCollection("messages");
                    BulkWriteResult result = collection.bulkWrite(bulk, new BulkWriteOptions().ordered(true));

                    if (result.getModifiedCount() <= 0) {
                        plugin.getLogger().severe(MessageFormat.format("Failed insert for {0} messages: {1}", bulk.size(), result));

                    }
                }
            });

        }, 0L, 200L);
    }

    /**
     * Loads the chat formats from the config file
     *
     * @return the amount of formats loaded
     */
    public int reloadFormats() {
        chatConfig = FileUtils.getConfig("chat");
        YamlConfiguration conf = chatConfig;

        formats.clear();
        if (!conf.contains("formats") || !conf.isConfigurationSection("formats")) {
            return 0;
        }

        for (String keys : conf.getConfigurationSection("formats").getKeys(false)) {
            String path = "formats." + keys + ".";

            if (!conf.contains(path + "priority")) {
                plugin.getLogger().severe("There was no priority, skipping...");
                continue;
            }

            int priority = conf.getInt(path + "priority");

            String prefix = conf.getString(path + "prefix", "%display_name%");
            String suffix = conf.getString(path + "suffix", "");
            String chatColor = conf.getString(path + "chatColor", "&7");
            String nameClickCommand = conf.getString(path + "name_click_command", "/msg %player_name%");

            List<String> nameToolTip = conf.getStringList(path + "name_tooltip");

            ChatFormat format = formats.put(priority, new ChatFormat(
                    priority,
                    keys,
                    prefix,
                    suffix,
                    chatColor,
                    nameClickCommand,
                    nameToolTip
            ));

            if (format != null) {
                plugin.getLogger().severe(MessageFormat.format("There is a duplicate priority with {0} and {1}", format.getKey(), keys));
            }
        }

        return formats.size();
    }

    public ChatFormat getPlayerFormat(final Player player) {
        ChatFormat format = null;

        for (final ChatFormat format1 : getFormats().values()) {
            if (hasPermission(player, "chatformat." + format1.getKey())) {
                format = format1;
                break;
            }
        }

        if (format == null) {
            format = new ChatFormat(
                    Integer.MAX_VALUE,
                    "default",
                    "&7%display_name%",
                    "&7:",
                    "&7",
                    "/msg %player_name% ",
                    Lists.newArrayList()
            );
        }

        return format;
    }

    private boolean hasPermission(final Player player, final String s) {
        String group = plugin.getVaultPermissions().getPrimaryGroup(player);

        return group != null && plugin.getVaultPermissions().groupHas(player.getWorld(), group, s);
    }

    /**
     * @return Gets an unmodifiable map of all the formats loaded
     */
    public Map<Integer, ChatFormat> getFormats() {
        return Collections.unmodifiableMap(formats);
    }

    /**
     * Adds a message to the queue that will
     * later be stored into the database
     *
     * @param message the message to store
     */
    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
    }

    /**
     * @return The chat config file
     */
    public YamlConfiguration getChatConfig() {
        return chatConfig;
    }

    /**
     * @return Gets how long the chat delay is
     */
    public int getChatDelay() {
        return chatDelay;
    }

    /**
     * Sets how long the chat delay should be
     *
     * @param chatDelay the amount of time chat delay is
     */
    public void setChatDelay(int chatDelay) {
        this.chatDelay = chatDelay;
    }

    /**
     * @return Checks whether the chat is silenced
     */
    public boolean isSilenced() {
        return silenced;
    }

    /**
     * Sets whether the chat is silenced
     *
     * @param silenced Sets whether the chat is on or off
     */
    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }
}

