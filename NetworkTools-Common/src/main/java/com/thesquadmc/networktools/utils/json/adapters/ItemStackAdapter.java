package com.thesquadmc.networktools.utils.json.adapters;

import com.google.common.primitives.Ints;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.thesquadmc.networktools.utils.enums.EnumUtil;
import com.thesquadmc.networktools.utils.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItemStackAdapter extends TypeAdapter<ItemStack> {

    @Override
    public void write(JsonWriter jsonWriter, ItemStack itemStack) throws IOException {
        if (itemStack == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.beginObject();
        jsonWriter.name("material").value(itemStack.getType().name());
        jsonWriter.name("amount").value(itemStack.getAmount());
        jsonWriter.name("durability").value(itemStack.getDurability());

        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();

            if (meta.hasDisplayName()) {
                jsonWriter.name("display_name").value(meta.getDisplayName());
            }

            if (meta.spigot().isUnbreakable()) {
                jsonWriter.name("unbreakable").value(true);
            }

            if (meta.hasEnchants()) {
                jsonWriter.name("enchants").value(toStringEnchants(itemStack.getEnchantments()));
            }
        }
        jsonWriter.endObject();
    }

    @Override
    public ItemStack read(JsonReader reader) throws IOException {
        ItemBuilder builder = new ItemBuilder(Material.AIR);

        reader.beginObject();
        String fieldname = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = reader.nextName();
            }

            if ("material".equals(fieldname)) {
                reader.peek();

                Material material = EnumUtil.getEnum(Material.class, reader.nextString());
                builder.type(material);
            }

            if ("amount".equals(fieldname)) {
                reader.peek();
                builder.amount(reader.nextInt());
            }

            if ("durability".equals(fieldname)) {
                reader.peek();
                builder.durability(reader.nextInt());
            }

            if ("display_name".equals(fieldname)) {
                reader.peek();
                builder.name(reader.nextString());
            }

            if ("unbreakable".equals(fieldname)) {
                reader.peek();
                builder.unbreakable(reader.nextBoolean());
            }

            if ("enchants".equals(fieldname)) {
                reader.peek();
                Map<Enchantment, Integer> enchants = fromStringEnchants(reader.nextString());

                enchants.forEach(builder::enchantment);
            }
        }

        reader.endObject();
        return builder.build();
    }

    private String toStringEnchants(Map<Enchantment, Integer> enchantments) {
        StringBuilder enchants = new StringBuilder();
        enchantments.forEach((enchantment, level) -> enchants.append(enchantment.getName())
                .append(":")
                .append(level)
                .append(";"));

        return enchants.toString();
    }

    //TODO Figure out how to make this look a lot better but it works for now
    private Map<Enchantment, Integer> fromStringEnchants(String string) {
        Map<Enchantment, Integer> enchamtments = new HashMap<>();

        string = string.replace("{", "");
        string = string.replace("}", "");


        if (string.isEmpty()) return enchamtments;

        String[] partials = string.split(";");
        for (String partial : partials) {
            String enchant = partial.split(":")[0];
            Integer level = Ints.tryParse(partial.split(":")[1]);

            Enchantment enchantment = Enchantment.getByName(enchant);
            if (enchantment == null || level == null) {
                continue;
            }

            enchamtments.put(enchantment, level);
        }

        return enchamtments;
    }
}