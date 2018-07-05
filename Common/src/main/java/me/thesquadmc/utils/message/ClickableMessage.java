package me.thesquadmc.utils.message;

import me.thesquadmc.Main;
import me.thesquadmc.utils.msgs.CC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public abstract class ClickableMessage implements Listener {

    private final Player player;
    private final String message;
    private final String hoverMessage;
    private final UUID command;

    protected ClickableMessage(Player player, String message, String hoverMessage) {
        this.player = player;
        this.message = message;
        this.hoverMessage = hoverMessage;
        this.command = UUID.randomUUID();

        Bukkit.getPluginManager().registerEvents(this, Main.getMain());

        BaseComponent[] components = TextComponent.fromLegacyText(CC.translate(message));
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command.toString());

        HoverEvent hoverEvent = null;
        if (hoverMessage != null && !hoverMessage.isEmpty()) {
            BaseComponent[] hoverText = TextComponent.fromLegacyText(CC.translate(hoverMessage));
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
        }

        for (BaseComponent component : components) {
            component.setClickEvent(clickEvent);

            if (hoverEvent != null) {
                component.setHoverEvent(hoverEvent);
            }
        }

        player.spigot().sendMessage(components);
    }

    public abstract void onClick(Player player);

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().replaceAll("/", "").equals(command.toString())) {
            e.setCancelled(true);

            Main.getMain().getClickableMessageManager().addUsedCommand(e.getMessage());

            onClick(player);
            HandlerList.unregisterAll(this);
        }
    }
}
