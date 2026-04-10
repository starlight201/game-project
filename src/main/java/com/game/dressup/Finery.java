package com.game.dressup;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 服饰装饰器基类
 * 装饰器模式实现
 */
public abstract class Finery implements DressUpItem {
    protected String name;
    protected String description;
    protected double price;
    protected int stylePoints;
    protected String imagePath;
    protected String type;
    protected boolean owned = false;
    protected int unlockLevel = 1;
    // 新增：装饰图像自身的绘制偏移（相对于其左上角）
    // 例如：帽子希望底部中心对齐锚点，则 drawOffset = (width/2, height)
    protected Point drawOffset = new Point(0, 0); // 默认左上角对齐

    public Finery(String name, String description, double price, int stylePoints,
                  String imagePath, String type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stylePoints = stylePoints;
        this.imagePath = imagePath;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getStylePoints() {
        return stylePoints;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean isOwned() {
        return owned;
    }

    @Override
    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    @Override
    public int getUnlockLevel() {
        return unlockLevel;
    }

    public void setUnlockLevel(int unlockLevel) {
        this.unlockLevel = unlockLevel;
    }

    /**
     * 应用装扮到图片
     */
    @Override
    public Image applyToImage(Image baseImage) {
        try {
            Image accessoryImage = loadAccessoryImage();
            if (accessoryImage == null) return baseImage;

            BufferedImage combined = new BufferedImage(
                    baseImage.getWidth(null),
                    baseImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = combined.createGraphics();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2d.drawImage(baseImage, 0, 0, null);

            // 获取当前角色的锚点（需从上下文获取 currentDuck）
            Person duck = DressUpManager.getInstance().getCurrentDuck();
            Point anchor = null;
            if (duck != null && duck.getAnchorPoints().containsKey(this.type)) {
                anchor = duck.getAnchorPoints().get(this.type);
            } else {
                // 回退到默认偏移
                anchor = new Point(baseImage.getWidth(null)/2, baseImage.getHeight(null)/2);
            }

            // 计算装饰左上角位置（使装饰中心对齐锚点）
            int x = anchor.x - accessoryImage.getWidth(null) / 2;
            int y = anchor.y - accessoryImage.getHeight(null) / 2;

            g2d.drawImage(accessoryImage, x, y, null);
            g2d.dispose();
            return combined;
        } catch (Exception e) {
            e.printStackTrace();
            return baseImage;
        }
    }

    /**
     * 加载装扮图片
     */
    protected Image loadAccessoryImage() {
        try {
            // 先尝试从资源路径加载
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File("resources/images/accessories/" + imagePath);
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            }

            // 如果文件不存在，创建默认图片
            return createDefaultImage();

        } catch (IOException e) {
            return createDefaultImage();
        }
    }

    /**
     * 创建默认图片
     */
    protected Image createDefaultImage() {
        int size = 100;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 设置透明背景
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, size, size);
        g2d.setComposite(AlphaComposite.SrcOver);

        // 根据类型绘制不同形状
        g2d.setColor(getDefaultColor());
        g2d.fill(getDefaultShape(size));

//        // 绘制文字
//        g2d.setColor(Color.WHITE);
//        g2d.setFont(new Font("Arial", Font.BOLD, 12));
//        String text = name.length() > 8 ? name.substring(0, 8) + "..." : name;
//        drawCenteredString(g2d, text, size);

        g2d.dispose();
        return image;
    }

    /**
     * 获取默认颜色
     */
    protected Color getDefaultColor() {
        switch (type) {
            case "hat": return new Color(139, 69, 19); // 棕色
            case "glasses": return new Color(105, 105, 105); // 灰色
            case "scarf": return new Color(220, 20, 60); // 红色
            case "tie": return new Color(30, 144, 255); // 蓝色
            case "cane": return new Color(160, 82, 45); // 褐色
            default: return Color.GRAY;
        }
    }

    /**
     * 获取默认形状
     */
    protected Shape getDefaultShape(int size) {
        switch (type) {
            case "hat":
                return new java.awt.geom.RoundRectangle2D.Double(
                        size * 0.2, size * 0.1, size * 0.6, size * 0.4, 20, 20);
            case "glasses":
                return new java.awt.geom.Ellipse2D.Double(
                        size * 0.1, size * 0.3, size * 0.3, size * 0.3);
            case "scarf":
                return new java.awt.geom.Rectangle2D.Double(
                        size * 0.3, size * 0.5, size * 0.4, size * 0.3);
            case "tie":
                int[] xPoints = {size/2, (int)(size*0.6), (int)(size*0.4)};
                int[] yPoints = {(int)(size*0.2), (int)(size*0.8), (int)(size*0.8)};
                return new java.awt.Polygon(xPoints, yPoints, 3);
            case "cane":
                return new java.awt.geom.Rectangle2D.Double(
                        size * 0.45, size * 0.1, size * 0.1, size * 0.8);
            default:
                return new java.awt.geom.Ellipse2D.Double(0, 0, size, size);
        }
    }

    /**
     * 在中心绘制文字
     */
    private void drawCenteredString(Graphics2D g2d, String text, int size) {
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        int x = (size - metrics.stringWidth(text)) / 2;
        int y = (size - metrics.getHeight()) / 2 + metrics.getAscent();
        g2d.drawString(text, x, y);
    }

    /**
     * 获取图片偏移量
     */
    protected Point getImageOffset() {
        return new Point(0, 0);
    }

    @Override
    public boolean isCompatibleWith(DressUpItem other) {
        // 默认不兼容同类型装扮
        if (other != null && this.getType().equals(other.getType())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f (%d 点)",
                name, type, price, stylePoints);
    }
}