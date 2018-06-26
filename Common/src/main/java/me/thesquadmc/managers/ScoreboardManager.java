package me.thesquadmc.managers;

import me.thesquadmc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ScoreboardManager {

    private Map<UUID, Scoreboard> scoreboardMap = new HashMap<>();

    public void join(Player player) {
        sendScoreboard(player);
        nametagLogin(player);
        updateNametag(player);
    }

    public void leave(Player player) {
        nametagLogout(player);
        scoreboardMap.remove(player.getUniqueId());
    }

    private void sendScoreboard(Player player) {
        //check if not null
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        if (scoreboard.getTeam(player.getName()) != null) {
            scoreboard.getTeam(player.getName()).unregister();
        }
        org.bukkit.scoreboard.Team prefix = scoreboard.registerNewTeam(player.getName());
        prefix.setPrefix(PlayerUtils.getRank(player).getTablistPrefix() + " ");
        prefix.setNameTagVisibility(NameTagVisibility.ALWAYS);
        prefix.addPlayer(player);
        player.setScoreboard(scoreboard);
        scoreboardMap.put(player.getUniqueId(), scoreboard);
    }

    private void nametagLogout(Player player) {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player1.getUniqueId() != player.getUniqueId()) {
                player1.getScoreboard().getTeam(player.getName()).unregister();
            }
        }
        for (Team team : player.getScoreboard().getTeams()) {
            team.unregister();
        }
    }

    public void updateNametag(Player player) {
        org.bukkit.scoreboard.Team team = scoreboardMap.get(player.getUniqueId()).getTeam(player.getName());
        team.setPrefix(PlayerUtils.getPlayerPrefix(player) + " ");
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player1.getUniqueId() != player.getUniqueId()) {
                org.bukkit.scoreboard.Team newTeam = player1.getScoreboard().getTeam(player.getName());
                newTeam.setPrefix(PlayerUtils.getRank(player).getTablistPrefix() + " ");
            }
        }
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player1.getUniqueId() != player.getUniqueId()) {
                org.bukkit.scoreboard.Team newTeam = player.getScoreboard().getTeam(player1.getName());
                newTeam.setPrefix(PlayerUtils.getRank(player1).getTablistPrefix() + " ");
            }
        }
    }

    private void nametagLogin(Player player) {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player1.getUniqueId() != player.getUniqueId()) {
                org.bukkit.scoreboard.Team newTeam = player1.getScoreboard().registerNewTeam(player.getName());
                newTeam.setPrefix(PlayerUtils.getRank(player).getTablistPrefix() + " ");
                newTeam.addPlayer(player);
                newTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
            }
        }
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (player1.getUniqueId() != player.getUniqueId()) {
                org.bukkit.scoreboard.Team newTeam = player.getScoreboard().registerNewTeam(player1.getName());
                newTeam.setPrefix(PlayerUtils.getRank(player1).getTablistPrefix() + " ");
                newTeam.addPlayer(player1);
                newTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
            }
        }
    }

}
