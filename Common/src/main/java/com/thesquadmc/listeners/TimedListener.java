package com.thesquadmc.listeners;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.abstraction.Sounds;
import com.thesquadmc.utils.enums.UpdateType;
import com.thesquadmc.utils.handlers.UpdateEvent;
import com.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TimedListener implements Listener {

    private final NetworkTools networkTools;

    public TimedListener(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getUpdateType() == UpdateType.MIN) {
            if (Bukkit.getServerName().toUpperCase().contains("HUB")
                    || Bukkit.getServerName().toUpperCase().contains("SKYBLOCK")
                    || Bukkit.getServerName().toUpperCase().contains("FACTIONS")
                    || Bukkit.getServerName().toUpperCase().contains("PRISON")
                    || Bukkit.getServerName().toUpperCase().contains("TROLLWARS")
                    || Bukkit.getServerName().toUpperCase().contains("CREATIVE")) {
                networkTools.setRestartTime(networkTools.getRestartTime() + 1);
                if (networkTools.getRestartTime() == 720) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.kickPlayer(CC.translate("&e&lRESTART &6■ &7Daily server restart"));
                    }
                    Bukkit.shutdown();
                } else if (networkTools.getRestartTime() == 715) {
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(CC.translate("&e&lRESTART &6■ &7Daily server restart in &e5 &7min"));
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(" ");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sounds.NOTE_PLING.bukkitSound(), 1.0f, 1.0f);
                    }
                } else if (networkTools.getRestartTime() == 719) {
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
