package me.thesquadmc.listeners;

import me.thesquadmc.Main;
import me.thesquadmc.managers.TempDataManager;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.enums.UpdateType;
import me.thesquadmc.utils.msgs.CC;
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

	private final Main main;
	private final int diamondThreshhold = 13;
	private final int spawnerThreshhold = 4;
	private Map<UUID, Integer> diamondsMined = new HashMap<>();
	private Map<UUID, Integer> spawnerMined = new HashMap<>();

	public XrayListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.MIN) {
			TempDataManager dataManager = main.getTempDataManager();

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
					TempData data = dataManager.getTempData(staff.getUniqueId());
					if (PlayerUtils.isEqualOrHigherThen(staff, Rank.TRAINEE) && data.isXray()) {
						if (diamonds >= diamondThreshhold) {
							staff.sendMessage(CC.translate("&8[&4&lAnitCheat&8] &4[XRAY] &f" + player.getName() + " is suspected for XRAY. Mined " + diamonds + " diamonds in the last MINUTE! World&4=&f" + player.getWorld().getName() + ""));
						}
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
