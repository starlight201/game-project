package com.game.redpacket;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 玩家控制的坦克类
 */
public class Tank {
    private int x, y;
    private int width = 60;
    private int height = 40;
    private int speed = 5;
    private double angle = 0; // 坦克角度（弧度）

    // 移动控制
    private boolean movingForward = false;
    private boolean movingBackward = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // 坦克状态
    private boolean isHit = false;
    private long hitStartTime = 0;
    private static final long HIT_DURATION = 300; // 击中效果持续时间

    // 坦克图像
    private Image tankImage;
    private Color tankColor = new Color(70, 130, 180); // 钢蓝色

    // 碰撞检测边界
    private Rectangle collisionBounds;

    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
        this.collisionBounds = new Rectangle(x, y, width, height);
        loadTankImage();
    }

    /**
     * 从本地路径加载坦克图像
     */
    private void loadTankImage() {
        try {
            // 尝试从指定路径加载图像
            File imageFile = new File("E:\\javaproj\\game-project\\src\\main\\resources\\images\\dressup\\唐小鸭一号一号_20251211_155434.png");
            if (imageFile.exists()) {
                BufferedImage loadedImage = ImageIO.read(imageFile);
                // 调整图像大小以匹配坦克尺寸
                Image scaledImage = loadedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                this.tankImage = scaledImage;
            } else {
                // 如果图像文件不存在，则创建默认图像
                createDefaultTankImage();
            }
        } catch (IOException e) {
            System.out.println("无法加载坦克图像，使用默认图像: " + e.getMessage());
            createDefaultTankImage();
        }
    }

    /**
     * 创建默认坦克图像（备用方案）
     */
    private void createDefaultTankImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制坦克主体
        g2d.setColor(tankColor);
        g2d.fillRoundRect(0, 0, width, height, 15, 15);

        // 绘制坦克履带
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(5, 5, width - 10, 5);  // 上履带
        g2d.fillRect(5, height - 10, width - 10, 5); // 下履带

        // 绘制坦克炮塔
        g2d.setColor(new Color(50, 100, 150));
        g2d.fillOval(width/2 - 15, height/2 - 15, 30, 30);

        // 绘制坦克炮管
        g2d.setColor(Color.BLACK);
        g2d.fillRect(width/2, height/2 - 3, 25, 6);

        // 绘制坦克标识
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("TANK", width/2 - 15, height/2 + 5);

        g2d.dispose();
        this.tankImage = image;
    }

    /**
     * 更新坦克状态
     */
    public void update() {

        // 处理移动
        if (movingForward) {
            moveForward();
        }
        if (movingBackward) {
            moveBackward();
        }
        if (movingLeft) {
            moveLeft();  // 新增左移方法
        }
        if (movingRight) {
            moveRight(); // 新增右移方法
        }

        // 更新碰撞边界
        updateCollisionBounds();
        updateHitStatus();
    }

    private void moveForward() {
        y -= speed;
    }

    private void moveBackward() {
        y += speed;
    }
    // 添加左右移动方法
    private void moveLeft() {
        x -= speed;
    }

    private void moveRight() {
        x += speed;
    }

    private void updateCollisionBounds() {
        collisionBounds.setLocation(x, y);
    }

    private void updateHitStatus() {
        if (isHit && System.currentTimeMillis() - hitStartTime > HIT_DURATION) {
            isHit = false;
        }
    }

    /**
     * 处理键盘按下事件
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                movingForward = true;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                movingBackward = true;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                movingLeft = true;  // 改为左移
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                movingRight = true;  // 改为右移
                break;
        }
    }

    /**
     * 处理键盘释放事件
     */
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                movingForward = false;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                movingBackward = false;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                movingLeft = false;   // 改为左移
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                movingRight = false;  // 改为右移
                break;
        }
    }

    /**
     * 绘制坦克
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 应用旋转变换
        AffineTransform oldTransform = g2d.getTransform();
        g2d.rotate(angle, x + width/2, y + height/2);

        // 绘制坦克图像
        if (tankImage != null) {
            g2d.drawImage(tankImage, x, y, null);
        } else {
            // 如果图像为空，绘制默认形状
            g2d.setColor(tankColor);
            g2d.fillRoundRect(x, y, width, height, 15, 15);
        }

        // 如果被击中，绘制红色覆盖层
        if (isHit) {
            g2d.setColor(new Color(255, 0, 0, 100));
            g2d.fillRoundRect(x, y, width, height, 15, 15);
        }

        g2d.setTransform(oldTransform);

        // 调试模式下绘制碰撞边界
        if (false) { // 设置为true可显示碰撞边界
            g2d.setColor(Color.RED);
            g2d.drawRect(collisionBounds.x, collisionBounds.y,
                    collisionBounds.width, collisionBounds.height);
        }

        g2d.dispose();
    }

    /**
     * 检查边界碰撞
     */
    public void checkBoundaryCollision(int panelWidth, int panelHeight) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > panelWidth) x = panelWidth - width;
        if (y + height > panelHeight) y = panelHeight - height;

        updateCollisionBounds();
    }

    /**
     * 检查与红包的碰撞
     */
    public boolean checkCollision(RedPacket packet) {
        if (packet.isCollected()) {
            return false;
        }

        // 使用红包的精确碰撞检测
        return packet.checkCollisionWithTank(x, y, width, height);
    }

    /**
     * 设置被击中状态
     */
    public void setHit(boolean hit) {
        if (hit && !isHit) {
            isHit = true;
            hitStartTime = System.currentTimeMillis();
        } else if (!hit) {
            isHit = false;
        }
    }

    // Getter 方法
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public double getAngle() { return angle; }
    public Rectangle getCollisionBounds() { return new Rectangle(collisionBounds); }
    public boolean isMoving() { return movingForward || movingBackward || movingLeft || movingRight; }
    public boolean isHit() { return isHit; }

    /**
     * 设置坦克位置
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        updateCollisionBounds();
    }

    /**
     * 重置坦克状态
     */
    public void reset() {
        resetMovementState();  // 使用新的重置方法
        isHit = false;
        angle = 0;
    }

    @Override
    public String toString() {
        return String.format("Tank[位置:(%d,%d), 角度:%.2f, 移动:%s, 被击中:%s]",
                x, y, Math.toDegrees(angle), isMoving(), isHit);
    }
    public void resetMovementState() {
        movingForward = false;
        movingBackward = false;
        movingLeft = false;
        movingRight = false;
    }
}