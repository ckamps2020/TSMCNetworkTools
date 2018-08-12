package com.thesquadmc.networktools.listeners;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.commands.FreezeCommand;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public final class FreezeListener implements Listener {

    private final NetworkTools plugin;

    public FreezeListener(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (FreezeCommand.getFrozen().contains(player.getUniqueId())) {
            PlayerUtils.unfreezePlayer(player);
            plugin.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
            plugin.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
            plugin.getFrozenInventory().getAdmitted().remove(player.getUniqueId());
            plugin.getFrozenInventory().getDenying().remove(player.getUniqueId());
            FreezeCommand.getFrozen().remove(player.getUniqueId());
            for (Player t : Bukkit.getOnlinePlayers()) {
                if (PlayerUtils.isEqualOrHigherThen(t, Rank.MOD)) {
                    t.sendMessage(CC.translate("&e&lFREEZE &6■ &c" + player.getName() + " has logged out while frozen!"));
                }
            }
        }
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        if (e.getEntity() != null && e.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();
            if (FreezeCommand.getFrozen().contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("FROZEN")) {
            if (FreezeCommand.getFrozen().contains(e.getPlayer().getUniqueId())) {
                Player player = (Player) e.getPlayer();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (plugin.getFrozenInventory().getScreenshare().containsKey(player.getUniqueId())) {
                        plugin.getFrozenInventory().buildScreenshareInventory(player, plugin.getFrozenInventory().getScreenshare().get(player.getUniqueId()));
                    } else if (plugin.getFrozenInventory().getAdmitMenu().contains(player.getUniqueId())) {
                        plugin.getFrozenInventory().buildAdmitInventory(player);
                    } else if (plugin.getFrozenInventory().getScreenshare().containsKey(player.getUniqueId())) {
                        plugin.getFrozenInventory().buildScreenshareInventory(player, plugin.getFrozenInventory().getScreenshare().get(player.getUniqueId()));
                    } else {
                        plugin.getFrozenInventory().buildFrozenInventory(player);
                    }
                }, 2L);
            }
        } else if (e.getInventory().getName().toUpperCase().startsWith("FREEZE MENU FOR")) {
            plugin.getFrozenInventory().getViewing().remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(AsyncPlayerChatEvent e) {
        if (plugin.getFrozenInventory().getTyping().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            String discordName = e.getMessage();
            Player t = plugin.getFrozenInventory().getTyping().get(e.getPlayer().getUniqueId());
            if (t != null) {
                plugin.getFrozenInventory().getTyping().remove(e.getPlayer().getUniqueId());
                t.closeInventory();
                plugin.getFrozenInventory().getScreenshare().put(t.getUniqueId(), discordName);
                plugin.getFrozenInventory().buildScreenshareInventory(t, discordName);
                e.getPlayer().sendMessage(CC.translate("&e&lFREEZE &6■ &7Thanks! Informing the frozen player now"));
            } else {
                plugin.getFrozenInventory().getTyping().remove(e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory() != null && e.getInventory().getName().toUpperCase().startsWith("FREEZE MENU FOR ")) {
            e.setCancelled(true);
            Player t = Bukkit.getPlayer(plugin.getFrozenInventory().getViewing().get(player.getUniqueId()));
            if (t == null) {
                player.closeInventory();
                plugin.getFrozenInventory().getViewing().remove(player.getUniqueId());
                return;
            }
            if (e.getSlot() == 11) {
                player.closeInventory();
                player.sendMessage(CC.translate("&e&lFREEZE &6■ &7Please enter your discord name in chat now:"));
                plugin.getFrozenInventory().getTyping().put(player.getUniqueId(), t);
            } else if (e.getSlot() == 13) {
                PlayerUtils.unfreezePlayer(t);
                FreezeCommand.getFrozen().remove(t.getUniqueId());
                plugin.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
                plugin.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
                plugin.getFrozenInventory().getAdmitted().remove(player.getUniqueId());
                plugin.getFrozenInventory().getDenying().remove(player.getUniqueId());
                plugin.getFrozenInventory().getScreenshare().remove(player.getUniqueId());
                player.closeInventory();
                player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You have unfrozen &e" + t.getName()));
                t.closeInventory();
                t.sendMessage(CC.translate("&e&lFREEZE &6■ &7You have been &eunfrozen&7. Thank you for your &epatience&7"));
            } else if (e.getSlot() == 15) {
                ItemStack itemStack = e.getCurrentItem();
                if (itemStack != null) {
                    if (itemStack.getData().getData() == (byte) 7) {
                        plugin.getFrozenInventory().getAdmitMenu().add(t.getUniqueId());
                        plugin.getFrozenInventory().buildAdmitInventory(t);
                        player.sendMessage(CC.translate("&e&lFREEZE &6■ &7You have asked the player to admit to breaking the rules!"));
                        player.closeInventory();
                    }
                }
            } else if (e.getSlot() == 26) {
                player.closeInventory();
            }
        } else if (e.getInventory() != null && e.getInventory().getName().toUpperCase().startsWith("FROZEN")) {
            e.setCancelled(true);
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack != null) {
                if (itemStack.getData().getData() == (byte) 7) {
                    if (e.getClick() == ClickType.RIGHT) {
                        plugin.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
                        plugin.getFrozenInventory().getDenying().add(player.getUniqueId());
                        plugin.getFrozenInventory().buildAdmitInventory(player);
                    } else if (e.getClick() == ClickType.LEFT) {
                        plugin.getFrozenInventory().getAdmitMenu().remove(player.getUniqueId());
                        plugin.getFrozenInventory().getAdmitted().add(player.getUniqueId());
                        plugin.getFrozenInventory().buildAdmitInventory(player);
                        for (Map.Entry<UUID, UUID> map : plugin.getFrozenInventory().getViewing().entrySet()) {
                            Player t = Bukkit.getPlayer(map.getValue());
                            if (t != null && t.getUniqueId() == player.getUniqueId()) {
                                Player p = Bukkit.getPlayer(map.getKey());
                                plugin.getFrozenInventory().buildStaffGUI(p, player);
                            }
                        }
                    }
                }
            }
        }
    }

}
