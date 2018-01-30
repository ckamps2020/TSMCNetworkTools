package me.thesquadmc.commands;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.InventorySize;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InvseeCommand implements CommandExecutor {

	private final Main main;

	public InvseeCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.invsee")) {
				if (args.length == 1) {
					Player t = Bukkit.getPlayer(args[0]);
					if (t != null) {
						buildInvseeMenu(player, t);
					} else {
						player.sendMessage(StringUtils.msg("&cThat player is offline or does not exist!"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /invsee <player>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

	private void buildInvseeMenu(Player player, Player target) {
		Inventory inventory = Bukkit.createInventory(null, InventorySize.SIX_LINE.getSize(), "INVSEE");
		int i = 0;
		for (ItemStack s : target.getInventory().getContents()) {
			if (s != null) {
				inventory.setItem(i, s);
				i++;
			}
		}
		if (target.getInventory().getHelmet() != null) {
			inventory.setItem(45, target.getInventory().getHelmet());
		} else {
			inventory.setItem(45, new ItemBuilder(Material.BARRIER).name("&cNo Head").build());
		}
		if (target.getInventory().getChestplate() != null) {
			inventory.setItem(46, target.getInventory().getChestplate());
		} else {
			inventory.setItem(46, new ItemBuilder(Material.BARRIER).name("&cNo Chestplate").build());
		}
		if (target.getInventory().getLeggings() != null) {
			inventory.setItem(47, target.getInventory().getLeggings());
		} else {
			inventory.setItem(47, new ItemBuilder(Material.BARRIER).name("&cNo Leggings").build());
		}
		if (target.getInventory().getBoots() != null) {
			inventory.setItem(48, target.getInventory().getBoots());
		} else {
			inventory.setItem(48, new ItemBuilder(Material.BARRIER).name("&cNo Boots").build());

		}
		for (int ii = 36; ii < 51; ii++) {
			if (ii != 45 && ii != 46 && ii != 47 && ii != 48) {
				inventory.setItem(ii, new ItemBuilder(Material.STAINED_GLASS_PANE, 7).build());
			}
		}
		inventory.setItem(51, new ItemBuilder(Material.IRON_CHESTPLATE).name("&e&lArmor Bar").lore("&7" + getArmorLevel(target) + "&8/&71").build());
		inventory.setItem(52, new ItemBuilder(Material.INK_SACK).data(1).name("&e&lHealth Bar").lore("&7" + target.getHealth() + "&8/&720.0").build());
		inventory.setItem(53, new ItemBuilder(Material.GRILLED_PORK).name("&e&lHunger Bar").lore("&7" + target.getFoodLevel() + "&8/&720").build());
		player.openInventory(inventory);
	}

	public double getArmorLevel(Player player) {
		org.bukkit.inventory.PlayerInventory inv = player.getInventory();
		ItemStack helmet = null;
		ItemStack boots = null;
		ItemStack chest = null;
		ItemStack pants = null;

		if(inv.getBoots() != null){boots = inv.getBoots();}
		if(inv.getBoots() == null){boots = new ItemStack(Material.LEATHER_BOOTS);}
		if (inv.getHelmet() != null){helmet = inv.getHelmet(); }
		if(inv.getHelmet() == null){helmet = new ItemStack(Material.LEATHER_HELMET);}
		if (inv.getChestplate() != null){chest = inv.getChestplate();}
		if(inv.getChestplate() == null){chest = new ItemStack(Material.LEATHER_CHESTPLATE);}
		if (inv.getLeggings() != null){pants = inv.getLeggings();}
		if(inv.getLeggings() == null){pants = new ItemStack(Material.LEATHER_LEGGINGS);}
		double red = 0.0;
		if (helmet.getType() == null || helmet.getType() == Material.AIR)red = red + 0.0;
		else if(helmet != null && helmet.getType() == Material.LEATHER_HELMET)red = red + 0.04;
		else if(helmet != null && helmet.getType() == Material.GOLD_HELMET)red = red + 0.08;
		else if(helmet != null && helmet.getType() == Material.CHAINMAIL_HELMET)red = red + 0.08;
		else if(helmet != null && helmet.getType() == Material.IRON_HELMET)red = red + 0.08;
		else if(helmet != null && helmet.getType() == Material.DIAMOND_HELMET)red = red + 0.12;
		//
		if (boots.getType() == null || boots.getType() == Material.AIR)red = red + 0;
		else if(boots != null && boots.getType() == Material.LEATHER_BOOTS)red = red + 0.04;
		else if(boots != null && boots.getType() == Material.GOLD_BOOTS)red = red + 0.04;
		else if(boots != null && boots.getType() == Material.CHAINMAIL_BOOTS)red = red + 0.04;
		else if(boots != null && boots.getType() == Material.IRON_BOOTS)red = red + 0.08;
		else if(boots != null && boots.getType() == Material.DIAMOND_BOOTS)red = red + 0.12;
		//
		if (pants.getType() == null || pants.getType() == Material.AIR)red = red + 0;
		else if(pants != null && pants.getType() == Material.LEATHER_LEGGINGS)red = red + 0.08;
		else if(pants != null && pants.getType() == Material.GOLD_LEGGINGS)red = red + 0.12;
		else if(pants != null && pants.getType() == Material.CHAINMAIL_LEGGINGS)red = red + 0.16;
		else if(pants != null && pants.getType() == Material.IRON_LEGGINGS)red = red + 0.20;
		else if(pants != null && pants.getType() == Material.DIAMOND_LEGGINGS)red = red + 0.24;
		//
		if (chest.getType() == null || chest.getType() == Material.AIR)red = red + 0;
		else if(chest != null && chest.getType() == Material.LEATHER_CHESTPLATE)red = red + 0.12;
		else if(chest != null && chest.getType() == Material.GOLD_CHESTPLATE)red = red + 0.20;
		else if(chest != null && chest.getType() == Material.CHAINMAIL_CHESTPLATE)red = red + 0.20;
		else if(chest != null && chest.getType() == Material.IRON_CHESTPLATE)red = red + 0.24;
		else if(chest != null && chest.getType() == Material.DIAMOND_CHESTPLATE)red = red + 0.32;
		return red;
	}

}
