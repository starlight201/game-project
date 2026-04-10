package com.game.redpacket;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

/**
 * 星形红包
 */
public class StarRedPacket extends RedPacket {

    public StarRedPacket(int x, int y, RedPacketSize size) {
        super(x, y, size, RedPacketShape.STAR);
    }

    @Override
    public void draw(Graphics2D g) {
        if (collected) return;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建星形路径
        Path2D star = createStar();

        // 绘制星形红包主体
        g.setColor(color);
        g.fill(star);

        // 绘制金色边框
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(2));
        g.draw(star);

        // 绘制红包金额
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, getFontSize()));
        String amountStr = String.format("¥%.2f", amount);
        drawCenteredString(g, amountStr);

        // 绘制闪烁效果
        if (System.currentTimeMillis() % 1000 < 500) {
            g.setColor(new Color(255, 255, 200, 100));
            g.fill(createInnerStar());
        }
    }

    @Override
    public boolean contains(int px, int py) {
        Path2D star = createStar();
        return star.contains(px, py);
    }

    @Override
    public Area getShapeArea() {
        return new Area(createStar());
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
        return getWidth();
    }

    private Path2D createStar() {
        Path2D star = new Path2D.Float();
        int width = getWidth();
        int height = getHeight();
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int outerRadius = Math.min(width, height) / 2;
        int innerRadius = outerRadius / 2;

        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 5 * i;
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            int px = (int) (centerX + Math.cos(angle) * radius);
            int py = (int) (centerY + Math.sin(angle) * radius);

            if (i == 0) {
                star.moveTo(px, py);
            } else {
                star.lineTo(px, py);
            }
        }
        star.closePath();

        return star;
    }

    private Path2D createInnerStar() {
        Path2D innerStar = new Path2D.Float();
        int width = getWidth() / 2;
        int height = getHeight() / 2;
        int centerX = x + getWidth() / 2;
        int centerY = y + getHeight() / 2;
        int outerRadius = Math.min(width, height) / 3;
        int innerRadius = outerRadius / 2;

        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 5 * i;
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            int px = (int) (centerX + Math.cos(angle) * radius);
            int py = (int) (centerY + Math.sin(angle) * radius);

            if (i == 0) {
                innerStar.moveTo(px, py);
            } else {
                innerStar.lineTo(px, py);
            }
        }
        innerStar.closePath();

        return innerStar;
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
}