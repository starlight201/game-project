package com.game.dressup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 唐小鸭类 - 继承唐老鸭抽象类
 */
public class LittleDuck extends DonaldDuck {

    private int duckType; // 1: 唐小鸭一号, 2: 唐小鸭二号

    public LittleDuck(int type) {
        super(getDuckName(type), getDuckDescription(type));
        this.duckType = type;
    }

    private static String getDuckName(int type) {
        switch (type) {
            case 1: return "唐小鸭一号";
            case 2: return "唐小鸭二号";
            default: return "唐小鸭";
        }
    }

    private static String getDuckDescription(int type) {
        switch (type) {
            case 1: return "唐小鸭一号";
            case 2: return "唐小鸭二号";
            default: return "";
        }
    }

    @Override
    protected void loadBaseImage() {
        try {
            // 根据类型加载不同的图片
            String imageName = (duckType == 1) ? "little_duck_1.png" : "little_duck_2.png";
            File imageFile = new File("resources/images/duck/" + imageName);
            if (imageFile.exists()) {
                baseImage = ImageIO.read(imageFile);
            } else {
                baseImage = createDefaultImage();
            }
        } catch (IOException e) {
            baseImage = createDefaultImage();
        }
    }

    /**
     * 创建默认唐小鸭图片，根据类型略有不同
     */
    private Image createDefaultImage() {
        int width = 220;
        int height = 300;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 根据类型使用不同的背景色
        Color backgroundColor = (duckType == 1) ?
                new Color(255, 255, 200) : new Color(255, 240, 245); // 一号浅黄，二号浅粉

        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height);

        // 身体颜色也略有不同
        Color bodyColor = (duckType == 1) ? Color.YELLOW : new Color(255, 255, 150);
        g2d.setColor(bodyColor);
        g2d.fillOval(50, 80, 120, 120);

        // 头部
        g2d.fillOval(60, 40, 100, 80);

        // 嘴巴
        g2d.setColor(Color.ORANGE);
        g2d.fillArc(95, 70, 35, 20, 0, 180);

        // 眼睛
        g2d.setColor(Color.BLACK);
        g2d.fillOval(85, 60, 10, 10);
        g2d.fillOval(125, 60, 10, 10);

        // 腮红 - 二号更明显
        Color blushColor = (duckType == 1) ?
                new Color(255, 182, 193) : new Color(255, 150, 180);
        g2d.setColor(blushColor);
        g2d.fillOval(75, 75, 15, 10);
        g2d.fillOval(130, 75, 15, 10);



        // 脚
        g2d.setColor(Color.ORANGE);
        g2d.fillOval(75, 200, 25, 12);
        g2d.fillOval(120, 200, 25, 12);

        // 手臂
        g2d.fillOval(35, 120, 25, 15);
        g2d.fillOval(160, 120, 25, 15);

        g2d.dispose();
        return image;
    }

    @Override
    public String show() {
        String emoji = (duckType == 1) ? "🐤🐤" : "🦆🦆";
        return emoji + " " + name + ": " + description + "\n" + getOutfitDescription();
    }

    private String getOutfitDescription() {
        if (accessories.isEmpty()) {
            return "当前无装扮";
        }

        StringBuilder sb = new StringBuilder("当前装扮:\n");
        for (DressUpItem item : accessories) {
            sb.append("  - ").append(item.getName()).append(" (").append(item.getDescription()).append(")\n");
        }
        sb.append("总价值: $").append(String.format("%.2f", getTotalOutfitCost()));
        sb.append("  可爱度: ").append(getCutenessScore()).append("分");
        return sb.toString();
    }

    /**
     * 唐小鸭特别方法 - 获取可爱度
     */
    public int getCutenessScore() {
        int baseCuteness = (duckType == 1) ? 60 : 50; // 一号更可爱
        for (DressUpItem item : accessories) {
            baseCuteness += item.getStylePoints() * 2;
        }
        return Math.min(baseCuteness, 100);
    }

    @Override
    public int getClassicScore() {
        return 75; // 唐小鸭的经典度评分
    }

    @Override
    public int getDisneyStyleScore() {
        int baseScore = 70;
        for (DressUpItem item : accessories) {
            baseScore += item.getStylePoints();
        }
        return Math.min(baseScore, 95);
    }

    public int getDuckType() {
        return duckType;
    }
}