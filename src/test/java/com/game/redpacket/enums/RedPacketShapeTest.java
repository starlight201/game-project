package com.game.redpacket.enums;

import com.game.redpacket.RedPacketShape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 红包形状枚举测试
 */
class RedPacketShapeTest {

    @Test
    @DisplayName("测试枚举值数量")
    void testEnumCount() {
        RedPacketShape[] shapes = RedPacketShape.values();
        assertEquals(4, shapes.length, "应有4种红包形状");
    }

    @ParameterizedTest
    @EnumSource(RedPacketShape.class)
    @DisplayName("测试所有形状枚举值")
    void testAllShapesExist(RedPacketShape shape) {
        assertNotNull(shape, "形状枚举不应为null");
        assertNotNull(shape.getDisplayName(), "显示名称不应为null");
    }

    @ParameterizedTest
    @CsvSource({
            "SQUARE, 方形",
            "CIRCLE, 圆形",
            "TRIANGLE, 三角形",
            "STAR, 星形"
    })
    @DisplayName("测试形状名称映射")
    void testShapeNames(String enumName, String displayName) {
        RedPacketShape shape = RedPacketShape.valueOf(enumName);
        assertEquals(displayName, shape.getDisplayName(), "显示名称应匹配");
    }

    @ParameterizedTest
    @CsvSource({
            "方形, SQUARE",
            "圆形, CIRCLE",
            "三角形, TRIANGLE",
            "星形, STAR"
    })
    @DisplayName("测试从名称解析形状")
    void testFromName(String name, RedPacketShape expected) {
        RedPacketShape actual = RedPacketShape.fromName(name);
        assertEquals(expected, actual, "从名称解析应返回正确的形状");
    }

    @Test
    @DisplayName("测试从无效名称解析")
    void testFromInvalidName() {
        // 默认应返回SQUARE
        RedPacketShape result = RedPacketShape.fromName("无效形状");
        assertEquals(RedPacketShape.SQUARE, result, "无效名称应返回默认值SQUARE");
    }

    @Test
    @DisplayName("测试枚举顺序")
    void testEnumOrder() {
        RedPacketShape[] shapes = RedPacketShape.values();

        assertEquals(RedPacketShape.SQUARE, shapes[0], "第一个枚举应为SQUARE");
        assertEquals(RedPacketShape.CIRCLE, shapes[1], "第二个枚举应为CIRCLE");
        assertEquals(RedPacketShape.TRIANGLE, shapes[2], "第三个枚举应为TRIANGLE");
        assertEquals(RedPacketShape.STAR, shapes[3], "第四个枚举应为STAR");
    }

    @Test
    @DisplayName("测试枚举比较")
    void testEnumComparison() {
        assertTrue(RedPacketShape.SQUARE.compareTo(RedPacketShape.CIRCLE) < 0,
                "SQUARE应小于CIRCLE");
        assertTrue(RedPacketShape.CIRCLE.compareTo(RedPacketShape.TRIANGLE) < 0,
                "CIRCLE应小于TRIANGLE");
        assertEquals(0, RedPacketShape.SQUARE.compareTo(RedPacketShape.SQUARE),
                "相同枚举比较应为0");
    }

    @Test
    @DisplayName("测试枚举名称")
    void testEnumName() {
        assertEquals("SQUARE", RedPacketShape.SQUARE.name(), "枚举名称应正确");
        assertEquals("CIRCLE", RedPacketShape.CIRCLE.name(), "枚举名称应正确");
        assertEquals("TRIANGLE", RedPacketShape.TRIANGLE.name(), "枚举名称应正确");
        assertEquals("STAR", RedPacketShape.STAR.name(), "枚举名称应正确");
    }

    @Test
    @DisplayName("测试枚举序数")
    void testEnumOrdinal() {
        assertEquals(0, RedPacketShape.SQUARE.ordinal(), "SQUARE序数应为0");
        assertEquals(1, RedPacketShape.CIRCLE.ordinal(), "CIRCLE序数应为1");
        assertEquals(2, RedPacketShape.TRIANGLE.ordinal(), "TRIANGLE序数应为2");
        assertEquals(3, RedPacketShape.STAR.ordinal(), "STAR序数应为3");
    }

    @Test
    @DisplayName("测试枚举值Of方法")
    void testValueOf() {
        assertEquals(RedPacketShape.SQUARE, RedPacketShape.valueOf("SQUARE"),
                "valueOf应返回正确枚举");
        assertEquals(RedPacketShape.CIRCLE, RedPacketShape.valueOf("CIRCLE"),
                "valueOf应返回正确枚举");
        assertEquals(RedPacketShape.TRIANGLE, RedPacketShape.valueOf("TRIANGLE"),
                "valueOf应返回正确枚举");
        assertEquals(RedPacketShape.STAR, RedPacketShape.valueOf("STAR"),
                "valueOf应返回正确枚举");
    }

    @Test
    @DisplayName("测试枚举toString方法")
    void testToString() {
        assertEquals("方形", RedPacketShape.SQUARE.toString(), "toString应返回显示名称");
        assertEquals("圆形", RedPacketShape.CIRCLE.toString(), "toString应返回显示名称");
        assertEquals("三角形", RedPacketShape.TRIANGLE.toString(), "toString应返回显示名称");
        assertEquals("星形", RedPacketShape.STAR.toString(), "toString应返回显示名称");
    }
}