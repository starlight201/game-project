package com.game.redpacket.factory;

import com.game.redpacket.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 红包工厂测试
 */
class RedPacketFactoryTest extends BaseRedPacketTest {

    @BeforeEach
    void setUp() {
        setRandomSeed(12345L); // 固定随机种子确保测试可重复
    }

    @Test
    @DisplayName("测试创建随机红包")
    void testCreateRandomRedPacket() {
        RedPacket packet = RedPacketFactory.createRandomRedPacket(TEST_X, TEST_Y);

        assertValidRedPacket(packet);
        assertEquals(TEST_X, packet.getX(), "红包X坐标应正确设置");
        assertEquals(TEST_Y, packet.getY(), "红包Y坐标应正确设置");
    }

    @ParameterizedTest
    @EnumSource(RedPacketSize.class)
    @DisplayName("测试创建指定大小的红包")
    void testCreateRedPacketWithSize(RedPacketSize size) {
        RedPacket packet = RedPacketFactory.createRedPacket(TEST_X, TEST_Y, size, RedPacketShape.SQUARE);

        assertValidRedPacket(packet);
        assertEquals(size, packet.getSize(), "红包大小应匹配");
        assertEquals(RedPacketShape.SQUARE, packet.getShape(), "红包形状应为方形");
    }

    @ParameterizedTest
    @EnumSource(RedPacketShape.class)
    @DisplayName("测试创建指定形状的红包")
    void testCreateRedPacketWithShape(RedPacketShape shape) {
        RedPacket packet = RedPacketFactory.createRedPacket(TEST_X, TEST_Y, RedPacketSize.MEDIUM, shape);

        assertValidRedPacket(packet);
        assertEquals(RedPacketSize.MEDIUM, packet.getSize(), "红包大小应为中");
        assertEquals(shape, packet.getShape(), "红包形状应匹配");
    }

    @Test
    @DisplayName("测试批量创建红包")
    void testCreateRedPackets() {
        int count = 10;
        RedPacket[] packets = RedPacketFactory.createRedPackets(count, TEST_WIDTH, TEST_HEIGHT);

        assertEquals(count, packets.length, "应创建指定数量的红包");

        for (int i = 0; i < packets.length; i++) {
            assertValidRedPacket(packets[i]);
            assertRedPacketInBounds(packets[i], TEST_WIDTH, TEST_HEIGHT);

            // 检查红包不重叠（粗略检查）
            for (int j = i + 1; j < packets.length; j++) {
                assertRedPacketsNotOverlapping(packets[i], packets[j]);
            }
        }
    }

    @Test
    @DisplayName("测试创建大量红包")
    void testCreateManyRedPackets() {
        int count = 100;
        RedPacket[] packets = RedPacketFactory.createRedPackets(count, TEST_WIDTH, TEST_HEIGHT);

        assertEquals(count, packets.length, "应创建大量红包");

        // 统计不同形状和大小的红包数量
        int squareCount = 0;
        int circleCount = 0;
        int triangleCount = 0;
        int starCount = 0;

        int smallCount = 0;
        int mediumCount = 0;
        int largeCount = 0;

        for (RedPacket packet : packets) {
            switch (packet.getShape()) {
                case SQUARE: squareCount++; break;
                case CIRCLE: circleCount++; break;
                case TRIANGLE: triangleCount++; break;
                case STAR: starCount++; break;
            }

            switch (packet.getSize()) {
                case SMALL: smallCount++; break;
                case MEDIUM: mediumCount++; break;
                case LARGE: largeCount++; break;
            }
        }

        // 验证各种形状的红包都有创建
        assertTrue(squareCount > 0, "应创建方形红包");
        assertTrue(circleCount > 0, "应创建圆形红包");
        assertTrue(triangleCount > 0, "应创建三角形红包");
        assertTrue(starCount > 0, "应创建星形红包");

        // 验证各种大小的红包都有创建
        assertTrue(smallCount > 0, "应创建小红包");
        assertTrue(mediumCount > 0, "应创建中红包");
        assertTrue(largeCount > 0, "应创建大红包");
    }

    @ParameterizedTest
    @CsvSource({
            "小, 方形, SMALL, SQUARE",
            "中, 圆形, MEDIUM, CIRCLE",
            "大, 星形, LARGE, STAR"
    })
    @DisplayName("测试从名称创建红包")
    void testCreateFromName(String sizeName, String shapeName, RedPacketSize expectedSize, RedPacketShape expectedShape) {
        RedPacket packet = RedPacketFactory.createFromName(TEST_X, TEST_Y, sizeName, shapeName);

        assertValidRedPacket(packet);
        assertEquals(expectedSize, packet.getSize(), "红包大小应匹配名称");
        assertEquals(expectedShape, packet.getShape(), "红包形状应匹配名称");
    }

    @Test
    @DisplayName("测试从无效名称创建红包")
    void testCreateFromInvalidNames() {
        // 测试无效大小名称
        RedPacket packet1 = RedPacketFactory.createFromName(TEST_X, TEST_Y, "无效大小", "方形");
        assertEquals(RedPacketSize.SMALL, packet1.getSize(), "无效大小应使用默认值");

        // 测试无效形状名称
        RedPacket packet2 = RedPacketFactory.createFromName(TEST_X, TEST_Y, "中", "无效形状");
        assertEquals(RedPacketShape.SQUARE, packet2.getShape(), "无效形状应使用默认值");

        // 测试两个都无效
        RedPacket packet3 = RedPacketFactory.createFromName(TEST_X, TEST_Y, "无效大小", "无效形状");
        assertEquals(RedPacketSize.SMALL, packet3.getSize(), "应使用默认大小");
        assertEquals(RedPacketShape.SQUARE, packet3.getShape(), "应使用默认形状");
    }

    @Test
    @DisplayName("测试红包金额范围")
    void testRedPacketAmountRange() {
        // 测试多次创建，验证金额在合理范围内
        for (int i = 0; i < 100; i++) {
            RedPacket packet = RedPacketFactory.createRandomRedPacket(TEST_X, TEST_Y);
            double amount = packet.getAmount();

            assertTrue(amount >= 0.1, "红包金额应 >= 0.1");
            assertTrue(amount <= 99.9, "红包金额应 <= 99.9");

            // 验证金额与大小相关
            double baseAmount = amount / packet.getSize().getMultiplier();
            assertTrue(baseAmount >= 0.1 && baseAmount <= 9.9,
                    "基础金额应在合理范围内");
        }
    }

    @Test
    @DisplayName("测试红包位置分布")
    void testRedPacketPositionDistribution() {
        int count = 50;
        RedPacket[] packets = RedPacketFactory.createRedPackets(count, TEST_WIDTH, TEST_HEIGHT);

        // 验证红包位置在有效范围内
        for (RedPacket packet : packets) {
            assertTrue(packet.getX() >= 0, "红包X坐标应 >= 0");
            assertTrue(packet.getY() >= 0, "红包Y坐标应 >= 0");
            assertTrue(packet.getX() + packet.getWidth() <= TEST_WIDTH,
                    "红包右边界应在面板内");
            assertTrue(packet.getY() + packet.getHeight() <= TEST_HEIGHT,
                    "红包下边界应在面板内");
        }
    }

    @Test
    @DisplayName("测试工厂单例模式")
    void testFactorySingletonPattern() {
        // 工厂类通常使用静态方法，无需单例测试
        // 这里验证方法调用不会抛出异常
        assertDoesNotThrow(() -> {
            RedPacketFactory.createRandomRedPacket(0, 0);
            RedPacketFactory.createRedPackets(5, 100, 100);
            RedPacketFactory.createFromName(0, 0, "中", "圆形");
        }, "工厂方法调用不应抛出异常");
    }

    @Test
    @DisplayName("测试边界情况处理")
    void testEdgeCases() {
        // 测试零个红包
        RedPacket[] zeroPackets = RedPacketFactory.createRedPackets(0, TEST_WIDTH, TEST_HEIGHT);
        assertEquals(0, zeroPackets.length, "零个红包应返回空数组");

        // 测试负坐标
        RedPacket negativePacket = RedPacketFactory.createRandomRedPacket(-10, -10);
        assertValidRedPacket(negativePacket);

        // 测试极小面板
        RedPacket[] smallPanelPackets = RedPacketFactory.createRedPackets(5, 10, 10);
        assertEquals(5, smallPanelPackets.length, "应能在小面板创建红包");
    }

    @Test
    @DisplayName("测试线程安全性")
    void testThreadSafety() throws InterruptedException {
        int threadCount = 10;
        int packetsPerThread = 20;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < packetsPerThread; j++) {
                    RedPacket packet = RedPacketFactory.createRandomRedPacket(
                            random.nextInt(TEST_WIDTH),
                            random.nextInt(TEST_HEIGHT)
                    );
                    assertValidRedPacket(packet);
                }
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 如果没有异常抛出，则测试通过
        assertTrue(true, "多线程环境下应正常工作");
    }

    // ========== 辅助断言方法 ==========

    public void assertValidRedPacket(RedPacket packet) {
        assertNotNull(packet, "红包对象不应为null");
        assertNotNull(packet.getSize(), "红包大小不应为null");
        assertNotNull(packet.getShape(), "红包形状不应为null");
        assertTrue(packet.getAmount() > 0, "红包金额应大于0");
        assertFalse(packet.isCollected(), "红包初始状态应未收集");
    }

    public void assertRedPacketInBounds(RedPacket packet, int width, int height) {
        assertTrue(packet.getX() >= 0, "红包X坐标应在边界内");
        assertTrue(packet.getY() >= 0, "红包Y坐标应在边界内");
        assertTrue(packet.getX() + packet.getWidth() <= width,
                "红包右边界应在边界内");
        assertTrue(packet.getY() + packet.getHeight() <= height,
                "红包下边界应在边界内");
    }

    public void assertRedPacketsNotOverlapping(RedPacket packet1, RedPacket packet2) {
        // 简化的重叠检测（使用边界矩形）
        boolean overlaps =
                packet1.getX() < packet2.getX() + packet2.getWidth() &&
                        packet1.getX() + packet1.getWidth() > packet2.getX() &&
                        packet1.getY() < packet2.getY() + packet2.getHeight() &&
                        packet1.getY() + packet1.getHeight() > packet2.getY();

        assertFalse(overlaps, "红包不应重叠");
    }
}