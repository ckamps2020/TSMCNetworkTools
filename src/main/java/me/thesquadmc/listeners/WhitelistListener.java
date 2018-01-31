package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class WhitelistListener implements Listener {

	private final Main main;

	public WhitelistListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if (e.getResult() == PlayerLoginEvent.Result.KICK_WHITELIST) {
			e.setKickMessage(main.getWhitelistMessage());
		}
	}

}
