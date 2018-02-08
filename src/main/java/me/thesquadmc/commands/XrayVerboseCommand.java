package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class XrayVerboseCommand implements CommandExecutor {

	private final Main main;

	public XrayVerboseCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				if (Bukkit.getServerName().toUpperCase().contains("HUB")) {
					player.sendMessage(StringUtils.msg("&cYou are not allowed to use this command here!"));
					return true;
				}
				if (!tempData.isXray()) {
					tempData.setXray(true);
					player.sendMessage(StringUtils.msg("&8[&4&lAntiCheat&8] &4[XRAY] &fYou enabled Xray Verbose"));
				} else {
					tempData.setXray(false);
					player.sendMessage(StringUtils.msg("&8[&4&lAntiCheat&8] &4[XRAY] &fYou disabled Xray Verbose"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
