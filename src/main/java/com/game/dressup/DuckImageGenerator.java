package com.game.dressup;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 鸭子图片生成器
 */
public class DuckImageGenerator {
    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 400; // 减小高度，去掉文字区域
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255); // 纯白背景
    private static final int PADDING = 10;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 生成鸭子图片 - 简化版，只包含鸭子形象
     */
    public BufferedImage generateDuckImage(Person duck, List<DressUpItem> accessories) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 设置渲染质量
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 绘制纯白背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // 绘制鸭子形象（居中显示，占据大部分空间）
        drawDuckSimple(g2d, duck, accessories);

        g2d.dispose();
        return image;
    }

    /**
     * 简化版鸭子绘制 - 只绘制鸭子，不包含任何文字信息
     */
    private void drawDuckSimple(Graphics2D g2d, Person duck, List<DressUpItem> accessories) {
        Image duckImage = duck.draw();

        if (duckImage != null) {
            // 计算缩放比例，使鸭子适应画布
            double scale = calculateScale(duckImage.getWidth(null), duckImage.getHeight(null));
            int scaledWidth = (int)(duckImage.getWidth(null) * scale*1.5);
            int scaledHeight = (int)(duckImage.getHeight(null) * scale*1.5);

            // 居中显示
            int x = (IMAGE_WIDTH - scaledWidth) / 2;
            int y = (IMAGE_HEIGHT - scaledHeight) / 2;

            // 绘制缩放后的鸭子图片
            g2d.drawImage(duckImage, x, y, scaledWidth, scaledHeight, null);

        } else {
            // 如果图片为空，绘制简单的鸭子轮廓
            drawSimpleDuckOutline(g2d);
        }
    }

    /**
     * 计算缩放比例
     */
    private double calculateScale(int originalWidth, int originalHeight) {
        double widthRatio = (IMAGE_WIDTH - 2 * PADDING) * 0.8 / originalWidth;
        double heightRatio = (IMAGE_HEIGHT - 2 * PADDING) * 0.8 / originalHeight;
        return Math.min(widthRatio, heightRatio);
    }

    /**
     * 绘制简单的鸭子轮廓
     */
    private void drawSimpleDuckOutline(Graphics2D g2d) {
        int centerX = IMAGE_WIDTH / 2;
        int centerY = IMAGE_HEIGHT / 2;

        g2d.setColor(Color.YELLOW);
        g2d.fillOval(centerX - 60, centerY - 80, 120, 160); // 身体
        g2d.fillOval(centerX - 40, centerY - 100, 80, 60); // 头部

        g2d.setColor(Color.ORANGE);
        g2d.fillArc(centerX - 15, centerY - 60, 30, 20, 0, 180); // 嘴巴

        g2d.setColor(Color.BLACK);
        g2d.fillOval(centerX - 20, centerY - 80, 10, 10); // 左眼
        g2d.fillOval(centerX + 10, centerY - 80, 10, 10); // 右眼
    }

    /**
     * 异步保存图片
     */
    public void saveImageAsync(BufferedImage image, String fileName, SaveCallback callback) {
        executor.execute(() -> {
            try {
                saveImage(image, fileName);
                SwingUtilities.invokeLater(() -> callback.onSuccess(fileName));
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * 保存图片
     */
    public void saveImage(BufferedImage image, String fileName) throws IOException {
        File outputDir = new File("output/duck_images");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, fileName);
        ImageIO.write(image, "PNG", outputFile);
    }

    /**
     * 生成文件名
     */
    public String generateFileName(Person duck) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String duckType = "";

        if (duck instanceof LittleDuck) {
            LittleDuck littleDuck = (LittleDuck) duck;
            duckType = littleDuck.getDuckType() == 1 ? "一号" : "二号";
        }

        return duck.getName() + duckType + "_" + timestamp + ".png";
    }

    /**
     * 生成分享文本 - 简化版
     */
    public String generateShareText(Person duck, List<DressUpItem> accessories) {
        StringBuilder sb = new StringBuilder();
        sb.append("🎉 我的").append(duck.getName()).append("装扮完成！\n\n");

        if (!accessories.isEmpty()) {
            sb.append("装扮清单：\n");
            for (DressUpItem item : accessories) {
                sb.append("  - ").append(item.getName()).append("\n");
            }
        }

        sb.append("\n图片已保存至 duck_images 文件夹");
        return sb.toString();
    }

    /**
     * 保存回调接口
     */
    public interface SaveCallback {
        void onSuccess(String fileName);
        void onError(String errorMessage);
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        executor.shutdown();
    }
}