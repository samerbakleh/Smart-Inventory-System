package com.inventory.app.exception;

public class ReachedMinLimitException extends Exception {
    public final int itemId;
    public final String itemName;
    public final double current;
    public final double need;
    public final double minLimit;
    public final double projected;

    public ReachedMinLimitException(int itemId, String itemName,
                                    double current, double need,
                                    double minLimit, double projected) {
        super("Reached min limit for '" + itemName + "' (id=" + itemId + "). " +
                "current=" + current + ", need=" + need +
                ", min=" + minLimit + ", projected=" + projected);
        this.itemId = itemId;
        this.itemName = itemName;
        this.current = current;
        this.need = need;
        this.minLimit = minLimit;
        this.projected = projected;
    }
}
