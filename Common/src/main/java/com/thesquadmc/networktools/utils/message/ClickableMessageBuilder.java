package com.thesquadmc.networktools.utils.message;

import com.google.common.base.Preconditions;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.function.Consumer;

public class ClickableMessageBuilder {

    private static final Consumer<Player> EMPTY_CONSUMER = player -> {
    };

    private final LinkedList<ClickableMessage> messages = new LinkedList<>();
    private final Player player;
    private String message;
    private String hoverMessage;
    private Consumer<Player> onClick = EMPTY_CONSUMER;

    public ClickableMessageBuilder(Player player) {

        this.player = player;
    }

    public ClickableMessageBuilder message(String message) {
        Preconditions.checkNotNull(message);

        this.message = message;
        return this;
    }

    public ClickableMessageBuilder hoverMessage(String hoverMessage) {
        this.hoverMessage = hoverMessage == null ? "" : hoverMessage;
        return this;
    }

    public ClickableMessageBuilder onClick(Consumer<Player> onClick) {
        this.onClick = onClick == null ? EMPTY_CONSUMER : onClick;
        return this;
    }

    public ClickableMessageBuilder space() {
        messages.add(new ClickableMessage(player, " ", " ", EMPTY_CONSUMER));
        return this;
    }

    public ClickableMessageBuilder complete() {
        Preconditions.checkNotNull(player, "Player cannot be null!");
        Preconditions.checkNotNull(message, "Message cannot be empty");

        ClickableMessage clickableMessage = new ClickableMessage(
                player,
                message,
                hoverMessage,
                onClick
        );

        messages.add(clickableMessage);

        this.message = "";
        this.hoverMessage = "";
        return this;
    }

    public void send() {
        LinkedList<BaseComponent[]> baseComponents = new LinkedList<>();

        messages.forEach(clickableMessage -> {
            baseComponents.add(clickableMessage.getComponents());
            clickableMessage.register();
        });

        BaseComponent[] components = StringUtils.mergeComponents(baseComponents);
        player.spigot().sendMessage(components);
    }

}
