package me.thesquadmc.managers;

import me.thesquadmc.objects.Report;
import me.thesquadmc.utils.ReportType;

import java.util.ArrayList;
import java.util.List;

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

	public void newClosedReport(Report report) {
		closedReports.add(report);
	}

	public void removeReport(Report report) {
		reports.remove(report);
	}

	public void newReport(Report report) {
		reports.add(report);
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
