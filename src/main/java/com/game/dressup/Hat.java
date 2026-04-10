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
 * 帽子装饰器
 */
public class Hat extends Finery {

    public Hat(String name, String description, double price, int stylePoints, String imagePath) {
        super(name, description, price, stylePoints, imagePath, "hat");
    }

    @Override
    public List<String> getApplicableCharacters() {
        return List.of();
    }

    @Override
    public Image applyToImage(Image baseImage) {
        try {
            // 加载帽子图片
            Image hatImage = loadAccessoryImage();
            if (hatImage == null) {
                return baseImage;
            }

            // 创建合成图片
            int width = Math.max(baseImage.getWidth(null), hatImage.getWidth(null));
            int height = Math.max(baseImage.getHeight(null), hatImage.getHeight(null));

            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combined.createGraphics();

            // 设置渲染质量
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // 绘制基础图片
            g2d.drawImage(baseImage, 0, 0, null);

            // 计算帽子位置（在头顶）
            int x = (width - hatImage.getWidth(null)) / 2;
            int y = 0; // 帽子在顶部，稍微向上调整

            // 根据帽子类型调整位置
            Point offset = getImageOffset();
            x += offset.x;
            y += offset.y;

            // 绘制帽子图片
            g2d.drawImage(hatImage, x, y, null);

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
                File file = new File("resources/images/accessories/hats/" + imagePath);
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
        // 更小更扁的帽子形状
        java.awt.geom.GeneralPath hatShape = new java.awt.geom.GeneralPath();

        // 扁平的帽子顶部
        hatShape.moveTo(size * 0.2, size * 0.3);
        hatShape.curveTo(size * 0.2, size * 0.1,
                size * 0.8, size * 0.1,
                size * 0.8, size * 0.3);

        // 扁平的帽子边缘
        hatShape.lineTo(size * 0.8, size * 0.5);
        hatShape.curveTo(size * 0.8, size * 0.6,
                size * 0.6, size * 0.65,
                size * 0.5, size * 0.65);
        hatShape.curveTo(size * 0.4, size * 0.65,
                size * 0.2, size * 0.6,
                size * 0.2, size * 0.5);
        // 调整帽子形状：上面完全扁平，下面稍微弯曲，y轴方向宽度减小



        hatShape.closePath();

        return hatShape;
    }

    @Override
    protected Point getImageOffset() {
        // 帽子向上偏移，因为帽子更小了
        return new Point(0, -15);
    }
}