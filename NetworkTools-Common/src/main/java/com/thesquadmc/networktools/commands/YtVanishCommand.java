package com.thesquadmc.networktools.commands;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.inventory.ItemBuilder;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class YtVanishCommand implements CommandExecutor {

    private final NetworkTools plugin;

    public YtVanishCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.YOUTUBE)) {
                TSMCUser user = TSMCUser.fromPlayer(player);
                if (!user.getSetting(PlayerSetting.VANISHED)) {
                    if (!user.getSetting(PlayerSetting.YOUTUBE_VANISHED)) {
                        enableVanish(player, user);
                    } else {
                        disableVanish(player, user);
                    }
                } else {
                    player.sendMessage(CC.translate("&e&lYT VANISH &6■ &7Please disable normal vanish first!"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

    private void disableVanish(Player player, TSMCUser user) {
        PlayerUtils.showPlayerSpectator(player);
        user.updateSetting(PlayerSetting.YOUTUBE_VANISHED, false);
        player.sendMessage(CC.translate("&e&lYT VANISH &6■ &7Vanish has been &edisabled"));

        plugin.getRedisManager().executeJedisAsync(jedis -> {
            JsonObject object = new JsonObject();
            object.add("server", JSONUtils.getGson().toJsonTree(Bukkit.getServerName()));
            object.add("rank", JSONUtils.getGson().toJsonTree(PlayerUtils.getStaffRank(player).name()));
            object.add("vanished", JSONUtils.getGson().toJsonTree(false));

            jedis.hset("staff", player.getName(), object.toString());
        });
    }

    private void enableVanish(Player player, TSMCUser user) {
        PlayerUtils.hidePlayerSpectatorYT(player);
        user.updateSetting(PlayerSetting.YOUTUBE_VANISHED, true);
        if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
            player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK, 10).name("&e&lToggle Vanish &7off").lore("&7Toggle vanish on or off").build());
        }
        player.sendMessage(CC.translate("&e&lVANISH &6■ &7You toggled vanish &eon&7! No one will be able to see you"));

        plugin.getRedisManager().executeJedisAsync(jedis -> {
            JsonObject object = new JsonObject();
            object.add("server", JSONUtils.getGson().toJsonTree(Bukkit.getServerName()));
            object.add("rank", JSONUtils.getGson().toJsonTree(PlayerUtils.getStaffRank(player).name()));
            object.add("vanished", JSONUtils.getGson().toJsonTree(true));

            jedis.hset("staff", player.getName(), object.toString());
        });
    }

}
