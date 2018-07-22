package me.thesquadmc.managers;

import me.thesquadmc.utils.file.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ItemManager {

    private final FileConfiguration config;
    private final HashMap<ItemData, Set<String>> items = new HashMap<>();

    public ItemManager() {
        config = FileUtils.getConfig("items");

        load();
    }

    private void load() {
        for (String sID : config.getStringList("items")) {
            String[] split = sID.split(",");
            String name = split[0];
            int matID = Integer.parseInt(split[1]);
            short durability = split.length == 3 ? Short.parseShort(split[2]) : 0;

            ItemData data = new ItemData(matID, durability);
            Set<String> relatedNames = this.items.getOrDefault(data, new HashSet<>());
            relatedNames.add(name);
            this.items.put(data, relatedNames);
        }
    }

    public Optional<ItemStack> getItem(String name) {
        return getItem(name, 1);
    }

    /**
     * @param name for example wood or brail or stickyp or wood:2
     */
    public Optional<ItemStack> getItem(String name, int amt) {
        String compareName;
        short overrideDura = -1;
        if (name.contains(":")) {
            compareName = name.split(":")[0];
            overrideDura = Short.parseShort(name.split(":")[1]);
        } else
            compareName = name;
        Optional<Map.Entry<ItemData, Set<String>>> optItem = this.items.entrySet().stream().filter(entry -> entry.getValue().contains(compareName)).findFirst();

        if (optItem.isPresent()) {
            ItemData data = optItem.get().getKey();
            ItemStack is = new ItemStack(Material.getMaterial(data.getId()), amt, overrideDura == -1 ? data.getDurability() : overrideDura);
            return Optional.of(is);
        }

        return Optional.empty();
    }

    private class ItemData {

        private short durability;
        private int id;

        public ItemData(int id, short durability) {
            this.id = id;
            this.durability = durability;
        }

        public int getId() {
            return this.id;
        }

        public short getDurability() {
            return this.durability;
        }
    }
}
