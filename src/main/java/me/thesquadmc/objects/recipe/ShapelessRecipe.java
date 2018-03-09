package me.thesquadmc.objects.recipe;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ShapelessRecipe {

	private final ItemStack output;
	private final List<ItemStack> ingredients = new ArrayList<>();

	public ShapelessRecipe(ItemStack result) {
		output = new ItemStack(result);
	}

	public ShapelessRecipe addIngredient(Material mat) {
		return addIngredient(1, mat);
	}

	public ShapelessRecipe addIngredient(int count, Material mat) {
		return addIngredient(count, new ItemStack(mat, 1));
	}

	public ShapelessRecipe addIngredient(ItemStack item) {
		return addIngredient(1, item);
	}

	public ShapelessRecipe addIngredient(int count, ItemStack item) {
		Validate.isTrue(ingredients.size() + count <= 9, "Shapeless recipes cannot have more than 9 ingredients");

		while (count-- > 0)
			ingredients.add(item);

		return this;
	}

	public ShapelessRecipe removeIngredient(Material mat) {
		return removeIngredient(1, mat);
	}

	public ShapelessRecipe removeIngredient(int count, Material mat) {
		return removeIngredient(count, new ItemStack(mat, 1));
	}

	public ShapelessRecipe removeIngredient(ItemStack item) {
		return removeIngredient(1, item);
	}

	public ShapelessRecipe removeIngredient(int count, ItemStack item) {
		Iterator<ItemStack> iterator = ingredients.iterator();
		while (count > 0 && iterator.hasNext()) {
			ItemStack stack = iterator.next();
			if (stack.isSimilar(item)) {
				iterator.remove();
				count--;
			}
		}
		return this;
	}

	public void register() {
		org.bukkit.inventory.ShapelessRecipe sr = new org.bukkit.inventory.ShapelessRecipe(output);
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
