package com.thesquadmc.networktools.listeners;

import com.thesquadmc.networktools.NetworkTools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class WhitelistListener implements Listener {

    private final NetworkTools networkTools;

    public WhitelistListener(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @EventHandler
    public void on(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            e.setKickMessage(networkTools.getWhitelistMessage());
        }
    }

}
