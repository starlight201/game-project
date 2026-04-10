package com.game.dressup;

import java.awt.Image;
import java.util.List;
import java.util.ArrayList;

/**
 * 装扮项接口
 */
public interface DressUpItem {
    /** 获取装扮名称 */
    String getName();

    /** 获取装扮描述 */
    String getDescription();

    /** 获取价格 */
    double getPrice();

    /** 获取时尚分数 */
    int getStylePoints();

    /** 获取适用的人物类型 */
    List<String> getApplicableCharacters();

    /** 获取装扮类型 */
    String getType();

    /** 应用装扮到图片 */
    Image applyToImage(Image baseImage);

    /** 检查是否兼容其他装扮 */
    default boolean isCompatibleWith(DressUpItem other) {
        // 默认所有装扮都兼容
        return true;
    }

    /** 获取资源路径 */
    String getImagePath();

    /** 是否已拥有 */
    boolean isOwned();

    /** 设置为已拥有 */
    void setOwned(boolean owned);

    /** 获取解锁等级 */
    int getUnlockLevel();

    /** 是否可以购买 */
    default boolean isPurchasable() {
        return getPrice() > 0;
    }

    /** 获取装扮效果描述 */
    default String getEffectDescription() {
        return "增加" + getStylePoints() + "点时尚值";
    }
}