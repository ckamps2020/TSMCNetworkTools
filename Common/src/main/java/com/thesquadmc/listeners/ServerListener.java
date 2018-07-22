package com.thesquadmc.listeners;

import com.thesquadmc.NetworkTools;
import com.thesquadmc.networking.redis.RedisMesage;
import com.thesquadmc.utils.enums.RedisArg;
import com.thesquadmc.utils.enums.RedisChannels;
import com.thesquadmc.utils.enums.UpdateType;
import com.thesquadmc.utils.handlers.UpdateEvent;
import com.thesquadmc.utils.server.ServerUtils;
import com.thesquadmc.utils.time.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ServerListener implements Listener {

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getUpdateType() == UpdateType.TWO_MIN) {
            if (Double.valueOf(ServerUtils.getTPS(0)) <= 15.00) {
                NetworkTools.getInstance().getRedisManager().sendMessage(RedisChannels.MONITOR_INFO, RedisMesage.newMessage()
                        .set(RedisArg.SERVER, Bukkit.getServerName() + " ")
                        .set(RedisArg.UPTIME, TimeUtils.getFormattedTime(System.currentTimeMillis() - NetworkTools.getInstance().getStartup()))
                        .set(RedisArg.COUNT, String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .set(RedisArg.MESSAGE.getName(), String.format("&7TPS = &e%s&7, Memory = &e%s&8/&e%s", ServerUtils.getTPS(0), ServerUtils.getUsedMemory(), ServerUtils.getTotalMemory()))
                        .set(RedisArg.TPS, ServerUtils.getTPS(0))
                        .set(RedisArg.MEMORY, ServerUtils.getUsedMemory() + "/" + ServerUtils.getTotalMemory()));
            }
        }
    }

}
