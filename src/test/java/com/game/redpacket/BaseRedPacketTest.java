package com.game.redpacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 抢红包游戏测试基类
 * 提供通用的测试工具方法和配置
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseRedPacketTest {

    // 测试常量
    protected static final int TEST_X = 100;
    protected static final int TEST_Y = 100;
    protected static final int TEST_WIDTH = 800;
    protected static final int TEST_HEIGHT = 600;
    protected static final int TEST_CENTER_X = TEST_WIDTH / 2;
    protected static final int TEST_CENTER_Y = TEST_HEIGHT / 2;

    // 测试随机数生成器（固定种子确保可重复性）
    protected Random random = new Random(123456789L);

    @BeforeEach
    void setUpBase() {
        MockitoAnnotations.openMocks(this);
    }

    // ========== 反射工具方法 ==========

    /**
     * 获取私有字段值
     */
    @SuppressWarnings("unchecked")
    protected <T> T getFieldValue(Object obj, String fieldName) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            fail("无法获取字段 '" + fieldName + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * 设置私有字段值
     */
    protected void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = findField(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            fail("无法设置字段 '" + fieldName + "': " + e.getMessage());
        }
    }

    /**
     * 递归查找字段（包括父类）
     */
    private Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            }
            return findField(superClass, fieldName);
        }
    }

    /**
     * 调用私有方法
     */
    protected <T> T invokePrivateMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }

            Method method = findMethod(obj.getClass(), methodName, paramTypes);
            method.setAccessible(true);

            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(obj, args);
            return result;
        } catch (Exception e) {
            fail("无法调用方法 '" + methodName + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * 递归查找方法（包括父类）
     */
    private Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes)
            throws NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            }
            return findMethod(superClass, methodName, paramTypes);
        }
    }

    // ========== 测试数据生成器 ==========

    /**
     * 创建测试红包
     */
    protected RedPacket createTestRedPacket(RedPacketSize size, RedPacketShape shape) {
        return RedPacketFactory.createRedPacket(TEST_X, TEST_Y, size, shape);
    }

    /**
     * 创建随机测试红包
     */
    protected RedPacket createRandomTestRedPacket() {
        RedPacketSize size = RedPacketSize.values()[random.nextInt(RedPacketSize.values().length)];
        RedPacketShape shape = RedPacketShape.values()[random.nextInt(RedPacketShape.values().length)];
        return createTestRedPacket(size, shape);
    }

    /**
     * 创建测试坦克
     */
    protected Tank createTestTank() {
        return new Tank(TEST_CENTER_X, TEST_CENTER_Y);
    }

    /**
     * 创建测试坦克在指定位置
     */
    protected Tank createTestTank(int x, int y) {
        return new Tank(x, y);
    }

    // ========== 事件模拟方法 ==========

    /**
     * 创建键盘事件
     */
    protected KeyEvent createKeyEvent(int keyCode, int eventType) {
        JPanel panel = new JPanel();
        return new KeyEvent(
                panel,
                eventType,
                System.currentTimeMillis(),
                0,
                keyCode,
                KeyEvent.CHAR_UNDEFINED
        );
    }

    /**
     * 创建鼠标事件
     */
    protected MouseEvent createMouseEvent(int x, int y, int button, int eventType) {
        JPanel panel = new JPanel();
        return new MouseEvent(
                panel,
                eventType,
                System.currentTimeMillis(),
                0,
                x, y,
                1,
                false,
                button
        );
    }

    /**
     * 模拟按键按下
     */
    protected void simulateKeyPress(Object target, int keyCode) {
        KeyEvent event = createKeyEvent(keyCode, KeyEvent.KEY_PRESSED);
        if (target instanceof java.awt.event.KeyListener) {
            ((java.awt.event.KeyListener) target).keyPressed(event);
        }
    }

    /**
     * 模拟按键释放
     */
    protected void simulateKeyRelease(Object target, int keyCode) {
        KeyEvent event = createKeyEvent(keyCode, KeyEvent.KEY_RELEASED);
        if (target instanceof java.awt.event.KeyListener) {
            ((java.awt.event.KeyListener) target).keyReleased(event);
        }
    }

    // ========== 断言工具方法 ==========

    /**
     * 验证红包对象有效性
     */
    protected void assertValidRedPacket(RedPacket packet) {
        assertNotNull(packet, "红包对象不应为null");
        assertNotNull(packet.getSize(), "红包大小不应为null");
        assertNotNull(packet.getShape(), "红包形状不应为null");
        assertTrue(packet.getAmount() > 0, "红包金额应大于0");
        assertFalse(packet.isCollected(), "红包初始状态应未收集");
        assertTrue(packet.getX() >= 0, "红包X坐标应>=0");
        assertTrue(packet.getY() >= 0, "红包Y坐标应>=0");
    }

    /**
     * 验证红包在边界内
     */
    protected void assertRedPacketInBounds(RedPacket packet, int width, int height) {
        assertTrue(packet.getX() >= 0, "红包X坐标应在边界内");
        assertTrue(packet.getY() >= 0, "红包Y坐标应在边界内");
        assertTrue(packet.getX() + packet.getWidth() <= width, "红包右边界应在边界内");
        assertTrue(packet.getY() + packet.getHeight() <= height, "红包下边界应在边界内");
    }

    /**
     * 验证两个红包不重叠
     */
    protected void assertRedPacketsNotOverlapping(RedPacket packet1, RedPacket packet2) {
        Rectangle rect1 = new Rectangle(packet1.getX(), packet1.getY(),
                packet1.getWidth(), packet1.getHeight());
        Rectangle rect2 = new Rectangle(packet2.getX(), packet2.getY(),
                packet2.getWidth(), packet2.getHeight());
        assertFalse(rect1.intersects(rect2), "红包不应重叠");
    }

    /**
     * 验证坦克有效性
     */
    protected void assertValidTank(Tank tank) {
        assertNotNull(tank, "坦克对象不应为null");
        assertTrue(tank.getX() >= 0, "坦克X坐标应>=0");
        assertTrue(tank.getY() >= 0, "坦克Y坐标应>=0");
        assertTrue(tank.getWidth() > 0, "坦克宽度应>0");
        assertTrue(tank.getHeight() > 0, "坦克高度应>0");
    }

    /**
     * 验证坦克在边界内
     */
    protected void assertTankInBounds(Tank tank, int width, int height) {
        assertTrue(tank.getX() >= 0, "坦克X坐标应在边界内");
        assertTrue(tank.getY() >= 0, "坦克Y坐标应在边界内");
        assertTrue(tank.getX() + tank.getWidth() <= width, "坦克右边界应在边界内");
        assertTrue(tank.getY() + tank.getHeight() <= height, "坦克下边界应在边界内");
    }

    // ========== 等待工具方法 ==========

    /**
     * 安全等待（用于定时测试）
     */
    protected void safeWait(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("等待被中断: " + e.getMessage());
        }
    }

    /**
     * 等待Swing事件分发
     */
    protected void waitForSwing() {
        try {
            SwingUtilities.invokeAndWait(() -> {});
        } catch (Exception e) {
            fail("等待Swing事件分发失败: " + e.getMessage());
        }
    }

    // ========== 测试配置方法 ==========

    /**
     * 设置测试随机种子
     */
    protected void setRandomSeed(long seed) {
        random = new Random(seed);
    }
}