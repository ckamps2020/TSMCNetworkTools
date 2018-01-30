package me.thesquadmc.commands;

import me.lucko.luckperms.api.User;
import me.thesquadmc.Main;
import me.thesquadmc.objects.TempData;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class FreezePanelCommand implements CommandExecutor {

	private final Main main;

	public FreezePanelCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			User user = main.getLuckPermsApi().getUser(player.getUniqueId());
			TempData tempData = main.getTempDataManager().getTempData(player.getUniqueId());
			if (main.hasPerm(user, "tools.staff.freeze")) {
				if (args.length == 1) {
					Player t = Bukkit.getPlayer(args[0]);
					if (t != null) {
						if (FreezeCommand.getFrozen().contains(t.getUniqueId())) {
							main.getFrozenInventory().buildStaffGUI(player, t);
							main.getFrozenInventory().getViewing().put(player.getUniqueId(), t.getUniqueId());
						} else {
							player.sendMessage(StringUtils.msg("&cThat player is not frozen!"));
						}
					} else {
						player.sendMessage(StringUtils.msg("&cThat player does not exist or is offline!"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /freezepanel <player>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
