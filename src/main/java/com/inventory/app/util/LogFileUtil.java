package com.inventory.app.util;

import java.io.*;
import java.nio.charset.Charset;

public final class LogFileUtil {

    private LogFileUtil() {}

    public static String readTail(String filePath, int maxChars) throws IOException {
        File f = new File(filePath);
        if (!f.exists() || f.length() == 0) return "";

        long len = f.length();
        int bytesToRead = (int) Math.min(len, Math.max(1024, maxChars));

        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            long start = Math.max(0, len - bytesToRead);
            raf.seek(start);

            byte[] data = new byte[(int) (len - start)];
            raf.readFully(data);

            String text = new String(data, Charset.forName("UTF-8"));

            if (start > 0) {
                int nl = text.indexOf('\n');
                if (nl >= 0 && nl + 1 < text.length()) text = text.substring(nl + 1);
            }
            return text;
        }
    }

    public static void clear(String filePath) throws IOException {
        File f = new File(filePath);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (FileOutputStream fos = new FileOutputStream(f, false)) {
            // truncate
        }
    }

    public static long size(String filePath) {
        File f = new File(filePath);
        return f.exists() ? f.length() : 0L;
    }
}
