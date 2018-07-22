package com.thesquadmc.networktools.inventories;

import com.thesquadmc.networktools.utils.inventory.InventorySize;
import com.thesquadmc.networktools.utils.inventory.ItemBuilder;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public final class LogsInventory {

    public static void buildLogsInv(Player player, String name) {
        Inventory inventory = Bukkit.createInventory(null, InventorySize.SIX_LINE, "LOGS");
        ArrayList<String> logs = StringUtils.getLogs().get(name);
        for (int i = 0; i < logs.size(); i++) {
            if (i < 54) {
                List<String> strings = StringUtils.splitString(logs.get(i));
                List<String> msg = new ArrayList<>();
                msg.add(" ");
                for (String string : strings) {
                    msg.add("&f" + string);
                }
                inventory.setItem(i, new ItemBuilder(Material.PAPER).name("&c&lLOG " + (i + 1)).lore(msg).build());
            } else {
                break;
            }
        }
        player.openInventory(inventory);
    }

}
