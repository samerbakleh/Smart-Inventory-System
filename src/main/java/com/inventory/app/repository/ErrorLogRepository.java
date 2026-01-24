package com.inventory.app.repository;

import com.inventory.app.model.ErrorLogEntry;

import java.io.*;

public class ErrorLogRepository {

    private final File logFile;
    private final long maxBytes;
    private final int backupCount;

    private final Object ioLock = new Object();

    public ErrorLogRepository(String logFilePath, long maxBytes, int backupCount) {
        this.logFile = new File(logFilePath);
        this.maxBytes = Math.max(0, maxBytes);
        this.backupCount = Math.max(0, backupCount);
    }


    public void append(ErrorLogEntry entry) {
        synchronized (ioLock) {
            try {
                ensureDir();

                if (maxBytes > 0 && backupCount > 0) {
                    rotateIfNeeded();
                }

                try (OutputStream os = new FileOutputStream(logFile, true);
                     OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                     BufferedWriter bw = new BufferedWriter(osw)) {

                    bw.write(entry.toText());
                    bw.flush();
                }

            } catch (Exception ignored) {
            }
        }
    }

    private void ensureDir() throws IOException {
        File parent = logFile.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean ok = parent.mkdirs();
            if (!ok && !parent.exists()) {
                throw new IOException("Failed to create directories: " + parent.getAbsolutePath());
            }
        }
    }

    private void rotateIfNeeded() throws IOException {
        if (!logFile.exists()) return;

        long size = logFile.length();
        if (size <= maxBytes) return;

        for (int i = backupCount; i >= 1; i--) {
            File target = new File(logFile.getAbsolutePath() + "." + i);
            File source = (i == 1) ? logFile : new File(logFile.getAbsolutePath() + "." + (i - 1));

            if (source.exists()) {
                if (target.exists() && !target.delete()) {
                    throw new IOException("Failed to delete old backup: " + target.getAbsolutePath());
                }

                boolean moved = source.renameTo(target);
                if (!moved) {
                    copyFile(source, target);
                    if (!source.delete()) {
                        throw new IOException("Failed to delete after copy: " + source.getAbsolutePath());
                    }
                }
            }
        }

        try (FileOutputStream fos = new FileOutputStream(logFile, false)) {
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {

            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            out.flush();
        }
    }
}
