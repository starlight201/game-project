package com.game.dressup;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人物抽象基类
 * 使用装饰器模式的基础组件
 */
public abstract class Person {
    protected String name;
    protected String description;
    protected Image baseImage;
    protected List<DressUpItem> accessories = new ArrayList<>();
    protected Map<String, Point> anchorPoints = new HashMap<>();

    public Person(String name, String description) {
        this.name = name;
        this.description = description;
        loadBaseImage();
        initializeAnchorPoints();
    }
    // 在构造函数或 loadBaseImage 后初始化锚点
    protected void initializeAnchorPoints() {
        // 默认值（可被子类覆盖）
        anchorPoints.put("hat", new Point(100, 10));       // 头顶
        anchorPoints.put("glasses", new Point(95, 55));    // 眼睛
        anchorPoints.put("scarf", new Point(100, 130));    // 脖子
        anchorPoints.put("tie", new Point(100, 140));      // 胸口
        anchorPoints.put("cane", new Point(180, 160));     // 右手侧
    }

    /**
     * 加载基础图片
     */
    protected abstract void loadBaseImage();

    /**
     * 显示人物信息
     */
    public String show() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(description).append("\n");
        if (!accessories.isEmpty()) {
            sb.append("装扮: ");
            for (DressUpItem item : accessories) {
                sb.append(item.getName()).append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * 绘制人物
     */
    public Image draw() {
        if (baseImage == null) {
            loadBaseImage();
        }
        return applyAccessories(baseImage);
    }

    /**
     * 应用所有配饰
     */
    protected Image applyAccessories(Image image) {
        Image result = image;
        for (DressUpItem item : accessories) {
            result = item.applyToImage(result);
        }
        return result;
    }

    /**
     * 添加装扮项
     */
    public void addAccessory(DressUpItem item) {
        if (item != null && !accessories.contains(item)) {
            accessories.add(item);
        }
    }

    /**
     * 移除装扮项
     */
    public void removeAccessory(DressUpItem item) {
        accessories.remove(item);
    }

    /**
     * 清空所有装扮
     */
    public void clearAccessories() {
        accessories.clear();
    }

    /**
     * 获取总装扮价格
     */
    public double getTotalOutfitCost() {
        double total = 0;
        for (DressUpItem item : accessories) {
            total += item.getPrice();
        }
        return total;
    }

    /**
     * 获取装扮评分
     */
    public int getStyleScore() {
        int score = 0;
        for (DressUpItem item : accessories) {
            score += item.getStylePoints();
        }
        return score;
    }

    /**
     * 检查装扮项是否兼容
     */
    public boolean isAccessoryCompatible(DressUpItem newItem) {
        for (DressUpItem existing : accessories) {
            if (!existing.isCompatibleWith(newItem) || !newItem.isCompatibleWith(existing)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取所有装扮项
     */
    public List<DressUpItem> getAccessories() {
        return new ArrayList<>(accessories);
    }

    /**
     * 获取人物名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取基础图片
     */
    public Image getBaseImage() {
        return baseImage;
    }

    @Override
    public String toString() {
        return show();
    }
    public Map<String, Point> getAnchorPoints() {
        return anchorPoints;
    }


}