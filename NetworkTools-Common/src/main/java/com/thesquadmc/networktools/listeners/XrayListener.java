package com.thesquadmc.networktools.listeners;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.UpdateType;
import com.thesquadmc.networktools.utils.handlers.UpdateEvent;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import com.thesquadmc.networktools.utils.server.ServerType;
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
    public void on(UpdateEvent e) {
        if (e.getUpdateType() == UpdateType.MIN) {
            if (NetworkTools.getInstance().getServerType() == ServerType.PRISON) {
                return;
            }

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
                    if (PlayerUtils.isEqualOrHigherThen(staff, Rank.TRAINEE) && TSMCUser.fromPlayer(staff).getSetting(PlayerSetting.XRAY_NOTIFICATION)) {
                        int diamondThreshhold = 13;
                        if (diamonds >= diamondThreshhold) {
                            staff.sendMessage(CC.translate("&8[&4&lAntiCheat&8] &4[XRAY] &f" + player.getName() + " is suspected for XRAY. Mined " + diamonds + " diamonds in the last MINUTE! World&4=&f" + player.getWorld().getName() + ""));
                        }
                        int spawnerThreshhold = 4;
                        if (spawners >= spawnerThreshhold) {
                            staff.sendMessage(CC.translate("&8[&4&lAntiCheat&8] &4[XRAY] &f" + player.getName() + " is suspected for XRAY. Mined " + spawners + " spawners in the last MINUTE! World&4=&f" + player.getWorld().getName() + ""));
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
    public void on(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (NetworkTools.getInstance().getServerType() == ServerType.PRISON) {
            return;
        }

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
