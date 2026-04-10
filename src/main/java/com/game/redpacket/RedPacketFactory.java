package com.game.redpacket;

import java.util.Random;

/**
 * 红包工厂类 - 使用工厂模式创建不同类型的红包
 */
public class RedPacketFactory {
    private static final Random random = new Random();

    /**
     * 创建随机红包
     */
    public static RedPacket createRandomRedPacket(int x, int y) {
        RedPacketSize size = getRandomSize();
        RedPacketShape shape = getRandomShape();
        return createRedPacket(x, y, size, shape);
    }

    /**
     * 创建指定类型和大小的红包
     */
    public static RedPacket createRedPacket(int x, int y, RedPacketSize size, RedPacketShape shape) {
        switch (shape) {
            case SQUARE:
                return new SquareRedPacket(x, y, size);
            case CIRCLE:
                return new CircleRedPacket(x, y, size);
            case TRIANGLE:
                return new TriangleRedPacket(x, y, size);
            case STAR:
                return new StarRedPacket(x, y, size);
            default:
                return new SquareRedPacket(x, y, size);
        }
    }

    /**
     * 批量创建红包
     */
    public static RedPacket[] createRedPackets(int count, int maxX, int maxY) {
        RedPacket[] packets = new RedPacket[count];
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(Math.max(1, maxX - 50));
            int y = random.nextInt(Math.max(1, maxY - 50));
            packets[i] = createRandomRedPacket(x, y);
        }
        return packets;
    }

    private static RedPacketSize getRandomSize() {
        RedPacketSize[] sizes = RedPacketSize.values();
        return sizes[random.nextInt(sizes.length)];
    }

    private static RedPacketShape getRandomShape() {
        RedPacketShape[] shapes = RedPacketShape.values();
        return shapes[random.nextInt(shapes.length)];
    }

    /**
     * 根据名称创建红包
     */
    public static RedPacket createFromName(int x, int y, String sizeName, String shapeName) {
        RedPacketSize size = RedPacketSize.fromName(sizeName);
        RedPacketShape shape = RedPacketShape.fromName(shapeName);
        return createRedPacket(x, y, size, shape);
    }
}