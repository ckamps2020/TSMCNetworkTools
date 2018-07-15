package me.thesquadmc.listeners;

import me.thesquadmc.player.TSMCUser;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.UpdateType;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.msgs.CC;
import me.thesquadmc.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class XrayListener implements Listener {

    private Map<UUID, Integer> diamondsMined = new HashMap<>();
	private Map<UUID, Integer> spawnerMined = new HashMap<>();

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.MIN) {
			// Compile list of all xrayers
			Set<UUID> xrayers = new HashSet<>(diamondsMined.keySet());
			xrayers.addAll(spawnerMined.keySet());

			// Inform staff if over threshold
			for (UUID miner : xrayers) {
				Player player = Bukkit.getPlayer(miner);
				if (player == null) continue;

				int diamonds = diamondsMined.getOrDefault(miner, 0);
				int spawners = spawnerMined.getOrDefault(miner, 0);

				for (Player staff : Bukkit.getOnlinePlayers()) {
					if (PlayerUtils.isEqualOrHigherThen(staff, Rank.TRAINEE) && TSMCUser.fromPlayer(staff).isXray()) {
                        int diamondThreshhold = 13;
                        if (diamonds >= diamondThreshhold) {
							staff.sendMessage(CC.translate("&8[&4&lAnitCheat&8] &4[XRAY] &f" + player.getName() + " is suspected for XRAY. Mined " + diamonds + " diamonds in the last MINUTE! World&4=&f" + player.getWorld().getName() + ""));
						}
                        int spawnerThreshhold = 4;
                        if (spawners >= spawnerThreshhold) {
							staff.sendMessage(CC.translate("&8[&4&lAnitCheat&8] &4[XRAY] &f" + player.getName() + " is suspected for XRAY. Mined " + spawners + " spawners in the last MINUTE! World&4=&f" + player.getWorld().getName() + ""));
						}
					}
				}
			}

			xrayers.clear();
			diamondsMined.clear();
			spawnerMined.clear();
		}
	}

	@EventHandler
	public void onMine(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (e.getBlock().getType() == Material.DIAMOND_ORE) {
			if (diamondsMined.containsKey(player.getUniqueId())) {
				diamondsMined.put(player.getUniqueId(), diamondsMined.get(player.getUniqueId()) + 1);
			} else {
				diamondsMined.put(player.getUniqueId(), 1);
			}
		} else if (e.getBlock().getType() == Material.MOB_SPAWNER) {
			if (spawnerMined.containsKey(player.getUniqueId())) {
				spawnerMined.put(player.getUniqueId(), spawnerMined.get(player.getUniqueId()) + 1);
			} else {
				spawnerMined.put(player.getUniqueId(), 1);
			}
		}
	}

}
