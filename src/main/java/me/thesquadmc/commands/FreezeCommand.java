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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public final class FreezeCommand implements CommandExecutor {

	private final Main main;
	private static Map<UUID, UUID> stafffrozen = new HashMap<>();
	private static List<UUID> frozen = new ArrayList<>();

	public FreezeCommand(Main main) {
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
					String name = args[0];
					Player t = Bukkit.getPlayer(name);
					if (t != null) {
						User u = main.getLuckPermsApi().getUser(t.getUniqueId());
						if (!main.hasPerm(u, "tools.staff.freeze")) {
							freezePlayer(t);
							t.sendMessage(StringUtils.msg("&c&lYou have been frozen by staff, do not log out at all. Please follow staffs instructions at all time"));
							frozen.add(t.getUniqueId());
							main.getFrozenInventory().buildFrozenInventory(t);
							player.sendMessage(StringUtils.msg("&e&lFREEZE &6■ &7You have frozen &e" + t.getName() + "&7!"));
						} else {
							player.sendMessage(StringUtils.msg("&cYou are not allowed to freeze another staff member!"));
						}
					} else {
						player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
					}
				} else {
					player.sendMessage(StringUtils.msg("&cUsage: /freeze <player>"));
				}
			} else {
				player.sendMessage(StringUtils.msg("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

	public static List<UUID> getFrozen() {
		return frozen;
	}

	private void freezePlayer(Player player) {
		player.setWalkSpeed(0);
		player.setFlySpeed(0);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200, false, false));
	}

}
