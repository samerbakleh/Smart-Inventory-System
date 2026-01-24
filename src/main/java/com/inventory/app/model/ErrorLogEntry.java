package com.inventory.app.model;

import com.inventory.app.model.enums.LogLevel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public final class ErrorLogEntry {

    private static final String TS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS Z";

    private final long timestampMillis;
    private final LogLevel level;
    private final String threadName;
    private final String context;
    private final String message;
    private final Throwable throwable;

    public ErrorLogEntry(LogLevel level, String context, String message, Throwable throwable) {
        this.timestampMillis = System.currentTimeMillis();
        this.level = Objects.requireNonNull(level, "level");
        this.threadName = Thread.currentThread().getName();
        this.context = context == null ? "" : context;
        this.message = message == null ? "" : message;
        this.throwable = throwable;
    }

    public String toText() {
        StringBuilder sb = new StringBuilder(256);

        String ts = new SimpleDateFormat(TS_PATTERN).format(new Date(timestampMillis));

        sb.append(ts)
                .append(" | ").append(level)
                .append(" | ").append(threadName)
                .append(" | ").append(sanitize(context))
                .append(" | ").append(sanitize(message));

        if (throwable != null) {
            sb.append(" | ").append(throwable.getClass().getName());
            String em = throwable.getMessage();
            if (em != null && !em.isBlank()) {
                sb.append(": ").append(sanitize(em));
            }
        }

        sb.append(System.lineSeparator());

        if (throwable != null) {
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            for (String line : sw.toString().split("\\R")) {
                sb.append("\t").append(line).append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    private static String sanitize(String s) {
        if (s == null) return "";
        return s.replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ')
                .trim();
    }
}
