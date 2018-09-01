package com.thesquadmc.networktools.networking.redis.channels;

import com.google.gson.JsonObject;
import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.commands.StaffCommand;
import com.thesquadmc.networktools.networking.redis.RedisChannel;
import com.thesquadmc.networktools.networking.redis.RedisMesage;
import com.thesquadmc.networktools.player.PlayerSetting;
import com.thesquadmc.networktools.player.TSMCUser;
import com.thesquadmc.networktools.utils.enums.Rank;
import com.thesquadmc.networktools.utils.enums.RedisArg;
import com.thesquadmc.networktools.utils.enums.RedisChannels;
import com.thesquadmc.networktools.utils.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class FindChannel implements RedisChannel {

    private final NetworkTools plugin;

    public FindChannel(NetworkTools plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(String channel, JsonObject object) {
    }
}
