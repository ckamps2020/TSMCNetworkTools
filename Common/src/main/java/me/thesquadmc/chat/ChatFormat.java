package me.thesquadmc.chat;

import me.clip.placeholderapi.PlaceholderAPI;
import me.thesquadmc.fanciful.FancyMessage;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;
import java.util.stream.Collectors;

public class ChatFormat {

    /**
     * The priority of this format
     */
    private final int priority;

    /**
     * The key for this format
     */
    private final String key;

    /**
     * The prefix for this format
     */
    private final String prefix;

    /**
     * The suffix for this format
     */
    private final String suffix;

    /**
     * The chat color the player's message
     */
    private final String chatColor;

    /**
     * The command that is suggested when a
     * player clicks a name
     */
    private final String nameSuggestCommand;

    /**
     * The message that is displayed when
     * a player hovers over a name
     */
    private final List<String> nameToolTip;

    public ChatFormat(int priority, String key, String prefix, String suffix, String chatColor, String nameSuggestCommand, List<String> nameToolTip) {
        this.priority = priority;
        this.key = key;

        this.prefix = CC.translate(prefix);
        this.suffix = CC.translate(suffix);
        this.chatColor = CC.translate(chatColor);
        this.nameSuggestCommand = CC.translate(nameSuggestCommand);
        this.nameToolTip = nameToolTip.stream().map(s -> {
            if (s.equals(" ")) {
                return s;
            }

            return CC.translate(s.trim());
        }).collect(Collectors.toList());

        if (Bukkit.getPluginManager().getPermission("chatformat." + key) == null) {
            try {
                Bukkit.getPluginManager().addPermission(new Permission("chatformat." + key, PermissionDefault.FALSE));
            } catch (Exception ignored) {
            }
        }
    }

    public String getKey() {
        return key;
    }

    public int getPriority() {
        return priority;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getChatColor() {
        return chatColor;
    }

    public String getNameSuggestCommand() {
        return nameSuggestCommand;
    }

    public List<String> getNameToolTip() {
        return nameToolTip;
    }

    public FancyMessage toFancyMessage(Player sender, String message) {
        FancyMessage fancyMessage = new FancyMessage();

        String name = prefix.replace("%display_name%", sender.getName());
        String suggest = PlaceholderAPI.setPlaceholders(sender, nameSuggestCommand);
        List<String> tooltip = PlaceholderAPI.setPlaceholders(sender, nameToolTip);

        fancyMessage
                .text(PlaceholderAPI.setPlaceholders(sender, name))
                .suggest(suggest)
                .tooltip(tooltip);

        fancyMessage.then();

        fancyMessage.text(PlaceholderAPI.setPlaceholders(sender, suffix))
                .suggest(suggest)
                .tooltip(tooltip);

        fancyMessage.then();
        fancyMessage.text(chatColor + message);

        return fancyMessage;
    }
}
