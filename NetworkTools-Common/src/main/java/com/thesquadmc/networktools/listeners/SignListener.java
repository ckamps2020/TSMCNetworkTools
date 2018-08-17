package com.thesquadmc.networktools.listeners;

import com.thesquadmc.networktools.utils.msgs.FormatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

    @EventHandler
    public void on(SignChangeEvent e) {
        for (int x = 0; x < e.getLines().length; x++) {
            String line = e.getLine(x);
            line = FormatUtil.formatMessage(e.getPlayer(), "essentials.sign", line);

            e.setLine(x, line);
        }
    }
}
