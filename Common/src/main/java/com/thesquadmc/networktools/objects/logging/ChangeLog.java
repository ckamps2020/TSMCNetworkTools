package com.thesquadmc.networktools.objects.logging;

import com.thesquadmc.networktools.utils.enums.EnumUtil;
import com.thesquadmc.networktools.utils.time.TimeUtils;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.Objects;


public class ChangeLog {

    private final long creator;
    private final String log;

    private final Server type;
    private final Priority priority;

    private final long timestamp;

    public ChangeLog(long creator, Server type, Priority priority, String log, long timestamp) {
        this.creator = creator;
        this.type = type;
        this.priority = priority;
        this.log = log;
        this.timestamp = timestamp;
    }

    public ChangeLog(long creator, Server type, Priority priority, String log) {
        this(creator, type, priority, log, System.currentTimeMillis());
    }

    public static ChangeLog fromDocument(Document document) {
        return new ChangeLog(document.getLong("creator"),
                EnumUtil.getEnum(Server.class, document.getString("type")),
                EnumUtil.getEnum(Priority.class, document.getString("priority")),
                document.getString("log"),
                document.getLong("timestamp"));
    }

    public long getCreator() {
        return creator;
    }

    public Server getType() {
        return type;
    }

    public String getLog() {
        return log;
    }

    public Priority getPriority() {
        return priority;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return String.format(priority.getColor() + "[%s] %s " + ChatColor.GRAY + " - %s (%s)",
                priority.name(),
                type.getName(),
                log,
                TimeUtils.getFormattedTime(timestamp - System.currentTimeMillis()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ChangeLog)) {
            return false;
        }

        ChangeLog log = (ChangeLog) o;
        return creator == log.getCreator() &&
                timestamp == log.getTimestamp() &&
                type == log.getType() &&
                priority == log.getPriority() &&
                log.log.equals(log.getLog());
    }

    @Override
    public int hashCode() {
        return Objects.hash(creator, timestamp, type, priority, log);
    }

    public enum Priority {
        LOW(ChatColor.YELLOW),
        MEDIUM(ChatColor.GOLD),
        HIGH(ChatColor.RED);

        private final ChatColor color;

        Priority(ChatColor color) {
            this.color = color;
        }

        public ChatColor getColor() {
            return color;
        }
    }

    public enum Server {
        FACTIONS("Factions"),
        SKYBLOCK("SkyBlock"),
        CREATIVE("Creative"),
        PRISON("Prison"),
        MINIGAMES("Minigames"),
        TROLLWARS("TrollWars"),
        GLOBAL("Global");

        private final String name;


        Server(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}