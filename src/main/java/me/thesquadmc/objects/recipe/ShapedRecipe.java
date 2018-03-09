package me.thesquadmc.objects.recipe;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ShapedRecipe {

	private ItemStack output;
	private String[] rows;
	private Map<Character, ItemStack> ingredients = new HashMap<>();

	public ShapedRecipe(ItemStack result) {
		output = new ItemStack(result);
	}

	public ShapedRecipe shape(String... shape) {

		Validate.notNull(shape, "Must provide a shape");
		Validate.isTrue(shape.length > 0 && shape.length < 4, "Crafting recipes should be 1, 2, 3 rows, not ",
				shape.length);

		for (String row : shape) {
			Validate.notNull(row, "Shape cannot have null rows");
			Validate.isTrue(row.length() > 0 && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ",
					row.length());
			Map<Character, ItemStack> newIng = new HashMap<>();
			for (char c : row.toCharArray())
				newIng.put(c, ingredients.get(c));
			ingredients = newIng;
		}

		rows = shape;

		return this;
	}

	public ShapedRecipe setIngredient(char key, Material mat) {
		return setIngredient(key, new ItemStack(mat, 1));
	}

	public ShapedRecipe setIngredient(char key, ItemStack item) {
		Validate.isTrue(ingredients.containsKey(Character.valueOf(key)), "Symbol does not appear in the shape:", key);
		ingredients.put(key, item);
		return this;
	}

	public void register() {
		org.bukkit.inventory.ShapedRecipe sr = new org.bukkit.inventory.ShapedRecipe(output);
		sr.shape(rows);
		try {
			Field f = sr.getClass().getDeclaredField("ingredients");
			f.setAccessible(true);
			f.set(sr, ingredients);
			Bukkit.addRecipe(sr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
