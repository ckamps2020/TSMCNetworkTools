package me.thesquadmc.objects;

import java.util.UUID;

public final class Report {

	private String username;
	private String date;
	private String reporter;
	private String[] reason;
	private String closeDate;
	private int timeAlive;
	private String reportCloser;
	private UUID reportID;
	private String server;

	public Report(String username, String date, String reporter, String server, String... reason) {
		this.username = username;
		this.date = date;
		this.reporter = reporter;
		this.reason = reason;
		timeAlive = -1;
		reportCloser = "N/A";
		reportID = UUID.randomUUID();
		this.server = server;
	}

	public void setReportID(UUID reportID) {
		this.reportID = reportID;
	}

	public String getServer() {
		return server;
	}

	public UUID getReportID() {
		return reportID;
	}

	public String getReportCloser() {
		return reportCloser;
	}

	public void setReportCloser(String reportCloser) {
		this.reportCloser = reportCloser;
	}

	public int getTimeAlive() {
		return timeAlive;
	}

	public void setTimeAlive(int timeAlive) {
		this.timeAlive = timeAlive;
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	public String getUsername() {
		return username;
	}

	public String getDate() {
		return date;
	}

	public String getReporter() {
		return reporter;
	}

	public String[] getReason() {
		return reason;
	}

}
