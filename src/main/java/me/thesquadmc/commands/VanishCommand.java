package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.ItemBuilder;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VanishCommand implements CommandExecutor {

	private final Main main;

	public VanishCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.HELPER)) {
				if (!tempData.isVanished()) {
					PlayerUtils.hidePlayerSpectatorStaff(player);
					tempData.setVanished(true);
					if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
						player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK, 10).name("&e&lToggle Vanish &7off").lore("&7Toggle vanish on or off").build());
					}
					player.sendMessage(StringUtils.msg("&e&lVANISH &6■ &7You toggled vanish &eon&7! No one will be able to see you"));
				} else {
					PlayerUtils.showPlayerSpectator(player);
					tempData.setVanished(false);
					if (StaffmodeCommand.getStaffmode().containsKey(player.getUniqueId())) {
						player.getInventory().setItem(4, new ItemBuilder(Material.INK_SACK, 8).name("&e&lToggle Vanish &7on").lore("&7Toggle vanish on or off").build());
					}
					player.sendMessage(StringUtils.msg("&e&lVANISH &6■ &7You toggled vanish &eoff&7! Everyone will be able to see you"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
