package com.inventory.app.util;

import com.inventory.app.model.ErrorLogEntry;
import com.inventory.app.model.enums.LogLevel;
import com.inventory.app.repository.ErrorLogRepository;

import java.awt.event.ActionListener;

public final class ErrorLog {

    private static volatile ErrorLogRepository repo;

    private ErrorLog() {}

    private static void initIfNeeded() {
        if (repo != null) return;
        synchronized (ErrorLog.class) {
            if (repo != null) return;
            repo = new ErrorLogRepository(AppConstants.LOG_FILE, 1_000_000L, 3);
        }
    }

    public static void info(String context, String message) {
        log(LogLevel.INFO, context, message, null);
    }

    public static void warn(String context, String message) {
        log(LogLevel.WARN, context, message, null);
    }

    public static void error(String context, Throwable t) {
        String msg = (t == null || t.getMessage() == null) ? "" : t.getMessage();
        log(LogLevel.ERROR, context, msg, t);
    }

    public static void log(LogLevel level, String context, String message, Throwable t) {
        initIfNeeded();
        repo.append(new ErrorLogEntry(level, context, message, t));
    }

    public static void installGlobalHandlers() {
        initIfNeeded();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
                ErrorLog.log(LogLevel.ERROR, "Uncaught(" + thread.getName() + ")", "", throwable)
        );

    }

    public static void runSafely(String context, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            ErrorLog.error(context, t);
        }
    }

    public static ActionListener safeAction(String context, java.awt.event.ActionListener real) {
        return e -> runSafely(context, () -> real.actionPerformed(e));
    }
}
