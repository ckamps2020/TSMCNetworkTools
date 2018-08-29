package com.thesquadmc.networktools.kit;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.player.local.LocalPlayer;
import com.thesquadmc.networktools.utils.inventory.ItemUtils;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Kit {

    private String name;
    private long cooldown;
    private List<ItemStack> items = new ArrayList<>();

    public Kit() {
    } //For Gson

    public Kit(String name, long cooldown, List<ItemStack> items) {
        this.name = name;
        this.cooldown = cooldown;
        this.items = items;
    }

    public void giveKit(Player player) {
        LocalPlayer localPlayer = NetworkTools.getInstance().getLocalPlayerManager().getPlayer(player);

        if (!player.hasPermission("essentials.kits." + name.toLowerCase())) {
            player.sendMessage(CC.RED + "You do not have permission for this kit!");
            return;
        }

        boolean give = false;
        Long lastUsed = localPlayer.getUsedKit(name);
        if (lastUsed == -1 || (System.currentTimeMillis() - lastUsed) >= cooldown) {
            give = true;
        }

        if (!give) {
            long since = System.currentTimeMillis() - lastUsed;
            player.sendMessage(CC.RED + "You can use this kit again in " + TimeUtils.getFormattedTime(cooldown - since));
            return;
        }

        if (ItemUtils.getEmptySlots(player.getInventory()) < items.size()) {
            player.sendMessage(CC.RED + "You do not have enough space in your inventory for this kit!");
            return;
        }

        items.forEach(player.getInventory()::addItem);
        player.sendMessage(CC.translate("&e&lKIT &6■ &7You have received the &e{0} &7Kit!", name));

        localPlayer.setUsedKit(name, System.currentTimeMillis());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kit kit = (Kit) o;
        return cooldown == kit.cooldown &&
                Objects.equals(name, kit.name) &&
                Objects.equals(items, kit.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cooldown, items);
    }
}