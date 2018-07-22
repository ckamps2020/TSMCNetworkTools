package com.thesquadmc.listeners;

import com.thesquadmc.NetworkTools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class WhitelistListener implements Listener {

    private final NetworkTools networkTools;

    public WhitelistListener(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
            e.setKickMessage(networkTools.getWhitelistMessage());
        }
    }

}
