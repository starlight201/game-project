package com.game.redpacket;

/**
 * 红包形状枚举
 */
public enum RedPacketShape {
    SQUARE("方形"),
    CIRCLE("圆形"),
    TRIANGLE("三角形"),
    STAR("星形");

    private final String displayName;

    RedPacketShape(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RedPacketShape fromName(String name) {
        for (RedPacketShape shape : values()) {
            if (shape.displayName.equals(name)) {
                return shape;
            }
        }
        return SQUARE;
    }
}