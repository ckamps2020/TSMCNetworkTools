package com.thesquadmc.networktools.listeners;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.Sounds;
import com.thesquadmc.networktools.utils.enums.UpdateType;
import com.thesquadmc.networktools.utils.handlers.UpdateEvent;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TimedListener implements Listener {

    private final NetworkTools plugin;

    public TimedListener(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(UpdateEvent e) {
        if (e.getUpdateType() == UpdateType.MIN) {
            if (Bukkit.getServerName().toUpperCase().contains("HUB")
                    || Bukkit.getServerName().toUpperCase().contains("SKYBLOCK")
                    || Bukkit.getServerName().toUpperCase().contains("FACTIONS")
                    || Bukkit.getServerName().toUpperCase().contains("PRISON")
                    || Bukkit.getServerName().toUpperCase().contains("TROLLWARS")
                    || Bukkit.getServerName().toUpperCase().contains("CREATIVE")) {
                plugin.setRestartTime(plugin.getRestartTime() + 1);
                if (plugin.getRestartTime() == 720) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer(CC.translate("&e&lRESTART &6■ &7Daily server restart"));
                    }
                    Bukkit.shutdown();
                } else if (plugin.getRestartTime() == 715) {
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(CC.translate("&e&lRESTART &6■ &7Daily server restart in &e5 &7min"));
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(" ");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sounds.NOTE_PLING.bukkitSound(), 1.0f, 1.0f);
                    }
                } else if (plugin.getRestartTime() == 719) {
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(CC.translate("&e&lRESTART &6■ &7Daily server restart in &e1 &7min"));
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(" ");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sounds.NOTE_PLING.bukkitSound(), 1.0f, 1.0f);
                    }
                }
            }
        }
    }

}
