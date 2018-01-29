package me.thesquadmc.commands;

import org.bukkit.entity.Player;

public class UnFreezeCommand {

	private void unfreezePlayer(Player player) {
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.2f);
		player.getActivePotionEffects().clear();
	}

}
