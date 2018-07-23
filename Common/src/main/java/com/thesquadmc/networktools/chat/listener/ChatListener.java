package com.thesquadmc.networktools.chat.listener;

import com.google.common.collect.Maps;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.chat.ChatFormat;
import com.thesquadmc.networktools.chat.ChatMessage;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.FormatUtil;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import com.thesquadmc.networktools.utils.msgs.Unicode;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private static final String FILTER_PREFIX = CC.B_YELLOW + "FILTER " + CC.D_GRAY + Unicode.SQUARE + CC.GRAY + " ";

    private final NetworkTools plugin;
    private final Map<UUID, ChatMessage> lastMessage = Maps.newHashMap();

    public ChatListener(NetworkTools plugin) {
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
                    System.out.println(System.currentTimeMillis() - chatMessage.getTimestamp().getTime());

                    if (TimeUtils.elapsed(chatMessage.getTimestamp().getTime(), plugin.getChatManager().getChatDelay() * 1000)) {
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
        TextComponent msg = format.toTextComponent(player, message);

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> !TSMCUser.fromPlayer(p).isIgnored(player.getUniqueId()))
                .forEach(p -> p.spigot().sendMessage(msg));

        lastMessage.put(player.getUniqueId(), chatMessage);
        plugin.getChatManager().addMessage(chatMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();

        if (message.equals("/")) {
            return;
        }

        plugin.getChatManager().addMessage(new ChatMessage(
                e.getPlayer(),
                message,
                Bukkit.getServerName(),
                ChatMessage.ChatType.COMMAND,
                new Date(),
                null
        ));
    }
}
