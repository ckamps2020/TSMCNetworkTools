package me.thesquadmc.chat;

import com.google.common.collect.Lists;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import me.thesquadmc.Main;
import me.thesquadmc.chat.listener.ChatListener;
import me.thesquadmc.utils.server.Multithreading;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.text.MessageFormat;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatManager {

    private static final int MAX_WRITE = 100;

    private final Queue<ChatMessage> chatMessages = new ConcurrentLinkedDeque<>();

    private boolean silenced = false;
    private int chatDelay = 0;

    public ChatManager(Main plugin) {
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), plugin);

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
                MongoCollection<Document> collection = plugin.getMongo().getCollection("messages");
                BulkWriteResult result = collection.bulkWrite(bulk, new BulkWriteOptions().ordered(true));

                if (result.getModifiedCount() <= 0) {
                    plugin.getLogger().severe(MessageFormat.format("Failed insert for {0} messages: {1}", bulk.size(), result));

                } else {
                    plugin.getLogger().severe(MessageFormat.format("Insert for {0} messages to the database", bulk.size()));
                }
            });

        }, 0L, 200L);
    }

    public void addMessage(ChatMessage message) {
        chatMessages.add(message);
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

