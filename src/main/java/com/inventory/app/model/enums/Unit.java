package com.inventory.app.model.enums;

import java.util.Locale;

public enum Unit {
    PCS,
    KG, G,
    M, CM, MM,
    L, ML;

    public static Unit parse(String s) {
        if (s == null) return PCS;
        String t = s.trim();
        if (t.isEmpty()) return PCS;

        t = t.toUpperCase(Locale.ROOT);

        if (t.equals("PIECE") || t.equals("PIECES") || t.equals("COUNT")) return PCS;

        try {
            return Unit.valueOf(t);
        } catch (Exception e) {
            return PCS;
        }
    }
}
