package com.thesquadmc.networktools.utils.player;

import com.sgtcaze.nametagedit.NametagEdit;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.MojangGameProfile;
import com.thesquadmc.networktools.abstraction.NMSAbstract;
import com.thesquadmc.networktools.abstraction.ProfileProperty;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.server.Multithreading;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;
import me.lucko.luckperms.api.caching.UserData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerUtils {

    private static final NMSAbstract NMS_ABSTRACT = NetworkTools.getInstance().getNMSAbstract();

    public static void sit(Player p) {
        NMS_ABSTRACT.sit(p);
    }

    public static void stand(Player p) {
        NMS_ABSTRACT.stand(p);
    }

    public static Set<Player> getSitting() {
        return NMS_ABSTRACT.getSitting();
    }

    private static boolean isInBorder(Location center, Location notCenter, int range) {
        int x = center.getBlockX(), z = center.getBlockZ();
        int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();
        return x1 < (x + range) && z1 < (z + range) && x1 > (x - range) && z1 > (z - range);
    }

    public static Optional<? extends Player> getPlayer(String name) {
        return Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static List<Player> getNearbyPlayers(Location where, int range) {
        List<Player> found = new ArrayList<>();
        for (Entity entity : where.getWorld().getEntities()) {
            if (isInBorder(where, entity.getLocation(), range)) {
                if (entity instanceof Player) {
                    found.add((Player) entity);
                }
            }
        }
        return found;
    }

    /**
     * Checks whether a player is online across the network
     *
     * @param uuid the player to find
     * @return if the player is online on the network
     */
    public static CompletableFuture<Boolean> isOnline(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (Bukkit.getPlayer(uuid) != null) {
                return true;
            }

            try (Jedis jedis = NetworkTools.getInstance().getRedisManager().getResource()) {
                Map<String, String> status = jedis.hgetAll("player:" + uuid.toString());
                if (status == null) {
                    return false;

                } else if (status.containsKey("lastOnline")) {
                    return false;
                }
            }

            return true;
        }, Multithreading.POOL);
    }

    public static Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }

    public static void sendWorldBorderPacket(Player player, int warningBlocks) {
        NMS_ABSTRACT.sendWorldBorder(player, warningBlocks);
    }

    public static int convertExpToLevelLogin(Player player, int exp) {
        Double level = Math.max(Math.floor(8.7 * Math.log(exp + 111) + -40), 1);
        return level.intValue();
    }

    public static int convertExpToLevelLogin(int exp) {
        Double level = Math.max(Math.floor(8.7 * Math.log(exp + 111) + -40), 1);
        return level.intValue();
    }

    public static void hidePlayerSpectatorStaff(Player player) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (!isEqualOrHigherThen(p, Rank.TRAINEE)) {
                p.hidePlayer(player);
            }
        }
    }

    public static void hidePlayerSpectatorYT(Player player) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.hidePlayer(player);
        }
    }

    public static void showPlayerSpectator(Player player) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.showPlayer(player);
        }
    }

    public static void unfreezePlayer(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.removePotionEffect(PotionEffectType.JUMP);
    }

    public static void freezePlayer(Player player) {
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200, false, false));
    }

    public static boolean isEqualOrHigherThen(Player player, Rank rank) {
        return isEqualOrHigherThen(player.getUniqueId(), rank);
    }

    public static void refreshPlayer(Player player) {
        if (player == null) return;

        for (Player target : player.getWorld().getPlayers()) {
            refreshPlayer(player, target);
        }
    }

    public static void refreshPlayer(Player player, Player target) {
        if (player == null || target == null || player.getWorld() != target.getWorld() || !target.canSee(player))
            return;

        target.hidePlayer(player);
        Bukkit.getScheduler().runTaskLater(NetworkTools.getInstance(), () -> target.showPlayer(player), 2L);
    }

    public static void refreshPlayer(Player player, Collection<Player> targets) {
        targets.forEach(target -> refreshPlayer(player, target));
    }

    public static void addPlayerPermission(Player player, String perm) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getName() + " permission set " + perm + " true");
    }

    public static String getPlayerPrefix(Player player) {
        User user = NetworkTools.getInstance().getLuckPermsApi().getUser(player.getUniqueId());
        UserData cachedData = user.getCachedData();
        Contexts contexts = Contexts.allowAll();
        MetaData metaData = cachedData.getMetaData(contexts);
        return metaData.getPrefix();
    }

    public static String getPlayerSuffix(Player player) {
        User user = NetworkTools.getInstance().getLuckPermsApi().getUser(player.getUniqueId());
        UserData cachedData = user.getCachedData();
        Contexts contexts = Contexts.allowAll();
        MetaData metaData = cachedData.getMetaData(contexts);
        return metaData.getSuffix();
    }

    public static boolean isEqualOrHigherThen(UUID uuid, Rank rank) {
        User user = NetworkTools.getInstance().getLuckPermsApi().getUser(uuid);
        if (user != null) {
            for (Rank r : Rank.values()) {
                if (r.getName().equalsIgnoreCase(user.getPrimaryGroup())) {
                    if (r.getPriority() >= rank.getPriority()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean doesRankMatch(Player player, Rank rank) {
        User user = NetworkTools.getInstance().getLuckPermsApi().getUser(player.getUniqueId());
        if (user.getPrimaryGroup() != null) {
            for (Rank r : Rank.values()) {
                if (r.getName().equalsIgnoreCase(user.getPrimaryGroup())) {
                    if (r.getName().equalsIgnoreCase(rank.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasPermission(Group group, String permission) {
        return group.getPermissions().stream()
                .filter(Node::getValue)
                .filter(Node::isPermanent)
                .filter(n -> !n.isServerSpecific())
                .filter(n -> !n.isWorldSpecific())
                .anyMatch(n -> n.getPermission().startsWith(permission));
    }

    public static boolean hasPermission(Player player, String permission) {
        return NetworkTools.getInstance().getVaultPermissions().has(player, permission);
    }

    public static void setName(Player player, String name) {
        NMS_ABSTRACT.getGameProfile(player).setName(name);

        NametagEdit.getApi().reloadNametag(player);
    }

    public static void updateGlobalSkin(String name) {
        NetworkTools networkTools = NetworkTools.getInstance();
        ProfileProperty property = NMS_ABSTRACT.getSkinProperty(name);

        networkTools.setValue(property.getValue());
        networkTools.setValue(property.getSignature());
    }

    public static void removePlayerTextures(Player player) {
        hidePlayerSpectatorYT(player);
        NMS_ABSTRACT.getGameProfile(player).removeProperty("textures");
        showPlayerSpectator(player);
    }

    public static void restorePlayerTextures(Player player) {
        hidePlayerSpectatorYT(player);
        removePlayerTextures(player);

        TSMCUser user = TSMCUser.fromPlayer(player);
        ProfileProperty property = NMS_ABSTRACT.createNewProperty("textures", user.getSkinKey(), user.getSignature());
        NMS_ABSTRACT.getGameProfile(player).addProperty("textures", property);

        showPlayerSpectator(player);
    }

    public static void setSkin(Player player, String playerSkin) {
        hidePlayerSpectatorYT(player);

        MojangGameProfile profile = NMS_ABSTRACT.getGameProfile(player);
        profile.removeProperty("textures");

        ProfileProperty property = NMS_ABSTRACT.getSkinProperty(playerSkin);
        if (property != null) {
            profile.addProperty("textures", property);
        }

        showPlayerSpectator(player);
    }

    public static void setSameSkin(Player player) {
        hidePlayerSpectatorYT(player);

        MojangGameProfile profile = NMS_ABSTRACT.getGameProfile(player);
        profile.removeProperty("textures");

        NetworkTools networkTools = NetworkTools.getInstance();
        ProfileProperty property = NMS_ABSTRACT.createNewProperty("textures", networkTools.getValue(), networkTools.getSig());
        if (property != null) {
            profile.addProperty("textures", property);
        }

        showPlayerSpectator(player);
    }

    public static double getArmorLevel(Player player) {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        ItemStack helmet = null;
        ItemStack boots = null;
        ItemStack chest = null;
        ItemStack pants = null;

        if (inv.getBoots() != null) {
            boots = inv.getBoots();
        }
        if (inv.getBoots() == null) {
            boots = new ItemStack(Material.LEATHER_BOOTS);
        }
        if (inv.getHelmet() != null) {
            helmet = inv.getHelmet();
        }
        if (inv.getHelmet() == null) {
            helmet = new ItemStack(Material.LEATHER_HELMET);
        }
        if (inv.getChestplate() != null) {
            chest = inv.getChestplate();
        }
        if (inv.getChestplate() == null) {
            chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        }
        if (inv.getLeggings() != null) {
            pants = inv.getLeggings();
        }
        if (inv.getLeggings() == null) {
            pants = new ItemStack(Material.LEATHER_LEGGINGS);
        }
        double red = 0.0;
        if (helmet.getType() == null || helmet.getType() == Material.AIR) red = red + 0.0;
        else if (helmet != null && helmet.getType() == Material.LEATHER_HELMET) red = red + 0.04;
        else if (helmet != null && helmet.getType() == Material.GOLD_HELMET) red = red + 0.08;
        else if (helmet != null && helmet.getType() == Material.CHAINMAIL_HELMET) red = red + 0.08;
        else if (helmet != null && helmet.getType() == Material.IRON_HELMET) red = red + 0.08;
        else if (helmet != null && helmet.getType() == Material.DIAMOND_HELMET) red = red + 0.12;
        //
        if (boots.getType() == null || boots.getType() == Material.AIR) red = red + 0;
        else if (boots != null && boots.getType() == Material.LEATHER_BOOTS) red = red + 0.04;
        else if (boots != null && boots.getType() == Material.GOLD_BOOTS) red = red + 0.04;
        else if (boots != null && boots.getType() == Material.CHAINMAIL_BOOTS) red = red + 0.04;
        else if (boots != null && boots.getType() == Material.IRON_BOOTS) red = red + 0.08;
        else if (boots != null && boots.getType() == Material.DIAMOND_BOOTS) red = red + 0.12;
        //
        if (pants.getType() == null || pants.getType() == Material.AIR) red = red + 0;
        else if (pants != null && pants.getType() == Material.LEATHER_LEGGINGS) red = red + 0.08;
        else if (pants != null && pants.getType() == Material.GOLD_LEGGINGS) red = red + 0.12;
        else if (pants != null && pants.getType() == Material.CHAINMAIL_LEGGINGS) red = red + 0.16;
        else if (pants != null && pants.getType() == Material.IRON_LEGGINGS) red = red + 0.20;
        else if (pants != null && pants.getType() == Material.DIAMOND_LEGGINGS) red = red + 0.24;
        //
        if (chest.getType() == null || chest.getType() == Material.AIR) red = red + 0;
        else if (chest != null && chest.getType() == Material.LEATHER_CHESTPLATE) red = red + 0.12;
        else if (chest != null && chest.getType() == Material.GOLD_CHESTPLATE) red = red + 0.20;
        else if (chest != null && chest.getType() == Material.CHAINMAIL_CHESTPLATE) red = red + 0.20;
        else if (chest != null && chest.getType() == Material.IRON_CHESTPLATE) red = red + 0.24;
        else if (chest != null && chest.getType() == Material.DIAMOND_CHESTPLATE) red = red + 0.32;
        return red;
    }

}
