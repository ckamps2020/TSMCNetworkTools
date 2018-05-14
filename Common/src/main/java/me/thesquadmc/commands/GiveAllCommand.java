package me.thesquadmc.commands;

import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.inventory.ItemBuilder;
import me.thesquadmc.utils.inventory.ItemUtils;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GiveAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerUtils.isEqualOrHigherThen(player, Rank.MANAGER)) {
                if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
                    ItemStack itemStack = new ItemBuilder(player.getItemInHand()).build();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (ItemUtils.hasInventorySpace(p)) {
                            p.getInventory().addItem(itemStack);
                            player.sendMessage(CC.translate("&e&lGIVEALL &6■ &7You have received an item from " + player.getName()));
                        } else {
                            ItemUtils.spawnItem(itemStack, p.getLocation());
                            player.sendMessage(CC.translate("&e&lGIVEALL &6■ &7You have received an item from " + player.getName() + ". Since your inventory was full it was placed at your feet"));
                        }
                    }
                    player.sendMessage(CC.translate("&e&lGIVEALL &6■ &7You gave everyone a copy of the item in your hand!"));
                } else {
                    player.sendMessage(CC.translate("&e&lGIVEALL &6■ &7You are not holding anything!"));
                }
            } else {
                player.sendMessage(CC.translate("&e&lPERMISSIONS &6■ &7You do not have permission to use this command!"));
            }
        }
        return true;
    }

}
