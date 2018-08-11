package com.thesquadmc.networktools.utils.nms;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.abstraction.BossBarManager;
import org.bukkit.entity.Player;

import java.util.Set;

public final class BarUtils {

    private static final BossBarManager BOSS_BAR_MANAGER = NetworkTools.getInstance().getNMSAbstract().getBossBarManager();

    public static void setBar(Player p, String text, float healthPercent) {
        BOSS_BAR_MANAGER.setBar(p, text, healthPercent);
    }

    public static void removeBar(Player p) {
        BOSS_BAR_MANAGER.removeBar(p);
    }

    public static boolean hasBar(Player p) {
        return BOSS_BAR_MANAGER.hasBar(p);
    }

    public static void teleportBar(Player p) {
        BOSS_BAR_MANAGER.teleportBar(p);
    }

    public static void updateText(Player p, String text) {
        BOSS_BAR_MANAGER.updateText(p, text);
    }

    public static void updateHealth(Player p, float healthPercent) {
        BOSS_BAR_MANAGER.updateHealth(p, healthPercent);
    }

    public static void updateBar(Player p, String text, float healthPercent) {
        BOSS_BAR_MANAGER.updateBar(p, text, healthPercent);
    }

    public static Set<Player> getPlayers() {
        return BOSS_BAR_MANAGER.getPlayers();
    }

}
