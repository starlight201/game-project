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
 * 围巾装饰器
 */
public class Scarf extends Finery {

    public Scarf(String name, String description, double price, int stylePoints, String imagePath) {
        super(name, description, price, stylePoints, imagePath, "scarf");
    }

    @Override
    public List<String> getApplicableCharacters() {
        return List.of();
    }

    @Override
    public Image applyToImage(Image baseImage) {
        try {
            // 加载围巾图片
            Image scarfImage = loadAccessoryImage();
            if (scarfImage == null) {
                return baseImage;
            }

            // 创建合成图片
            int width = Math.max(baseImage.getWidth(null), scarfImage.getWidth(null));
            int height = Math.max(baseImage.getHeight(null), scarfImage.getHeight(null));

            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combined.createGraphics();

            // 设置渲染质量
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // 绘制基础图片
            g2d.drawImage(baseImage, 0, 0, null);

            // 计算围巾位置（在脖子位置）
            int x = (width - scarfImage.getWidth(null)) / 2;
            int y = height * 1/2; // 围巾在脖子位置

            // 根据围巾类型调整位置
            Point offset = getImageOffset();
            x += offset.x;
            y += offset.y;
            y-=150;

            // 绘制围巾图片
            g2d.drawImage(scarfImage, x, y, null);

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
                File file = new File("resources/images/accessories/scarves/" + imagePath);
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
        // 围巾形状 - 长条形
        java.awt.geom.GeneralPath scarfShape = new java.awt.geom.GeneralPath();

        // 围巾主体
        scarfShape.moveTo(size * 0.3, size * 0.5);
        scarfShape.curveTo(size * 0.4, size * 0.6, size * 0.6, size * 0.6, size * 0.7, size * 0.5);
        scarfShape.curveTo(size * 0.8, size * 0.4, size * 0.6, size * 0.8, size * 0.5, size * 0.9);
        scarfShape.curveTo(size * 0.4, size * 0.8, size * 0.2, size * 0.4, size * 0.3, size * 0.5);
        scarfShape.closePath();

        return scarfShape;
    }

    @Override
    protected Color getDefaultColor() {
        // 围巾默认颜色 - 红色
        return new Color(220, 20, 60);
    }

    @Override
    protected Point getImageOffset() {
        // 围巾向下偏移
        return new Point(0, 30);
    }
}