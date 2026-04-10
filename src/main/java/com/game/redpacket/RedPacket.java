package com.game.redpacket;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Random;

/**
 * 红包抽象基类
 */
public abstract class RedPacket {
    protected int x, y;
    protected double amount;
    protected RedPacketSize size;
    protected RedPacketShape shape;
    protected Color color;
    protected double moveAngle;
    protected static final int BASE_SPEED = 3;
    protected int speed;
    protected Random random;
    protected boolean collected;
    protected long collectionTime;

    // 不同大小的基础尺寸
    protected static final int SMALL_SIZE = 30;
    protected static final int MEDIUM_SIZE = 50;
    protected static final int LARGE_SIZE = 70;

    public RedPacket(int x, int y, RedPacketSize size, RedPacketShape shape) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.shape = shape;
        this.random = new Random();
        this.collected = false;
        this.speed = (int) (BASE_SPEED * (1 + random.nextDouble() * 0.5)); // 速度有些随机变化

        // 根据大小设置金额范围
        double baseAmount = 0.1 + random.nextDouble() * 9.9;
        this.amount = baseAmount * size.getMultiplier();

        // 随机移动角度
        this.moveAngle = random.nextDouble() * 2 * Math.PI;

        // 根据金额设置颜色（金额越大颜色越深）
        int red = 255;
        int green = Math.max(50, 255 - (int)(amount * 5));
        int blue = Math.max(50, 255 - (int)(amount * 5));
        this.color = new Color(red, green, blue);
    }

    // 抽象方法，子类必须实现
    public abstract void draw(Graphics2D g);
    public abstract boolean contains(int px, int py);
    public abstract Area getShapeArea();
    public abstract int getWidth();
    public abstract int getHeight();

    // 公共方法
    public void move() {
        if (!collected) {
            x += (int) (speed * Math.cos(moveAngle));
            y += (int) (speed * Math.sin(moveAngle));
        }
    }

    public void checkBoundaryCollision(int panelWidth, int panelHeight) {
        if (collected) return;

        boolean collision = false;

        // 左边界碰撞
        if (x < 0) {
            x = 0;
            moveAngle = Math.PI - moveAngle;
            collision = true;
        }
        // 右边界碰撞
        else if (x + getWidth() > panelWidth) {
            x = panelWidth - getWidth();
            moveAngle = Math.PI - moveAngle;
            collision = true;
        }
        // 上边界碰撞
        if (y < 0) {
            y = 0;
            moveAngle = -moveAngle;
            collision = true;
        }
        // 下边界碰撞
        else if (y + getHeight() > panelHeight) {
            y = panelHeight - getHeight();
            moveAngle = -moveAngle;
            collision = true;
        }

        // 碰撞后给一个小的随机角度变化，避免卡在边界
        if (collision) {
            moveAngle += (random.nextDouble() - 0.5) * 0.5;
        }
    }

    public boolean checkCollisionWithTank(int tankX, int tankY, int tankWidth, int tankHeight) {
        if (collected) return false;

        Rectangle tankRect = new Rectangle(tankX, tankY, tankWidth, tankHeight);
        Area packetArea = getShapeArea();
        Area tankArea = new Area(tankRect);

        tankArea.intersect(packetArea);
        return !tankArea.isEmpty();
    }

    public void collect() {
        if (!collected) {
            collected = true;
            collectionTime = System.currentTimeMillis();
        }
    }

    public void reset(int panelWidth, int panelHeight) {
        this.x = random.nextInt(Math.max(1, panelWidth - getWidth()));
        this.y = random.nextInt(Math.max(1, panelHeight - getHeight()));
        this.collected = false;
        this.moveAngle = random.nextDouble() * 2 * Math.PI;

        // 重新生成金额
        double baseAmount = 0.1 + random.nextDouble() * 9.9;
        this.amount = baseAmount * size.getMultiplier();
    }

    // Getter 方法
    public int getX() { return x; }
    public int getY() { return y; }
    public double getAmount() { return amount; }
    public RedPacketSize getSize() { return size; }
    public RedPacketShape getShape() { return shape; }
    public boolean isCollected() { return collected; }
    public long getCollectionTime() { return collectionTime; }

    public String getDisplayInfo() {
        return String.format("%s%s红包: ¥%.2f", size.getDisplayName(), shape.getDisplayName(), amount);
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }
}