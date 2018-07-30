package me.thesquadmc.objects.log;

import me.thesquadmc.objects.logs.LogType;

public class Log {

    private final LogType type;
    private final String message;
    private final String server;

    private final long timestamp = System.currentTimeMillis();

    private Log(LogType type, String message, String server) {
        this.type = type;
        this.message = message;
        this.server = server;
    }

    public static Log create(LogType type, String message, String server) {
        return new Log(type, message, server);
    }

    public LogType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getServer() {
        return server;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
