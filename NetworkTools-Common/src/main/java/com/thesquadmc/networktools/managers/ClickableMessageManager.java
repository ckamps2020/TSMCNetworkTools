package com.thesquadmc.networktools.managers;

import com.google.common.collect.Lists;
import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

//TODO Move this to a ChatManager type thing
public class ClickableMessageManager implements Listener {

    private final List<String> usedCommands = Lists.newArrayList();

    public ClickableMessageManager(NetworkTools plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void addUsedCommand(String command) {
        usedCommands.add(command);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerCommandPreprocessEvent e) {
        if (usedCommands.contains(e.getMessage())) {
            e.setCancelled(true);
        }
    }
}
