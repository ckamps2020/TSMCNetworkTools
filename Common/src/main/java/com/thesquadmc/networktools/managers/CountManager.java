package com.thesquadmc.networktools.managers;

import java.util.HashMap;
import java.util.Map;

public final class CountManager {

    private Map<String, Integer> count = new HashMap<>();

    public int getTotalOnlineCount() {
        int i = 0;
        for (Map.Entry<String, Integer> map : count.entrySet()) {
            i = i + map.getValue();
        }
        return i;
    }

    public int getFactionsCount() {
        int i = 0;
        for (Map.Entry<String, Integer> map : count.entrySet()) {
            if (map.getKey().toUpperCase().startsWith("FACTIONS")) {
                i = i + map.getValue();
            }
        }
        return i;
    }

    public int getSkyblockCount() {
        int i = 0;
        for (Map.Entry<String, Integer> map : count.entrySet()) {
            if (map.getKey().toUpperCase().startsWith("SKYBLOCK")) {
                i = i + map.getValue();
            }
        }
        return i;
    }

    public int getPrisonCount() {
        int i = 0;
        for (Map.Entry<String, Integer> map : count.entrySet()) {
            if (map.getKey().toUpperCase().startsWith("PRISON")) {
                i = i + map.getValue();
            }
        }
        return i;
    }

    public int getCreativeCount() {
        int i = 0;
        for (Map.Entry<String, Integer> map : count.entrySet()) {
            if (map.getKey().toUpperCase().startsWith("CREATIVE")) {
                i = i + map.getValue();
            }
        }
        return i;
    }

    public int getHubCount() {
        int i = 0;
        for (Map.Entry<String, Integer> map : count.entrySet()) {
            if (map.getKey().toUpperCase().startsWith("HUB")) {
                i = i + map.getValue();
            }
        }
        return i;
    }

    public boolean doesCountExist(String string) {
        return count.containsKey(string);
    }

    public int getCount(String string) {
        if (doesCountExist(string)) {
            return count.get(string);
        }
        return 0;
    }

    public Map<String, Integer> getCount() {
        return count;
    }

}
