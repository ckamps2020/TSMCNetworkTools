package me.thesquadmc.listeners;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.StringUtils;
import me.thesquadmc.utils.handlers.UpdateEvent;
import me.thesquadmc.utils.handlers.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class XrayListener implements Listener {

	private final Main main;
	private final int diamondThreshhold = 8;
	private final int spawnerThreshhold = 10;
	private Map<UUID, Integer> diamondsMined = new HashMap<>();
	private Map<UUID, Integer> spawnerMined = new HashMap<>();

	public XrayListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onUpdate(UpdateEvent e) {
		if (e.getUpdateType() == UpdateType.MIN) {
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
			if (diamondsMined.get(player.getUniqueId()) >= diamondThreshhold) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					User u = main.getLuckPermsApi().getUser(p.getUniqueId());
					TempData data = main.getTempDataManager().getTempData(p.getUniqueId());
					if (main.hasPerm(u, "tools.staff.xrayverbose")) {
						if (data.isXray()) {
							p.sendMessage(StringUtils.msg("&8[&4&lAnitCheat&8] &4[XRAY] &f" + player.getName() + " is suspected for XRAY. Mined " + diamondsMined.get(player.getUniqueId()) + " diamonds in the last MINUTE! World&4=&f" + player.getWorld().getName() + ""));
						}
					}
				}
			}
		} else if (e.getBlock().getType() == Material.MOB_SPAWNER) {
			if (spawnerMined.containsKey(player.getUniqueId())) {
				spawnerMined.put(player.getUniqueId(), spawnerMined.get(player.getUniqueId()) + 1);
			} else {
				spawnerMined.put(player.getUniqueId(), 1);
			}
			if (spawnerMined.get(player.getUniqueId()) >= spawnerThreshhold) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					User u = main.getLuckPermsApi().getUser(p.getUniqueId());
					TempData data = main.getTempDataManager().getTempData(p.getUniqueId());
					if (main.hasPerm(u, "tools.staff.xrayverbose")) {
						if (data.isXray()) {
							p.sendMessage(StringUtils.msg("&8[&4&lAnitCheat&8] &4[XRAY] &f" + player.getName() + " is suspected for XRAY. Mined " + spawnerMined.get(player.getUniqueId()) + " spawners in the last MINUTE! World&4=&f" + player.getWorld().getName() + ""));
						}
					}
				}
			}
		}
	}

}
