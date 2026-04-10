package com.game.redpacket;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 游戏主面板 - 负责游戏逻辑和渲染
 */
public class RedPacketGamePanel extends JPanel {
    // 游戏状态枚举
    public enum GameState { READY, PLAYING, PAUSED, GAME_OVER }

    // 游戏状态
    private GameState gameState = GameState.READY;
    private Tank playerTank;
    private List<RedPacket> redPackets;
    private Random random;

    // 游戏统计
    private int score = 0;
    private double totalMoneyCollected = 0;
    private int packetsCollected = 0;
    private long gameStartTime;
    private long gameDuration = 60000; // 60秒游戏时间
    private long remainingTime;

    // 游戏计时器
    private Timer gameTimer;
    private static final int FPS = 60;
    private static final int TIMER_DELAY = 1000 / FPS;

    // 游戏配置
    private int initialPacketCount = 20;
    private int maxPackets = 30;
    private int packetSpawnRate = 60; // 每60帧生成一个新红包

    // 帧计数器
    private int frameCount = 0;

    // 图像资源
    private Image backgroundImage;
    private Font gameFont;

    // 监听器
    private GameEventListener gameEventListener;

    public RedPacketGamePanel() {
        random = new Random();  // 新增：提前初始化
        initializePanel();
        initializeGame();
        initializeListeners();
    }


    private void initializePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        // 创建背景图像
        createBackground();

        // 设置字体
        gameFont = new Font("微软雅黑", Font.BOLD, 14);

        random = new Random();
    }

    private void initializeGame() {
        // 从数据库加载初始金额
        double initialMoney = RedPacketDAO.getTotalMoney();
        totalMoneyCollected = initialMoney;

        // 创建玩家坦克（初始位置在屏幕中央底部）
        playerTank = new Tank(370, 500);

        // 创建初始红包
        redPackets = new ArrayList<>();
        spawnInitialPackets();

        // 初始化游戏计时器
        gameTimer = new Timer(TIMER_DELAY, new GameLoop());
    }

    private void initializeListeners() {
        addKeyListener(new GameKeyListener());
    }

    private void createBackground() {
        if (random == null) random = new Random();  // 防御性检查
        // 创建渐变背景
        int width = 800, height = 600;
        BufferedImage bg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bg.createGraphics();

        // 创建渐变背景
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(30, 30, 70),
                width, height, new Color(10, 10, 30)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // 添加星空效果
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int size = random.nextInt(2) + 1;
            g2d.fillOval(x, y, size, size);
        }

        g2d.dispose();
        backgroundImage = bg;
    }

    private void spawnInitialPackets() {
        for (int i = 0; i < initialPacketCount; i++) {
            spawnRedPacket();
        }
    }

    private void spawnRedPacket() {
        if (redPackets.size() >= maxPackets) return;

        // 确保尺寸有效
        int width = getWidth();
        int height = getHeight();
        if (width <= 100 || height <= 150) {
            width = 800;   // 默认宽度
            height = 600;  // 默认高度
        }

        int x = random.nextInt(width - 100) + 50;
        int y = random.nextInt(height - 150) + 50;

        RedPacket packet = RedPacketFactory.createRandomRedPacket(x, y);
        redPackets.add(packet);
    }



    /**
     * 开始游戏
     */
    public void startGame() {
        if (gameState == GameState.PLAYING) return;

        gameState = GameState.PLAYING;
        gameStartTime = System.currentTimeMillis();
        remainingTime = gameDuration;

        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }

        if (gameEventListener != null) {
            gameEventListener.onGameStart();
        }
    }

    /**
     * 暂停游戏
     */
    public void pauseGame() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            gameTimer.stop();

            if (gameEventListener != null) {
                gameEventListener.onGamePause();
            }
        }
    }


    /**
     * 继续游戏
     */
    public void resumeGame() {
        if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            // 修复：重置坦克移动状态
            playerTank.resetMovementState();
            gameTimer.start();
            this.requestFocus();
            if (gameEventListener != null) {
                gameEventListener.onGameResume();
            }
        }
    }


    /**
     * 重新开始游戏
     */
    public void restartGame() {
        // 重置游戏状态
        gameState = GameState.READY;
        score = 0;
        totalMoneyCollected = 0;
        packetsCollected = 0;
        frameCount = 0;

        // 重置坦克
        playerTank.setPosition(370, 500);
        playerTank.reset();

        // 清空红包并重新生成
        redPackets.clear();
        spawnInitialPackets();

        // 重置时间
        remainingTime = gameDuration;

        repaint();

        if (gameEventListener != null) {
            gameEventListener.onGameRestart();
        }
    }

    /**
     * 结束游戏
     */
    private void endGame() {
        gameState = GameState.GAME_OVER;
        gameTimer.stop();

        if (gameEventListener != null) {
            gameEventListener.onGameOver(score, totalMoneyCollected, packetsCollected);
        }
    }

    /**
     * 游戏主循环
     */
    private class GameLoop implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameState != GameState.PLAYING) return;

            updateGame();
            repaint();
            frameCount++;
        }
    }

    private void updateGame() {
        // 更新剩余时间
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - gameStartTime;
        remainingTime = Math.max(0, gameDuration - elapsed);

        // 检查游戏是否结束
        if (remainingTime <= 0) {
            endGame();
            return;
        }

        // 更新坦克
        playerTank.update();
        playerTank.checkBoundaryCollision(getWidth(), getHeight());

        // 定期生成新红包
        if (frameCount % packetSpawnRate == 0 && redPackets.size() < maxPackets) {
            spawnRedPacket();
        }

        // 更新红包位置和碰撞检测
        updateRedPackets();

        // 检测坦克与红包的碰撞
        checkCollisions();
    }

    private void updateRedPackets() {
        for (int i = redPackets.size() - 1; i >= 0; i--) {
            RedPacket packet = redPackets.get(i);

            if (packet.isCollected()) {
                // 移除已被收集一段时间的红包
                if (System.currentTimeMillis() - packet.getCollectionTime() > 1000) {
                    redPackets.remove(i);
                }
                continue;
            }

            // 更新红包位置
            packet.move();
            packet.checkBoundaryCollision(getWidth(), getHeight());
        }
    }

    private void checkCollisions() {
        for (RedPacket packet : redPackets) {
            if (packet.isCollected()) continue;

            if (playerTank.checkCollision(packet)) {
                // 收集红包
                collectRedPacket(packet);
            }
        }
    }

    private void collectRedPacket(RedPacket packet) {
        packet.collect();
        playerTank.setHit(true);

        // 更新统计
        double amount = packet.getAmount();
        totalMoneyCollected += amount;
        packetsCollected++;
        score += (int)(amount * 10);

        // 更新数据库
        RedPacketDAO.updateTotalMoney(amount);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 绘制背景
        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // 绘制游戏元素
        drawGameElements(g2d);

        // 绘制UI
        drawUI(g2d);

        // 绘制游戏状态信息
        drawGameState(g2d);
    }

    private void drawGameElements(Graphics2D g2d) {
        // 绘制红包
        for (RedPacket packet : redPackets) {
            packet.draw(g2d);
        }

        // 绘制坦克
        playerTank.draw(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        g2d.setFont(gameFont);

        // 绘制游戏信息面板
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(10, 10, 200, 100, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.drawString("时间: " + formatTime(remainingTime), 20, 30);
        g2d.drawString("分数: " + score, 20, 50);
        g2d.drawString("金额: ¥" + String.format("%.2f", totalMoneyCollected), 20, 70);
        g2d.drawString("红包: " + packetsCollected + "个", 20, 90);

        // 绘制控制提示
        if (gameState == GameState.READY) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("按空格键开始游戏", getWidth()/2 - 60, getHeight()/2);
        }
    }

    private void drawGameState(Graphics2D g2d) {
        if (gameState == GameState.PAUSED) {
            int centerX = getWidth() / 2;
            int startY = getHeight() / 2 - 40; // 起始Y坐标

            drawCenteredStringAt(g2d, "游戏暂停", Color.YELLOW, 24, centerX, startY);
            drawCenteredStringAt(g2d, "按空格键继续", Color.WHITE, 16, centerX, startY + 35);
        } else if (gameState == GameState.GAME_OVER) {
            int centerX = getWidth() / 2;
            int startY = getHeight() / 2 - 60; // 起始Y坐标，给更多空间

            drawCenteredStringAt(g2d, "游戏结束!", Color.RED, 32, centerX, startY);
            drawCenteredStringAt(g2d, "最终分数: " + score, Color.YELLOW, 24, centerX, startY + 45);
            drawCenteredStringAt(g2d, "总金额: ¥" + String.format("%.2f", totalMoneyCollected), Color.YELLOW, 24, centerX, startY + 80);
            drawCenteredStringAt(g2d, "按R键重新开始", Color.WHITE, 16, centerX, startY + 120);
        }
    }

    // 新增方法：在指定位置绘制居中文字
    private void drawCenteredStringAt(Graphics2D g2d, String text, Color color, int fontSize, int centerX, int y) {
        Font originalFont = g2d.getFont();
        g2d.setFont(new Font("微软雅黑", Font.BOLD, fontSize));
        g2d.setColor(color);

        FontMetrics fm = g2d.getFontMetrics();
        int x = centerX - fm.stringWidth(text) / 2;

        g2d.drawString(text, x, y);
        g2d.setFont(originalFont);
    }


    private void drawCenteredString(Graphics2D g2d, String text, Color color, int fontSize) {
        Font originalFont = g2d.getFont();
        g2d.setFont(new Font("微软雅黑", Font.BOLD, fontSize));
        g2d.setColor(color);

        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2 - fontSize;

        g2d.drawString(text, x, y);
        g2d.setFont(originalFont);
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    /**
     * 键盘监听器
     */
    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameState == GameState.PLAYING) {
                playerTank.keyPressed(e);

                // 暂停游戏
                if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    pauseGame();
                }
            } else if (gameState == GameState.READY) {
                // 开始游戏
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startGame();
                }
            } else if (gameState == GameState.PAUSED) {
                // 继续游戏
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    resumeGame();
                }
            } else if (gameState == GameState.GAME_OVER) {
                // 重新开始游戏
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();
                }
            }

            // 退出游戏（ESC键）
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (gameEventListener != null) {
                    gameEventListener.onGameExit();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (gameState == GameState.PLAYING) {
                playerTank.keyReleased(e);
            }
        }
    }

    // Getter 和 Setter 方法
    public GameState getGameState() { return gameState; }
    public int getScore() { return score; }
    public double getTotalMoneyCollected() { return totalMoneyCollected; }
    public int getPacketsCollected() { return packetsCollected; }
    public long getRemainingTime() { return remainingTime; }

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    public void setGameDuration(long duration) {
        this.gameDuration = duration;
        this.remainingTime = duration;
    }

    /**
     * 游戏事件监听器接口
     */
    public interface GameEventListener {
        void onGameStart();
        void onGamePause();
        void onGameResume();
        void onGameOver(int score, double totalMoney, int packetsCollected);
        void onGameRestart();
        void onGameExit();
        void onRedPacketCollected(RedPacket packet, double amount);
    }
}