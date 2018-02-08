package me.thesquadmc.managers;

import me.thesquadmc.Main;
import me.thesquadmc.networking.JedisTask;
import me.thesquadmc.objects.Report;
import me.thesquadmc.utils.enums.RedisArg;
import me.thesquadmc.utils.enums.RedisChannels;
import me.thesquadmc.utils.enums.ReportType;
import me.thesquadmc.utils.StringUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ReportManager {

	private List<Report> reports = new ArrayList<>();
	private List<Report> closedReports = new ArrayList<>();

	public void removeClosedReport(Report report) {
		closedReports.remove(report);
	}

	public Report getReportFromUUID(String uuid) {
		for (Report report : reports) {
			if (report.getReportID().toString().equalsIgnoreCase(uuid)) {
				return report;
			}
		}
		return null;
	}

	public void newClosedReport(String uuid, String name) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
			@Override
			public void run() {
				try (Jedis jedis = Main.getMain().getPool().getResource()) {
					JedisTask.withName(UUID.randomUUID().toString())
							.withArg(RedisArg.UUID.getArg(), uuid)
							.withArg(RedisArg.DATE.getArg(), StringUtils.getDate())
							.withArg(RedisArg.PLAYER.getArg(), name)
							.send(RedisChannels.CLOSED_REPORTS.getChannelName(), jedis);
				}
			}
		});
	}

	public void registerClosedReport(Report report) {
		closedReports.add(report);
	}

	public void removeReport(Report report) {
		reports.remove(report);
	}

	public void registerReport(Report report) {
		reports.add(report);
	}

	public void newReport(Report report) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : report.getReason()) {
			stringBuilder.append(" " + s);
		}
		Bukkit.getScheduler().runTaskAsynchronously(Main.getMain(), new Runnable() {
			@Override
			public void run() {
				try (Jedis jedis = Main.getMain().getPool().getResource()) {
					JedisTask.withName(UUID.randomUUID().toString())
							.withArg(RedisArg.PLAYER.getArg(), report.getUsername())
							.withArg(RedisArg.ORIGIN_PLAYER.getArg(), report.getReporter())
							.withArg(RedisArg.DATE.getArg(), report.getDate())
							.withArg(RedisArg.REASON.getArg(), stringBuilder.toString())
							.withArg(RedisArg.UUID.getArg(), report.getReportID().toString())
							.withArg(RedisArg.SERVER.getArg(), Bukkit.getServerName())
							.send(RedisChannels.REPORTS.getChannelName(), jedis);
				}
			}
		});
	}

	public List<Report> getReports() {
		return reports;
	}

	public List<Report> getClosedReports() {
		return closedReports;
	}

	public boolean isValidReportType(String type) {
		for (ReportType reports : ReportType.values()) {
			if (reports.getCheatType().equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}

}
