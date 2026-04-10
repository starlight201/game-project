package com.game.redpacket;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

/**
 * 三角形红包
 */
public class TriangleRedPacket extends RedPacket {

    public TriangleRedPacket(int x, int y, RedPacketSize size) {
        super(x, y, size, RedPacketShape.TRIANGLE);
    }

    @Override
    public void draw(Graphics2D g) {
        if (collected) return;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建三角形路径
        Path2D triangle = createTriangle();

        // 绘制三角形红包主体
        g.setColor(color);
        g.fill(triangle);

        // 绘制金色边框
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(2));
        g.draw(triangle);

        // 绘制红包金额
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, getFontSize()));
        String amountStr = String.format("¥%.2f", amount);
        drawCenteredString(g, amountStr);

        // 绘制形状标识
        g.setColor(new Color(255, 255, 200, 150));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        String shapeStr = "三";
        drawShapeIdentifier(g, shapeStr);
    }

    @Override
    public boolean contains(int px, int py) {
        Path2D triangle = createTriangle();
        return triangle.contains(px, py);
    }

    @Override
    public Area getShapeArea() {
        return new Area(createTriangle());
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
        return getWidth(); // 等边三角形
    }

    private Path2D createTriangle() {
        Path2D triangle = new Path2D.Float();
        int width = getWidth();
        int height = getHeight();

        // 等边三角形的三个顶点
        int x1 = x + width / 2;  // 顶点
        int y1 = y;
        int x2 = x;               // 左下角
        int y2 = y + height;
        int x3 = x + width;      // 右下角
        int y3 = y + height;

        triangle.moveTo(x1, y1);
        triangle.lineTo(x2, y2);
        triangle.lineTo(x3, y3);
        triangle.closePath();

        return triangle;
    }

    private int getFontSize() {
        switch (size) {
            case SMALL: return 10;
            case MEDIUM: return 12;
            case LARGE: return 14;
            default: return 12;
        }
    }

    private void drawCenteredString(Graphics2D g, String text) {
        FontMetrics fm = g.getFontMetrics();
        int centerX = x + getWidth() / 2;
        int centerY = y + getHeight() / 2;

        int textX = centerX - fm.stringWidth(text) / 2;
        int textY = centerY + fm.getAscent() / 2 - fm.getHeight() / 4;

        g.drawString(text, textX, textY);
    }

    private void drawShapeIdentifier(Graphics2D g, String text) {
        FontMetrics fm = g.getFontMetrics();
        int centerX = x + getWidth() / 2;
        int bottomY = y + getHeight() - 5;

        int textX = centerX - fm.stringWidth(text) / 2;
        g.drawString(text, textX, bottomY);
    }
}