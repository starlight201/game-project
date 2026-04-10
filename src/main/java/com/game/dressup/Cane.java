package com.game.dressup;

import java.awt.Point;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * 手杖装饰器
 */
public class Cane extends Finery {

    public Cane(String name, String description, double price, int stylePoints, String imagePath) {
        super(name, description, price, stylePoints, imagePath, "cane");
    }

    @Override
    public List<String> getApplicableCharacters() {
        return List.of();
    }

    @Override
    public Image applyToImage(Image baseImage) {
        try {
            // 加载手杖图片
            Image caneImage = loadAccessoryImage();
            if (caneImage == null) {
                return baseImage;
            }

            // 创建合成图片
            int width = Math.max(baseImage.getWidth(null), caneImage.getWidth(null));
            int height = Math.max(baseImage.getHeight(null), caneImage.getHeight(null));

            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combined.createGraphics();

            // 设置渲染质量
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // 绘制基础图片
            g2d.drawImage(baseImage, 0, 0, null);

            // 计算手杖位置（在旁边）
            int x = width - caneImage.getWidth(null) - 20; // 右边
            int y = height - caneImage.getHeight(null) - 20; // 底部

            // 根据手杖类型调整位置
            Point offset = getImageOffset();
            x += offset.x;
            y += offset.y;
            y-=40;

            // 绘制手杖图片
            g2d.drawImage(caneImage, x, y, null);

            g2d.dispose();
            return combined;

        } catch (Exception e) {
            e.printStackTrace();
            return baseImage;
        }
    }

    @Override
    public boolean isPurchasable() {
        return super.isPurchasable();
    }

    @Override
    public String getEffectDescription() {
        return super.getEffectDescription();
    }

    @Override
    protected Image loadAccessoryImage() {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File("resources/images/accessories/canes/" + imagePath);
                if (file.exists()) {
                    return ImageIO.read(file);
                }
            }
            return createDefaultImage();
        } catch (IOException e) {
            return createDefaultImage();
        }
    }

    @Override
    protected java.awt.Shape getDefaultShape(int size) {
        // 手杖形状 - 长棍
        java.awt.geom.GeneralPath caneShape = new java.awt.geom.GeneralPath();

        // 手杖杆
        caneShape.append(new java.awt.geom.Rectangle2D.Double(
                size * 0.45, size * 0.1, size * 0.1, size * 0.8), false);

        // 手杖柄
        caneShape.append(new java.awt.geom.Ellipse2D.Double(
                size * 0.4, size * 0.05, size * 0.2, size * 0.1), false);

        return caneShape;
    }

    @Override
    protected Color getDefaultColor() {
        // 手杖默认颜色 - 棕色
        return new Color(160, 82, 45);
    }

    @Override
    protected Point getImageOffset() {
        // 手杖在右边
        return new Point(30, -20);
    }
}