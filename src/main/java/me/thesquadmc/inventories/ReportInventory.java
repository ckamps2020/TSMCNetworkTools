package me.thesquadmc.inventories;

import me.thesquadmc.Main;
import me.thesquadmc.objects.Report;
import me.thesquadmc.utils.InventorySize;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.MathUtils;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public final class ReportInventory {

	private final Main main;
	private Map<UUID, List<Inventory>> map = new HashMap<>();

	public ReportInventory(Main main) {
		this.main = main;
	}

	public void buildManagerReportsMenu(Player player) {
		Inventory inv = Bukkit.createInventory(null, InventorySize.THREE_LINE.getSize(), "Report Manager");
		inv.setItem(11, new ItemBuilder(Material.DIAMOND_SWORD).name("&e&lOpen Reports").lore("&7View all open reports").build());
		inv.setItem(15, new ItemBuilder(Material.BARRIER).name("&e&lClosed Reports").lore("&7View all current closed reports").build());
		player.openInventory(inv);
	}

	public void buildReportsMenu(Player player) {
		int pages = MathUtils.getTotalPages(main.getReportManager().getReports().size());
		int loc = 0;
		List<Inventory> list = new ArrayList<>();
		boolean override = false;
		for (int i = 0; i < pages; i++) {
			Inventory inv = Bukkit.createInventory(null, InventorySize.SIX_LINE.getSize(), "Reports Page " + (i + 1));
			for (int l = loc; l < main.getReportManager().getReports().size(); l++) {
				if (override) {
					loc++;
					override = false;
					Report report = main.getReportManager().getReports().get(l);
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
					head.setDurability((short) 3);
					SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
					skullMeta.setOwner(report.getUsername());
					skullMeta.setDisplayName(StringUtils.msg("&e&l" + report.getUsername()));
					List<String> lore = new ArrayList<>();
					lore.add(StringUtils.msg("&8DATE OF REPORT: " + report.getDate()));
					lore.add(StringUtils.msg("&7"));
					if (Bukkit.getPlayer(report.getReporter()) != null) {
						lore.add(StringUtils.msg("&7Reported by: &a" + report.getReporter()));
					} else {
						lore.add(StringUtils.msg("&7Reported by: &c" + report.getReporter()));
					}
					lore.add(StringUtils.msg("&7Server: &e" + Bukkit.getServerName()));
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : report.getReason()) {
						stringBuilder.append(" " + s);
					}
					lore.add(StringUtils.msg("&7Reason(s):&e" + stringBuilder.toString()));
					lore.add(StringUtils.msg("&c&lRIGHT-CLICK TO CLOSE REPORT"));
					lore.add(StringUtils.msg("&cID " + report.getReportID()));
					skullMeta.setLore(lore);
					head.setItemMeta(skullMeta);
					inv.addItem(head);
				} else if (MathUtils.canContinue(loc)) {
					loc++;
					Report report = main.getReportManager().getReports().get(l);
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
					head.setDurability((short) 3);
					SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
					skullMeta.setOwner(report.getUsername());
					skullMeta.setDisplayName(StringUtils.msg("&e&l" + report.getUsername()));
					List<String> lore = new ArrayList<>();
					lore.add(StringUtils.msg("&8DATE OF REPORT: " + report.getDate()));
					lore.add(StringUtils.msg("&7"));
					if (Bukkit.getPlayer(report.getReporter()) != null) {
						lore.add(StringUtils.msg("&7Reported by: &a" + report.getReporter()));
					} else {
						lore.add(StringUtils.msg("&7Reported by: &c" + report.getReporter()));
					}
					lore.add(StringUtils.msg("&7Server: &e" + Bukkit.getServerName()));
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : report.getReason()) {
						stringBuilder.append(" " + s);
					}
					lore.add(StringUtils.msg("&7Reason(s):&e" + stringBuilder.toString()));
					lore.add(StringUtils.msg("&c&lRIGHT-CLICK TO CLOSE REPORT"));
					lore.add(StringUtils.msg("&cID " + report.getReportID()));
					skullMeta.setLore(lore);
					head.setItemMeta(skullMeta);
					inv.addItem(head);
				} else {
					override = true;
					inv.setItem(53, new ItemBuilder(Material.ARROW).name(String.valueOf(i + 2)).lore("&7Click to go to page &e" + (i + 2)).build());
					break;
				}
			}
			list.add(inv);
		}
		map.put(player.getUniqueId(), list);
		Inventory first = list.get(0);
		player.openInventory(first);
	}

	public void buildClosedReportsMenu(Player player) {
		int pages = MathUtils.getTotalPages(main.getReportManager().getClosedReports().size());
		int loc = 0;
		List<Inventory> list = new ArrayList<>();
		boolean override = false;
		for (int i = 0; i < pages; i++) {
			Inventory inv = Bukkit.createInventory(null, InventorySize.SIX_LINE.getSize(), "Closed Reports Page " + (i + 1));
			for (int l = loc; l < main.getReportManager().getClosedReports().size(); l++) {
				if (override) {
					override = false;
					loc++;
					//build item
					Report report = main.getReportManager().getClosedReports().get(l);
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
					head.setDurability((short) 3);
					SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
					skullMeta.setOwner(report.getUsername());
					skullMeta.setDisplayName(StringUtils.msg("&e&l" + report.getUsername()));
					List<String> lore = new ArrayList<>();
					lore.add(StringUtils.msg("&8TIME OF CLOSE: " + report.getCloseDate()));
					lore.add(StringUtils.msg("&7"));
					lore.add(StringUtils.msg("&7Original Report by: &e" + report.getReporter()));
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : report.getReason()) {
						stringBuilder.append(" " + s);
					}
					lore.add(StringUtils.msg("&7Reports Reason(s):&e" + stringBuilder.toString()));
					lore.add(StringUtils.msg("&7Report Closed by: &e" + report.getReportCloser()));
					skullMeta.setLore(lore);
					head.setItemMeta(skullMeta);
					inv.addItem(head);
				} else if (MathUtils.canContinue(loc)) {
					loc++;
					//build item
					Report report = main.getReportManager().getClosedReports().get(l);
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
					head.setDurability((short) 3);
					SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
					skullMeta.setOwner(report.getUsername());
					skullMeta.setDisplayName(StringUtils.msg("&e&l" + report.getUsername()));
					List<String> lore = new ArrayList<>();
					lore.add(StringUtils.msg("&8TIME OF CLOSE: " + report.getCloseDate()));
					lore.add(StringUtils.msg("&7"));
					lore.add(StringUtils.msg("&7Original Report by: &e" + report.getReporter()));
					StringBuilder stringBuilder = new StringBuilder();
					for (String s : report.getReason()) {
						stringBuilder.append(" " + s);
					}
					lore.add(StringUtils.msg("&7Reports Reason(s):&e" + stringBuilder.toString()));
					lore.add(StringUtils.msg("&7Report Closed by: &e" + report.getReportCloser()));
					skullMeta.setLore(lore);
					head.setItemMeta(skullMeta);
					inv.addItem(head);
				} else {
					override = true;
					inv.setItem(52, new ItemBuilder(Material.ARROW).name(String.valueOf(i + 2)).lore("&7Click to go to page &e" + (i + 2)).build());
					break;
				}
			}
			list.add(inv);
		}
		map.put(player.getUniqueId(), list);
		Inventory first = list.get(0);
		player.openInventory(first);
	}

	public Map<UUID, List<Inventory>> getMap() {
		return map;
	}

}
