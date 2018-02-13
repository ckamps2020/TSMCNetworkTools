package me.thesquadmc.commands;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.utils.PlayerUtils;
import me.thesquadmc.utils.enums.Rank;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.msgs.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public final class ProxyTransportCommand implements CommandExecutor {

	private final Main main;

	public ProxyTransportCommand(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (PlayerUtils.isEqualOrHigherThen(player, Rank.TRAINEE)) {
				if (args.length == 1) {
					String server = args[0];
					player.sendMessage(CC.translate("&e&lREPORT &6■ &7Attempting to send you to &e" + server + "&7..."));
					try (Jedis jedis = main.getPool().getResource()) {
						JedisTask.withName(UUID.randomUUID().toString())
								.withArg(RedisArg.PLAYER.getArg(), player.getName())
								.withArg(RedisArg.SERVER.getArg(), server)
								.send(RedisChannels.TRANSPORT.getChannelName(), jedis);
					}
				}
			} else {
				player.sendMessage(CC.translate("&cYou do not have permission to use this command!"));
			}
		}
		return true;
	}

}
