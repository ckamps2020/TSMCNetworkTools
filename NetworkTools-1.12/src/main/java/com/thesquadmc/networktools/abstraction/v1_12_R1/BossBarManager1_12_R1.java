package com.thesquadmc.networktools.abstraction.v1_12_R1;

import com.thesquadmc.networktools.abstraction.BossBarManager;
import com.thesquadmc.networktools.utils.math.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BossBarManager1_12_R1 implements BossBarManager {
	
	private final Map<UUID, BossBar> bars = new HashMap<>();
	
	@Override
	public void setBar(Player player, String text, float healthPercent) {
		BossBar bar = Bukkit.createBossBar(text, BarColor.PINK, BarStyle.SOLID);
		bar.setProgress(MathUtils.clamp(healthPercent, 0.0, 1.0));
		bar.addPlayer(player);
		
		this.bars.put(player.getUniqueId(), bar);
	}

	@Override
	public void removeBar(Player player) {
		BossBar bar = bars.get(player.getUniqueId());
		if (bar == null) return;
		
		bar.removeAll();
		this.bars.remove(player.getUniqueId());
	}

	@Override
	public boolean hasBar(Player player) {
		return bars.containsKey(player.getUniqueId());
	}

	@Override
	public void teleportBar(Player player) {
		return; // Literally no additional functionality for 1.12.2
	}

	@Override
	public void updateText(Player player, String text) {
		this.updateBar(player, text, -1);
	}

	@Override
	public void updateHealth(Player player, float healthPercent) {
		this.updateBar(player, null, healthPercent);
	}

	@Override
	public void updateBar(Player player, String text, float healthPercent) {
		BossBar bar = bars.get(player.getUniqueId());
		if (bar == null) return;
		
		if (text != null) bar.setTitle(text);
		if (healthPercent != -1) bar.setProgress(MathUtils.clamp(healthPercent, 0.0, 1.0));
	}

	@Override
	public Set<Player> getPlayers() {
		return bars.keySet().stream()
				.map(Bukkit::getPlayer)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}

}