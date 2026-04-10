package com.game.redpacket;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * 方形红包
 */
public class SquareRedPacket extends RedPacket {
    private static final int CORNER_ARC = 15;

    public SquareRedPacket(int x, int y, RedPacketSize size) {
        super(x, y, size, RedPacketShape.SQUARE);
    }

    @Override
    public void draw(Graphics2D g) {
        if (collected) return;

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制红包主体（圆角矩形）
        g.setColor(color);
        g.fillRoundRect(x, y, getWidth(), getHeight(), CORNER_ARC, CORNER_ARC);

        // 绘制金色边框
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, getWidth(), getHeight(), CORNER_ARC, CORNER_ARC);

        // 绘制红包金额
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, getFontSize()));
        String amountStr = String.format("¥%.2f", amount);
        drawCenteredString(g, amountStr, x, y, getWidth(), getHeight());

        // 绘制形状标识
        g.setColor(new Color(255, 255, 200, 150));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        String shapeStr = "方";
        drawCenteredString(g, shapeStr, x, y + getHeight()/2, getWidth(), getHeight()/2);
    }

    @Override
    public boolean contains(int px, int py) {
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, getWidth(), getHeight(), CORNER_ARC, CORNER_ARC);
        return rect.contains(px, py);
    }

    @Override
    public Area getShapeArea() {
        RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, getWidth(), getHeight(), CORNER_ARC, CORNER_ARC);
        return new Area(rect);
    }

    @Override
    public int getWidth() {
        switch (size) {
            case SMALL: return SMALL_SIZE;
            case MEDIUM: return MEDIUM_SIZE;
            case LARGE: return LARGE_SIZE;
            default: return MEDIUM_SIZE;
        }
    }

    @Override
    public int getHeight() {
        return getWidth(); // 方形宽高相等
    }

    private int getFontSize() {
        switch (size) {
            case SMALL: return 10;
            case MEDIUM: return 12;
            case LARGE: return 14;
            default: return 12;
        }
    }

    private void drawCenteredString(Graphics2D g, String text, int x, int y, int width, int height) {
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, textX, textY);
    }
}