package com.thesquadmc.networktools.commands;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.command.Command;
import com.thesquadmc.networktools.utils.command.CommandArgs;
import com.thesquadmc.networktools.utils.enums.EnumUtil;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.inventory.InventorySize;
import com.thesquadmc.networktools.utils.json.JSONUtils;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public final class StaffCommand {

    private final NetworkTools plugin;

    private final Multimap<Rank, StaffListInfo> staffList = ArrayListMultimap.create();
    private long lastRetrived = 0;

    public StaffCommand(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Command(name = {"staff"})
    public void staff(CommandArgs args) {
        if (args.isPlayer()) {
            if (lastRetrived == 0 || TimeUtils.elapsed(lastRetrived, 10 * 1000)) {
                staffList.clear();
                lastRetrived = System.currentTimeMillis();

                plugin.getRedisManager().executeJedisAsync(jedis -> {
                    Map<String, String> staff = jedis.hgetAll("staff");

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        System.out.println(staff);
                        staff.forEach((name, data) -> {
                            JsonObject object = JSONUtils.parseObject(data);

                            String server = object.get("server").getAsString();
                            Rank rank = EnumUtil.getEnum(Rank.class, object.get("rank").getAsString());
                            boolean vanished = object.get("vanished").getAsBoolean();

                            staffList.put(rank, new StaffListInfo(name, server, rank, vanished));
                        });

                        int size = staffList.keys().size();
                        new StaffMenuBuilder(InventorySize.getSize(size), staffList).build(args.getPlayer());
                    });
                });

            } else {
                int size = staffList.keys().size();
                new StaffMenuBuilder(InventorySize.getSize(size), staffList).build(args.getPlayer());
            }
        }
    }

    public class StaffListInfo {
        private final String name;
        private final String server;
        private final Rank rank;
        private final boolean vanished;

        private StaffListInfo(String name, String server, Rank rank, boolean vanished) {
            this.name = name;
            this.server = server;
            this.rank = rank;
            this.vanished = vanished;
        }

        public String getName() {
            return name;
        }

        public String getServer() {
            return server;
        }

        public Rank getRank() {
            return rank;
        }

        public boolean isVanished() {
            return vanished;
        }
    }
}