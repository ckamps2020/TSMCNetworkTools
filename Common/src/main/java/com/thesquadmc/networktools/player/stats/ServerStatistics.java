package com.thesquadmc.networktools.player.stats;

import com.thesquadmc.networktools.NetworkTools;
import com.thesquadmc.networktools.utils.enums.EnumUtil;
import com.thesquadmc.networktools.utils.server.ServerType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistics for each server
 */
public class ServerStatistics {

    private final String serverName;
    private final ServerType type;

    private Map<Stat<?>, Object> data;

    public ServerStatistics(String serverName, ServerType type) {
        this.serverName = serverName;
        this.type = type;
        this.data = new HashMap<>();
    }

    public ServerStatistics(String serverName, ServerType type, Map<Stat<?>, Object> data) {
        this.serverName = serverName;
        this.type = type;
        this.data = data;
    }

    public String getServerName() {
        return serverName;
    }

    public ServerType getType() {
        return type;
    }

    public static ServerStatistics fromDocument(Document document) {
        String name = document.getString("_id");
        ServerType type = EnumUtil.getEnum(ServerType.class, document.getString("server_type"));
        Map<String, Object> statistics = (Map<String, Object>) document.get("data");

        Map<Stat<?>, Object> stats = new HashMap<>();
        statistics.forEach((s, o) -> {
            Stat<?> stat = Stat.valueOf(s);
            if (stat == null) {
                NetworkTools.getInstance().getLogger().info(s + " could not be parsed as a Stat!");
                return;
            }

            stats.put(stat, o);
        });

        return new ServerStatistics(name, type, stats);
    }

    public <T> T updateStat(Stat<T> stat, T value) {
        return stat.getStatsType().cast(data.put(stat, value));
    }

    public <T> T getStat(Stat<T> stat) {
        return (stat != null) ? stat.getStatsType()
                .cast(data.getOrDefault(stat, stat.getDefaultValue())) : null;
    }

    public void overrideStats(Map<Stat<?>, Object> data) {
        this.data = data;
    }

    public void resetStatsToDefault() {
        this.data.clear();
        for (Stat<?> stat : Stat.VALUES) {
            this.data.put(stat, stat.getDefaultValue());
        }
    }

    public Document toDocument() {
        Map<String, Object> data = new HashMap<>();
        this.data.forEach((stat, o) -> data.put(stat.getName(), o));

        return new Document("_id", serverName)
                .append("server_type", type.name())
                .append("data", data);
    }
}
