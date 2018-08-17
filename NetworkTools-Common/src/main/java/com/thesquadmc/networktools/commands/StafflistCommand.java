package com.thesquadmc.networktools.commands;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.msgs.CC;
import com.thesquadmc.networktools.utils.msgs.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StafflistCommand implements CommandExecutor {

    private static Map<UUID, Map<String, String>> stafflist = new HashMap<>();
    private final NetworkTools networkTools;

    public StafflistCommand(NetworkTools networkTools) {
        this.networkTools = networkTools;
    }

    public static Map<UUID, Map<String, String>> getStafflist() {
        return stafflist;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            stafflist.put(player.getUniqueId(), new HashMap<>());
            networkTools.getRedisManager().sendMessage(RedisChannels.REQUEST_LIST, RedisMesage.newMessage()
                    .set(RedisArg.SERVER, Bukkit.getServerName())
                    .set(RedisArg.PLAYER, player.getName()));

            Bukkit.getScheduler().runTaskLater(networkTools, () -> {
                String trainee = "";
                String helper = "";
                String mod = "";
                String srmod = "";
                String admin = "";
                String manager = "";
                String developer = "";
                String owner = "";
                for (Map.Entry<String, String> m : stafflist.get(player.getUniqueId()).entrySet()) {
                    switch (m.getKey()) {
                        case RedisArg.TRAINEE:
                            trainee = m.getValue();
                            break;
                        case RedisArg.HELPER:
                            helper = m.getValue();
                            break;
                        case RedisArg.MOD:
                            mod = m.getValue();
                            break;
                        case RedisArg.SRMOD:
                            srmod = m.getValue();
                            break;
                        case RedisArg.ADMIN:
                            admin = m.getValue();
                            break;
                        case RedisArg.MANAGER:
                            manager = m.getValue();
                            break;
                        case RedisArg.DEVELOPER:
                            developer = m.getValue();
                            break;
                        case RedisArg.OWNER:
                            owner = m.getValue();
                            break;
                    }
                }
                String tr = "[ ]+";
                String hr = "[ ]+";
                String mr = "[ ]+";
                String srr = "[ ]+";
                String ar = "[ ]+";
                String manr = "[ ]+";
                String dr = "[ ]+";
                String or = "[ ]+";
                String[] ttokens = trainee.split(tr);
                String[] htokens = helper.split(hr);
                String[] mtokens = mod.split(mr);
                String[] srtokens = srmod.split(srr);
                String[] atokens = admin.split(ar);
                String[] mantokens = manager.split(manr);
                String[] dtokens = developer.split(dr);
                String[] otokens = owner.split(or);

                if (!trainee.equalsIgnoreCase("")) {
                    player.spigot().sendMessage(StringUtils.getHoverMessage("&a" + ttokens.length + " &a&lTRAINEE&a " + trainee, "&7Want to become &eTrainee&7? Apply at:\n" +
                            "&6&nwww.thesquadmc.net/forums/staff-applications"));
                } else {
                    player.sendMessage(CC.translate("&c0 &a&lTRAINEE&a &7None"));
                }
                if (!helper.equalsIgnoreCase("")) {
                    player.sendMessage(CC.translate("&a" + (htokens.length - 1) + " &b&lCHAT MOD&b" + helper));
                } else {
                    player.sendMessage(CC.translate("&c0 &b&lCHAT MOD&b &7None"));
                }
                if (!mod.equalsIgnoreCase("")) {
                    player.sendMessage(CC.translate("&a" + (mtokens.length - 1) + " &9&lMOD&9" + mod));
                } else {
                    player.sendMessage(CC.translate("&c0 &5&lMOD &7None"));
                }
                if (!srmod.equalsIgnoreCase("")) {
                    player.sendMessage(CC.translate("&a" + (srtokens.length - 1) + " &d&lSR MOD&d" + srmod));
                } else {
                    player.sendMessage(CC.translate("&c0 &d&lSR MOD &7None"));
                }
                if (!admin.equalsIgnoreCase("")) {
                    player.sendMessage(CC.translate("&a" + (atokens.length - 1) + " &c&lADMIN&c" + admin));
                } else {
                    player.sendMessage(CC.translate("&c0 &c&lADMIN&c &7None"));
                }
                if (!manager.equalsIgnoreCase("")) {
                    player.sendMessage(CC.translate("&a" + (mantokens.length - 1) + " &c&lMANAGER&c" + manager));
                } else {
                    player.sendMessage(CC.translate("&c0 &c&lMANAGER &7None"));
                }
                if (!developer.equalsIgnoreCase("")) {
                    player.sendMessage(CC.translate("&a" + (dtokens.length - 1) + " &c&lDEV&c" + developer));
                } else {
                    player.sendMessage(CC.translate("&c0 &c&lDEV &7None"));
                }
                if (!owner.equalsIgnoreCase("")) {
                    player.sendMessage(CC.translate("&a" + (otokens.length - 1) + " &c&lOWNER&c" + owner));
                } else {
                    player.sendMessage(CC.translate("&c0 &c&lOWNER&c &7None"));
                }
                //fetch online count
                stafflist.remove(player.getUniqueId());
            }, 5L);
        }
        return true;
    }

}
