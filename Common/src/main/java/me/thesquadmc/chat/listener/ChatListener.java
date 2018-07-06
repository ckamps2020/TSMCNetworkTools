package me.thesquadmc.chat.listener;

import com.google.common.collect.Maps;
import me.thesquadmc.Main;
import me.thesquadmc.chat.ChatMessage;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.msgs.Unicode;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private static final String FILTER_PREFIX = CC.B_YELLOW + "FILTER " + CC.D_GRAY + Unicode.SQUARE + CC.GRAY + " ";

    private final Main plugin;
    private final Map<UUID, ChatMessage> lastMessage = Maps.newHashMap();

    public ChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {

            //Check whether chat has been disabled
            if (plugin.getChatManager().isSilenced()) {
                player.sendMessage(FILTER_PREFIX + "The chat is currently silenced!");

                e.setCancelled(true);
                return;
            }

            //Get the last message they sent
            ChatMessage message = lastMessage.get(player.getUniqueId());
            if (message != null) {

                //Check if this is the same message as the last message
                if (message.getMessage().equalsIgnoreCase(e.getMessage())) {
                    player.sendMessage(FILTER_PREFIX + "You are not allowed to send the same message twice!");

                    e.setCancelled(true);
                    return;
                }

                //Check if there is a chat delay
                if (plugin.getChatManager().getChatDelay() > 0) {
                    long difference = (System.currentTimeMillis() - message.getTimestamp().getTime()) / 1000;


                    if (difference > plugin.getChatManager().getChatDelay()) {
                        player.sendMessage(FILTER_PREFIX + "You are sending messages too fast!");

                        e.setCancelled(true);
                        return;
                    }
                }
            }

            //Check if we should filter this message
            if (StringUtils.isFiltered(e.getMessage())) {
                player.sendMessage(FILTER_PREFIX + "You are not allowed to say that!");

                e.setCancelled(true);
                return;
            }

            message = new ChatMessage(player,
                    e.getMessage(),
                    Bukkit.getServerName(),
                    ChatMessage.ChatType.PUBLIC,
                    new Date(),
                    null
            );


            lastMessage.put(player.getUniqueId(), message);
            plugin.getChatManager().addMessage(message);

            for (String group : plugin.getVaultChat().getPlayerGroups(player)) {
                Bukkit.broadcastMessage(group);
            }
        }
    }
}
