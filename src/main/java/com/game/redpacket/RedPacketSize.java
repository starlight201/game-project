package com.game.redpacket;

/**
 * 红包大小枚举
 */
public enum RedPacketSize {
    SMALL("小", 1.0),
    MEDIUM("中", 2.0),
    LARGE("大", 5.0);

    private final String displayName;
    private final double multiplier;

    RedPacketSize(String displayName, double multiplier) {
        this.displayName = displayName;
        this.multiplier = multiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public static RedPacketSize fromName(String name) {
        for (RedPacketSize size : values()) {
            if (size.displayName.equals(name)) {
                return size;
            }
        }
        return SMALL;
    }
}