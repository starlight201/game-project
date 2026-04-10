package com.game.dressup;

import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * 眼镜装饰器
 */
public class Glasses extends Finery {

    public Glasses(String name, String description, double price, int stylePoints, String imagePath) {
        super(name, description, price, stylePoints, imagePath, "glasses");
    }

    @Override
    public List<String> getApplicableCharacters() {
        return List.of();
    }

    @Override
    public Image applyToImage(Image baseImage) {
        try {
            // 加载眼镜图片
            Image glassesImage = loadAccessoryImage();
            if (glassesImage == null) {
                return baseImage;
            }

            // 创建合成图片
            int width = Math.max(baseImage.getWidth(null), glassesImage.getWidth(null));
            int height = Math.max(baseImage.getHeight(null), glassesImage.getHeight(null));

            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combined.createGraphics();

            // 设置渲染质量
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // 绘制基础图片
            g2d.drawImage(baseImage, 0, 0, null);

            // 计算眼镜位置（在眼睛位置）
            int x = (width - glassesImage.getWidth(null)) / 2;
            int y = height * 1/4; // 眼镜在眼睛位置

            // 根据眼镜类型调整位置
            Point offset = getImageOffset();
            x += offset.x;
            y += offset.y;
            y-=50;

            // 绘制眼镜图片
            g2d.drawImage(glassesImage, x, y, null);

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
                File file = new File("resources/images/accessories/glasses/" + imagePath);
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
        java.awt.geom.GeneralPath glassesShape = new java.awt.geom.GeneralPath();

        // 左镜片（仅边框）
        glassesShape.append(new java.awt.geom.Ellipse2D.Double(size * 0.1, size * 0.3, size * 0.3, size * 0.3), false);
        // 右镜片
        glassesShape.append(new java.awt.geom.Ellipse2D.Double(size * 0.6, size * 0.3, size * 0.3, size * 0.3), false);
        // 横梁
        glassesShape.append(new java.awt.geom.Rectangle2D.Double(size * 0.4, size * 0.45, size * 0.2, size * 0.05), false);

        return glassesShape;
    }

    @Override
    protected Point getImageOffset() {
        // 眼镜居中偏上
        return new Point(0, -10);
    }
    @Override
    protected Image createDefaultImage() {
        int size = 100;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 透明背景
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, size, size);
        g2d.setComposite(AlphaComposite.SrcOver);

        // 描边眼镜（不填充）
        g2d.setColor(getDefaultColor());
        g2d.setStroke(new BasicStroke(3));
        Shape shape = getDefaultShape(size);
        g2d.draw(shape); // ← 只 draw，不 fill

        g2d.dispose();
        return image;
    }
}