package com.thesquadmc.networktools.commands;

import com.google.common.collect.Multimap;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.inventory.ItemBuilder;
import com.thesquadmc.networktools.utils.inventory.ItemUtils;
import com.thesquadmc.networktools.utils.inventory.builder.Menu;
import com.thesquadmc.networktools.utils.inventory.builder.MenuItem;
import com.thesquadmc.networktools.utils.inventory.builder.MenuManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class StaffMenuBuilder extends Menu {

    private final Multimap<Rank, StaffCommand.StaffListInfo> staffList;

    public StaffMenuBuilder(int size, Multimap<Rank, StaffCommand.StaffListInfo> staffList) {
        super("Staff Online", size);
        this.staffList = staffList;

        staffList.keySet().stream()
                .sorted(Enum::compareTo)
                .forEachOrdered(rank -> {
                    Collection<StaffCommand.StaffListInfo> infos = staffList.get(rank);

                    infos.stream()
                            .filter(staffListInfo -> {
                                System.out.println(staffListInfo.isVanished());

                                return !staffListInfo.isVanished();
                            })
                            .forEach(info -> addMenuItem(new MenuItem() {
                                @Override
                                public ItemStack getItem(Player player) {
                                    ItemStack itemStack = ItemUtils.buildSkull(info.getName());

                                    return new ItemBuilder(itemStack)
                                            .name(rank.getPrefix() + " " + info.getName())
                                            .lore(" ")
                                            .lore("&7Server: &e" + WordUtils.capitalize(info.getServer().toLowerCase()))
                                            .build();
                                }

                                @Override
                                public void onClick(Player player, Inventory inventory, ClickType clickType) {
                                }
                            }));
                });

    }
}
