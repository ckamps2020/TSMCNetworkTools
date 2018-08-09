package com.thesquadmc.networktools.player.stats;

import com.thesquadmc.networktools.utils.server.ServerType;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Season {

    private final String season;
    private final Set<ServerStatistics> serverStatistics;

    public Season(String season) {
        this.season = season;
        this.serverStatistics = new HashSet<>();
    }

    public Season(String season, Set<ServerStatistics> serverStatistics) {
        this.season = season;
        this.serverStatistics = serverStatistics;
    }

    public static Season fromDocument(Document document) {
        List<Document> servers = (List<Document>) document.get("servers");

        System.out.println(servers);

        return new Season(
                document.getString("_id"),
                servers.stream().map(ServerStatistics::fromDocument).collect(Collectors.toSet())
        );
    }

    public String getSeason() {
        return season;
    }

    public Set<ServerStatistics> getServerStatistics() {
        return serverStatistics;
    }

    public Optional<ServerStatistics> getServerStatistic(String name) {
        return serverStatistics.stream()
                .filter(stats -> stats.getServerName().equalsIgnoreCase(name))
                .findFirst();
    }

    public Set<ServerStatistics> getServerStatistics(ServerType type) {
        return serverStatistics.stream()
                .filter(stats -> stats.getType() == type)
                .collect(Collectors.toSet());
    }

    public void addServerStatistic(ServerStatistics serverStats) {
        boolean s = serverStatistics.add(serverStats);
        System.out.println("Season add " + s);
    }

    public Document toDocument() {
        List<Document> servers = serverStatistics.stream().map(ServerStatistics::toDocument).collect(Collectors.toList());

        System.out.println(servers);

        return new Document("_id", season)
                .append("servers", servers);
    }
}
