package com.thesquadmc.networktools.listeners;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.commands.StaffmodeCommand;
import com.thesquadmc.networktools.utils.enums.UpdateType;
import com.thesquadmc.networktools.utils.handlers.UpdateEvent;
import com.thesquadmc.networktools.utils.inventory.InventorySize;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class StaffmodeListener implements Listener {

    private static List<UUID> mining = new ArrayList<>();
    private static Map<UUID, Integer> blocksMined = new HashMap<>();
    private final NetworkTools networkTools;
    private Map<UUID, Integer> timeRemaining = new HashMap<>();
    private Map<UUID, Map<UUID, Integer>> cps = new HashMap<>();
    private Map<UUID, Integer> tempCPS = new HashMap<>();

    public StaffmodeListener(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    @EventHandler
    public void on(UpdateEvent e) {
        if (e.getUpdateType() == UpdateType.SEC) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (timeRemaining.containsKey(player.getUniqueId())) {
                    if (timeRemaining.get(player.getUniqueId()) - 1 == 0) {
                        Map<UUID, Integer> m = cps.get(player.getUniqueId());
                        for (Map.Entry<UUID, Integer> mm : m.entrySet()) {
                            int averageCPS = mm.getValue() / 5;
                            Player t = Bukkit.getPlayer(mm.getKey());
                            player.sendMessage(CC.translate("&e&lCPS &6■ &7CPS Calculated! Average CPS of &e" + t.getName() + " &7is: &e" + averageCPS));
                        }
                        cps.remove(player.getUniqueId());
                        timeRemaining.remove(player.getUniqueId());
                    } else {
                        timeRemaining.put(player.getUniqueId(), timeRemaining.get(player.getUniqueId()) - 1);
                        player.sendMessage(CC.translate("&e&lCPS &6■ &7Calculating… printing CPS in &e" + timeRemaining.get(player.getUniqueId())));
                        tempCPS.clear();
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() != null && e.getTarget().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getTarget();
            if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getPlayer().getItemInHand() != null) {
            if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                ItemStack stack = e.getPlayer().getItemInHand();
                if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("VANISH")) {
                    player.performCommand("vanish");
                    return;
                }
                if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("PLACE CHEST")) {
                    if (e.getClickedBlock() != null && e.getClickedBlock().getType() != null) {
                        if (e.getAction().toString().toUpperCase().contains("RIGHT")) {
                            if (Bukkit.getServerName().contains("FACTIONS") && player.getLocation().getY() <= 50) {
                                Block block = e.getClickedBlock();
                                block.getLocation().getWorld().getBlockAt(block.getLocation().clone().add(0, 1, 0)).setType(Material.CHEST);
                            } else {
                                player.sendMessage(CC.translate("&e&lCHEST ESP &6■ &7You can't use that here!"));
                            }
                        }
                    }
                    return;
                }
                if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("CONTROL PANEL")) {
                    if (e.getAction().toString().toUpperCase().contains("RIGHT")) {
                        player.closeInventory();
                        networkTools.getStaffmodeInventory().buildStaffpanel(player);
                    } else if (e.getAction().toString().toUpperCase().contains("LEFT")) {
                        player.closeInventory();
                        player.performCommand("rtp");
                    }
                    return;
                }
            }
        }
        if (tempCPS.get(player.getUniqueId()) != null) {
            tempCPS.put(player.getUniqueId(), tempCPS.get(player.getUniqueId()));
        } else {
            tempCPS.put(player.getUniqueId(), 1);
        }
        for (Map.Entry<UUID, Map<UUID, Integer>> m : cps.entrySet()) {
            Map<UUID, Integer> mm = m.getValue();
            if (mm.get(player.getUniqueId()) != null) {
                mm.put(player.getUniqueId(), mm.get(player.getUniqueId()) + 1);
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
            player.getInventory().clear();
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                p.showPlayer(player);
            }
            for (ItemStack itemStack : StaffmodeCommand.getStaffmode().get(player.getUniqueId())) {
                if (itemStack != null) {
                    player.getInventory().addItem(itemStack);
                }
            }
            StaffmodeCommand.getStaffmode().remove(player.getUniqueId());
            player.sendMessage(CC.translate("&e&lSTAFFMODE &6■  &7Staff mode has been &edisabled&7"));
        }
    }

    @EventHandler
    public void on(PlayerPickupItemEvent e) {
        if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent e) {
        Double x = e.getFrom().getX();
        Double y = e.getFrom().getY();
        Double z = e.getFrom().getZ();
        Double xx = e.getTo().getX();
        Double yy = e.getTo().getY();
        Double zz = e.getTo().getZ();
        if (x.intValue() > xx.intValue() || x.intValue() < xx.intValue()) {
            if (mining.contains(e.getPlayer().getUniqueId())) {
                mining.remove(e.getPlayer().getUniqueId());
                blocksMined.remove(e.getPlayer().getUniqueId());
            }
        } else if (y.intValue() > yy.intValue() || y.intValue() < yy.intValue()) {
            if (mining.contains(e.getPlayer().getUniqueId())) {
                mining.remove(e.getPlayer().getUniqueId());
                blocksMined.remove(e.getPlayer().getUniqueId());
            }
        } else if (z.intValue() > zz.intValue() || z.intValue() < zz.intValue()) {
            if (mining.contains(e.getPlayer().getUniqueId())) {
                mining.remove(e.getPlayer().getUniqueId());
                blocksMined.remove(e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        if (Bukkit.getServerName().toUpperCase().contains("SKYBLOCK")) {
            if (!e.getBlock().getType().name().toUpperCase().contains("WOOD")) {
                if (blocksMined.containsKey(e.getPlayer().getUniqueId())) {
                    if (blocksMined.get(e.getPlayer().getUniqueId()) + 1 >= 3) {
                        if (!mining.contains(e.getPlayer().getUniqueId())) {
                            mining.add(e.getPlayer().getUniqueId());
                        }
                    } else {
                        blocksMined.put(e.getPlayer().getUniqueId(), blocksMined.get(e.getPlayer().getUniqueId()) + 1);
                    }
                } else {
                    blocksMined.put(e.getPlayer().getUniqueId(), 1);
                }
            }
        }
    }

    @EventHandler
    public void on(BlockPlaceEvent e) {
        if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER && e.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();
            if (StaffmodeCommand.getStaffmode().containsKey(p.getUniqueId()) || StaffmodeCommand.getStaffmode().containsKey(d.getUniqueId())) {
                e.setCancelled(true);
            }
        } else if (e.getEntity().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            if (StaffmodeCommand.getStaffmode().containsKey(p.getUniqueId())) {
                e.setCancelled(true);
            }
        } else if (e.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getDamager();
            if (StaffmodeCommand.getStaffmode().containsKey(p.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(FoodLevelChangeEvent e) {
        Player player = (Player) e.getEntity();
        if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    @EventHandler
    public void on(PlayerItemDamageEvent e) {
        if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerPortalEvent e) {
        if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityDamageEvent e) {
        if (e.getEntity() != null && e.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) e.getEntity();
            if (player != null) {
                if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerArmorStandManipulateEvent e) {
        if (StaffmodeCommand.getStaffmode().containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getInventory() != null && e.getInventory().getName().equalsIgnoreCase("LOGS")) {
            e.setCancelled(true);
            return;
        }
        if (e.getInventory() != null && e.getInventory().getName().equalsIgnoreCase("CONTROL PANEL")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if (e.getSlot() == 4) {
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    player.closeInventory();
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(CC.translate("&e&lGAMEMODE &6■ &7Gamemode updated to survival"));
                } else {
                    player.closeInventory();
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(CC.translate("&e&lGAMEMODE &6■ &7Gamemode updated to spectator"));
                }
            } else if (e.getSlot() == 13) {
                player.performCommand("randomtp");
            } else if (e.getSlot() == 15) {
                if (Bukkit.getServerName().toUpperCase().contains("FACTIONS")) {
                    int i = 0;
                    List<Player> below = new ArrayList<>();
                    for (Player t : Bukkit.getOnlinePlayers()) {
                        if (i < 54) {
                            if (t.getLocation().getY() < 16.0) {
                                below.add(t);
                                i++;
                            }
                        } else {
                            break;
                        }
                    }
                    Inventory inv = Bukkit.createInventory(null, InventorySize.SIX_LINE, "Miners Below Y 16");
                    int ii = 0;
                    for (Player p : below) {
                        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
                        head.setDurability((short) 3);
                        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                        skullMeta.setOwner(p.getName());
                        skullMeta.setDisplayName(p.getName());
                        head.setItemMeta(skullMeta);
                        inv.setItem(ii, head);
                        ii++;
                    }
                    player.openInventory(inv);
                } else if (Bukkit.getServerName().toUpperCase().contains("SKYBLOCK")) {
                    int i = 0;
                    List<Player> below = new ArrayList<>();
                    for (UUID uuid : mining) {
                        if (i < 54) {
                            Player t = Bukkit.getPlayer(uuid);
                            if (t != null) {
                                below.add(t);
                                i++;
                            }
                        } else {
                            break;
                        }
                    }
                    Inventory inv = Bukkit.createInventory(null, InventorySize.SIX_LINE, "AFK Miners");
                    int ii = 0;
                    for (Player p : below) {
                        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
                        head.setDurability((short) 3);
                        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                        skullMeta.setOwner(p.getName());
                        skullMeta.setDisplayName(p.getName());
                        head.setItemMeta(skullMeta);
                        inv.setItem(ii, head);
                        ii++;
                    }
                    player.openInventory(inv);
                }
            }
        } else if (e.getInventory() != null && e.getInventory().getName().equalsIgnoreCase("Miners Below Y 16") || e.getInventory().getName().equalsIgnoreCase("AFK Miners")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                ItemStack stack = e.getCurrentItem();
                if (stack.getItemMeta() != null) {
                    Player player = (Player) e.getWhoClicked();
                    String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
                    Player t = Bukkit.getPlayer(name);
                    if (t != null) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(t.getLocation());
                    } else {
                        player.sendMessage(CC.translate("&e&lMINERS &6■ &7That player is offline or does not exist!"));
                    }
                }
            }
        } else if (e.getInventory() != null) {
            Player player = (Player) e.getWhoClicked();
            if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() == EntityType.PLAYER) {
            Player target = (Player) e.getRightClicked();
            Player player = e.getPlayer();
            if (player.getItemInHand() != null) {
                ItemStack stack = player.getItemInHand();
                if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
                    if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("FREEZE TARGET")) {
                        player.performCommand("freeze " + target.getName());
                    } else if (stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().toUpperCase().contains("CPS CHECKER")) {
                        if (!cps.containsKey(player.getUniqueId())) {
                            if (target.getUniqueId() != null && target.getName() != null) {
                                player.sendMessage(CC.translate("&e&lCPS &6■ &7Calculating… printing CPS in &e5&7"));
                                timeRemaining.put(player.getUniqueId(), 5);
                                Map<UUID, Integer> map = new HashMap<>();
                                map.put(target.getUniqueId(), 0);
                                cps.put(player.getUniqueId(), map);
                            }
                        } else {
                            player.sendMessage(CC.translate("&e&lCPS &6■ &7You are already calculating someones CPS!"));
                        }
                    }
                }
            }
        }
    }

}
