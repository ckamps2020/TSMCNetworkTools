package me.thesquadmc.chat.listener;

import com.google.common.collect.Maps;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.caching.MetaData;
import me.thesquadmc.Main;
import me.thesquadmc.chat.ChatFormat;
import me.thesquadmc.chat.ChatMessage;
import me.thesquadmc.fanciful.FancyMessage;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.msgs.FormatUtil;
import me.thesquadmc.utils.msgs.StringUtils;
import me.thesquadmc.utils.msgs.Unicode;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
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
        String message = e.getMessage();

        if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {

            //Check whether chat has been disabled
            if (plugin.getChatManager().isSilenced()) {
                player.sendMessage(FILTER_PREFIX + "The chat is currently silenced!");

                e.setCancelled(true);
                return;
            }

            //Get the last message they sent
            ChatMessage chatMessage = lastMessage.get(player.getUniqueId());
            if (chatMessage != null) {

                //Check if this is the same message as the last message
                if (chatMessage.getMessage().equalsIgnoreCase(e.getMessage())) {
                    player.sendMessage(FILTER_PREFIX + "You are not allowed to send the same message twice!");

                    e.setCancelled(true);
                    return;
                }

                //Check if there is a chat delay
                if (plugin.getChatManager().getChatDelay() > 0) {
                    long difference = (System.currentTimeMillis() - chatMessage.getTimestamp().getTime()) / 1000;


                    if (difference > plugin.getChatManager().getChatDelay()) {
                        player.sendMessage(FILTER_PREFIX + "You are sending messages too fast!");

                        e.setCancelled(true);
                        return;
                    }
                }
            }

            //Check if we should filter this message
            if (StringUtils.isFiltered(message)) {
                player.sendMessage(FILTER_PREFIX + "You are not allowed to say that!");

                e.setCancelled(true);
                return;
            }
        }

        ChatMessage chatMessage = new ChatMessage(player,
                message,
                Bukkit.getServerName(),
                ChatMessage.ChatType.PUBLIC,
                new Date(),
                null
        );


        message = FormatUtil.formatMessage(player, "essentials.chat", message);
        if (message == null) {
            player.sendMessage(CC.RED + "Something went wrong with sending your message!");

            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);

        ChatFormat format = plugin.getChatManager().getPlayerFormat(player);
        format.toFancyMessage(player, message).send(Bukkit.getOnlinePlayers()); //TODO Check if the player is ignored

        lastMessage.put(player.getUniqueId(), chatMessage);
        plugin.getChatManager().addMessage(chatMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerCommandPreprocessEvent e) {
        plugin.getChatManager().addMessage(new ChatMessage(
                e.getPlayer(),
                e.getMessage(),
                Bukkit.getServerName(),
                ChatMessage.ChatType.COMMAND,
                new Date(),
                null
        ));
    }
}
