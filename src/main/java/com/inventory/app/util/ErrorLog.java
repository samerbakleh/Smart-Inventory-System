package com.inventory.app.util;

import com.inventory.app.model.ErrorLogEntry;
import com.inventory.app.model.enums.LogLevel;
import com.inventory.app.repository.ErrorLogRepository;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

public final class ErrorLog {

    private static volatile ErrorLogRepository repo;

    private static volatile boolean edtHookInstalled = false;

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

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                ErrorLog.log(LogLevel.ERROR, "Uncaught(" + thread.getName() + ")", "", throwable);
            }
        });

        installEdtEventQueueHookOnce();
    }

    private static void installEdtEventQueueHookOnce() {
        if (edtHookInstalled) return;
        synchronized (ErrorLog.class) {
            if (edtHookInstalled) return;

            Toolkit.getDefaultToolkit()
                    .getSystemEventQueue()
                    .push(new LoggingEventQueue());

            edtHookInstalled = true;
        }
    }

    private static final class LoggingEventQueue extends EventQueue {
        @Override
        protected void dispatchEvent(AWTEvent event) {
            try {
                super.dispatchEvent(event);
            } catch (Throwable t) {
                ErrorLog.log(LogLevel.ERROR, "EDT", "Exception in Swing event: " + event, t);

                if (t instanceof RuntimeException) throw (RuntimeException) t;
                if (t instanceof Error) throw (Error) t;
                throw new RuntimeException(t);
            }
        }
    }

    public static void runSafely(String context, Runnable action) {
        try {
            action.run();
        } catch (Throwable t) {
            ErrorLog.error(context, t);
        }
    }

    public static ActionListener safeAction(String context, ActionListener real) {
        return new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                runSafely(context, new Runnable() {
                    @Override
                    public void run() {
                        real.actionPerformed(e);
                    }
                });
            }
        };
    }
}
