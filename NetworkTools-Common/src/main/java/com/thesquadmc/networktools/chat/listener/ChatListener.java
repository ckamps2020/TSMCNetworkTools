package com.thesquadmc.networktools.chat.listener;

import com.google.common.collect.Maps;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.chat.ChatFormat;
import com.thesquadmc.networktools.chat.ChatMessage;
import com.thesquadmc.networktools.chat.event.PlayerMessageEvent;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.FormatUtil;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private static final String PREFIX = CC.translate("&e&lCHAT &8■ &7 ");

    private final NetworkTools plugin;
    private final Map<UUID, ChatMessage> lastMessage = Maps.newHashMap();

    public ChatListener(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        if (e.isCancelled()) {
            return;
        }

        if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
            //Check whether chat has been disabled
            if (plugin.getChatManager().isSilenced()) {
                player.sendMessage(PREFIX + "The chat is currently silenced!");

                e.setCancelled(true);
                return;
            }

            //Get the last message they sent
            ChatMessage chatMessage = lastMessage.get(player.getUniqueId());
            if (chatMessage != null) {

                //Check if this is the same message as the last message
                if (chatMessage.getMessage().equalsIgnoreCase(e.getMessage())) {
                    player.sendMessage(PREFIX + "You are not allowed to send the same message twice!");

                    e.setCancelled(true);
                    return;
                }

                //Check if there is a chat delay
                if (plugin.getChatManager().getChatDelay() > 0) {
                    if (System.currentTimeMillis() - chatMessage.getTimestamp().getTime() < plugin.getChatManager().getChatDelay() * 1000) {
                        player.sendMessage(PREFIX + "You are sending messages too fast!");

                        e.setCancelled(true);
                        return;
                    }
                }
            }

            //Check if we should filter this message
            if (StringUtils.isFiltered(message)) {
                player.sendMessage(PREFIX + "You are not allowed to say that!");

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

        lastMessage.put(player.getUniqueId(), chatMessage);
        plugin.getChatManager().addMessage(chatMessage);

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
                .filter(p -> !PlayerUtils.isEqualOrHigherThen(p, Rank.TRAINEE) || !TSMCUser.fromPlayer(p).isIgnored(player.getUniqueId()))
                .forEach(p -> p.spigot().sendMessage(msg));
    }

    @EventHandler
    public void on(PlayerMessageEvent e) {
        String sender = e.getSender();
        String target = e.getTarget();

        String message = CC.translate("&8[&e&lSS&8] &6{0} &8■ &6{1} &8» &e{2}", sender, target, e.getMessage());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(sender)) {
                continue;
            }

            if (player.getName().equalsIgnoreCase(target)) {
                continue;
            }

            if (!PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
                continue;
            }

            if (!TSMCUser.fromPlayer(player).getSetting(PlayerSetting.SOCIALSPY)) {
                continue;
            }

            player.sendMessage(message);
        }

        /*
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.getName().equals(sender) || !player.getName().equals(target))
                .filter(player -> PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE))
                .filter(player -> TSMCUser.fromPlayer(player).getSetting(PlayerSetting.SOCIALSPY))
                .forEach(player -> player.sendMessage(message));
                */
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();

        if (e.isCancelled()) {
            return;
        }

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

    @EventHandler
    public void on(PlayerQuitEvent e) {
        lastMessage.remove(e.getPlayer().getUniqueId());
    }
}
