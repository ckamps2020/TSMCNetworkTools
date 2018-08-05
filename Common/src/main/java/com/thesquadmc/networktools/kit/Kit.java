package com.thesquadmc.networktools.kit;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.inventory.ItemUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kit {

    private String name;
    private long cooldown;
    private List<ItemStack> items = new ArrayList<>();

    public Kit() {
    } //For Gson

    public Kit(String name, List<ItemStack> items) {
        this.name = name;
        this.items = items;
    }

    public void giveKit(Player player) {
        LocalPlayer localPlayer = NetworkTools.getInstance().getLocalPlayerManager().getPlayer(player);

        boolean give = false;
        Long lastUsed = localPlayer.getUsedKit(name);
        if (lastUsed == -1 || (System.currentTimeMillis() - lastUsed) >= cooldown) {
            give = true;
        }

        if (!give) {
            player.sendMessage(CC.RED + "You cannot receive this kit yet!");
            return;
        }

        if (ItemUtils.getEmptySlots(player.getInventory()) < items.size()) {
            player.sendMessage(CC.RED + "You do not have enough space in your inventory for this kit!");
            return;
        }

        items.forEach(player.getInventory()::addItem);
        player.sendMessage(CC.translate("&e&lKIT &6■ &7You have received the &e{0} &7Kit!", name));
    }

    public String getName() {
        return name;
    }

    public long getCooldown() {
        return cooldown;
    }

    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(items);
    }
}