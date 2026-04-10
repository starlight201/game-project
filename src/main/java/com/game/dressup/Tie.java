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
 * 领带装饰器
 */
public class Tie extends Finery {

    public Tie(String name, String description, double price, int stylePoints, String imagePath) {
        super(name, description, price, stylePoints, imagePath, "tie");
    }

    @Override
    public List<String> getApplicableCharacters() {
        return List.of();
    }

    @Override
    public Image applyToImage(Image baseImage) {
        try {
            // 加载领带图片
            Image tieImage = loadAccessoryImage();
            if (tieImage == null) {
                return baseImage;
            }

            // 创建合成图片
            int width = Math.max(baseImage.getWidth(null), tieImage.getWidth(null));
            int height = Math.max(baseImage.getHeight(null), tieImage.getHeight(null));

            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combined.createGraphics();

            // 设置渲染质量
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // 绘制基础图片
            g2d.drawImage(baseImage, 0, 0, null);

            // 计算领带位置（在胸前）
            int x = (width - tieImage.getWidth(null)) / 2;
            int y = height * 3/5; // 领带在胸前位置

            // 根据领带类型调整位置
            Point offset = getImageOffset();
            x += offset.x;
            y += offset.y;
            y-=150;

            // 绘制领带图片
            g2d.drawImage(tieImage, x, y, null);

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
                File file = new File("resources/images/accessories/ties/" + imagePath);
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
        // 领带形状 - 三角形
        java.awt.geom.GeneralPath tieShape = new java.awt.geom.GeneralPath();

        tieShape.moveTo(size * 0.5, size * 0.2); // 顶部中间
        tieShape.lineTo(size * 0.7, size * 0.8); // 右下
        tieShape.lineTo(size * 0.3, size * 0.8); // 左下
        tieShape.closePath();

        return tieShape;
    }

    @Override
    protected Color getDefaultColor() {
        // 领带默认颜色 - 蓝色
        return new Color(30, 144, 255);
    }

    @Override
    protected Point getImageOffset() {
        // 领带向下偏移
        return new Point(0, 50);
    }
}