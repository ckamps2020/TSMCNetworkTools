package me.thesquadmc.utils.player;

import org.bukkit.entity.Player;

/**
 * Credits to a a plugin
 */
public class ExpUtil {

    private static double xplevel;
    private static int xpe;
    private static int result;

    private static void setXpLevel(final int level, final float cExp) {
        if (level > 30) {
            ExpUtil.xplevel = 4.5 * level * level - 162.5 * level + 2220.0;
            ExpUtil.xpe = 9 * level - 158;
            ExpUtil.xplevel += Math.round(cExp * ExpUtil.xpe);
            ExpUtil.result = (int) ExpUtil.xplevel;
            return;
        }
        if (level > 15) {
            ExpUtil.xplevel = 2.5 * level * level - 40.5 * level + 360.0;
            ExpUtil.xpe = 5 * level - 38;
            ExpUtil.xplevel += Math.round(cExp * ExpUtil.xpe);
            ExpUtil.result = (int) ExpUtil.xplevel;
            return;
        }

        ExpUtil.xplevel = level * level + 6 * level;
        ExpUtil.xpe = 2 * level + 7;
        ExpUtil.xplevel += Math.round(cExp * ExpUtil.xpe);
        ExpUtil.result = (int) ExpUtil.xplevel;
    }

    public static int getXp(final Player p) {
        setXpLevel(p.getLevel(), p.getExp());
        return ExpUtil.result;
    }
}
