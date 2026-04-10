package com.game.redpacket.enums;

import com.game.redpacket.RedPacketSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 红包大小枚举测试
 */
class RedPacketSizeTest {

    @Test
    @DisplayName("测试枚举值数量")
    void testEnumCount() {
        RedPacketSize[] values = RedPacketSize.values();
        assertEquals(3, values.length, "应有3种红包大小");
    }

    @ParameterizedTest
    @EnumSource(RedPacketSize.class)
    @DisplayName("测试所有枚举值的存在性")
    void testAllEnumValuesExist(RedPacketSize size) {
        assertNotNull(size, "枚举值不应为null");
        assertNotNull(size.getDisplayName(), "显示名称不应为null");
        assertTrue(size.getMultiplier() > 0, "乘数应大于0");
    }

    @ParameterizedTest
    @CsvSource({
            "SMALL, 小, 1.0",
            "MEDIUM, 中, 2.0",
            "LARGE, 大, 5.0"
    })
    @DisplayName("测试枚举值属性")
    void testEnumProperties(String enumName, String displayName, double multiplier) {
        RedPacketSize size = RedPacketSize.valueOf(enumName);

        assertEquals(displayName, size.getDisplayName(), "显示名称应匹配");
        assertEquals(multiplier, size.getMultiplier(), 0.001, "乘数应匹配");
    }

    @ParameterizedTest
    @CsvSource({
            "小, SMALL",
            "中, MEDIUM",
            "大, LARGE"
    })
    @DisplayName("测试从名称解析枚举")
    void testFromName(String name, RedPacketSize expected) {
        RedPacketSize actual = RedPacketSize.fromName(name);
        assertEquals(expected, actual, "从名称解析应返回正确的枚举");
    }

    @Test
    @DisplayName("测试从无效名称解析")
    void testFromInvalidName() {
        // 默认应返回SMALL
        RedPacketSize result = RedPacketSize.fromName("无效名称");
        assertEquals(RedPacketSize.SMALL, result, "无效名称应返回默认值SMALL");
    }

    @Test
    @DisplayName("测试乘数关系")
    void testMultiplierRelations() {
        // 验证乘数大小关系
        assertTrue(RedPacketSize.SMALL.getMultiplier() < RedPacketSize.MEDIUM.getMultiplier(),
                "中红包乘数应大于小红包");
        assertTrue(RedPacketSize.MEDIUM.getMultiplier() < RedPacketSize.LARGE.getMultiplier(),
                "大红包乘数应大于中红包");
    }

    @Test
    @DisplayName("测试枚举顺序")
    void testEnumOrder() {
        RedPacketSize[] sizes = RedPacketSize.values();

        // 验证枚举顺序
        assertEquals(RedPacketSize.SMALL, sizes[0], "第一个枚举应为SMALL");
        assertEquals(RedPacketSize.MEDIUM, sizes[1], "第二个枚举应为MEDIUM");
        assertEquals(RedPacketSize.LARGE, sizes[2], "第三个枚举应为LARGE");
    }

    @Test
    @DisplayName("测试枚举比较")
    void testEnumComparison() {
        assertTrue(RedPacketSize.SMALL.compareTo(RedPacketSize.MEDIUM) < 0,
                "SMALL应小于MEDIUM");
        assertTrue(RedPacketSize.MEDIUM.compareTo(RedPacketSize.LARGE) < 0,
                "MEDIUM应小于LARGE");
        assertEquals(0, RedPacketSize.SMALL.compareTo(RedPacketSize.SMALL),
                "相同枚举比较应为0");
    }

    @Test
    @DisplayName("测试枚举名称")
    void testEnumName() {
        assertEquals("SMALL", RedPacketSize.SMALL.name(), "枚举名称应正确");
        assertEquals("MEDIUM", RedPacketSize.MEDIUM.name(), "枚举名称应正确");
        assertEquals("LARGE", RedPacketSize.LARGE.name(), "枚举名称应正确");
    }

    @Test
    @DisplayName("测试枚举序数")
    void testEnumOrdinal() {
        assertEquals(0, RedPacketSize.SMALL.ordinal(), "SMALL序数应为0");
        assertEquals(1, RedPacketSize.MEDIUM.ordinal(), "MEDIUM序数应为1");
        assertEquals(2, RedPacketSize.LARGE.ordinal(), "LARGE序数应为2");
    }

    @Test
    @DisplayName("测试枚举值Of方法")
    void testValueOf() {
        assertEquals(RedPacketSize.SMALL, RedPacketSize.valueOf("SMALL"),
                "valueOf应返回正确枚举");
        assertEquals(RedPacketSize.MEDIUM, RedPacketSize.valueOf("MEDIUM"),
                "valueOf应返回正确枚举");
        assertEquals(RedPacketSize.LARGE, RedPacketSize.valueOf("LARGE"),
                "valueOf应返回正确枚举");
    }

    @Test
    @DisplayName("测试枚举toString方法")
    void testToString() {
        assertEquals("小", RedPacketSize.SMALL.toString(), "toString应返回显示名称");
        assertEquals("中", RedPacketSize.MEDIUM.toString(), "toString应返回显示名称");
        assertEquals("大", RedPacketSize.LARGE.toString(), "toString应返回显示名称");
    }
}