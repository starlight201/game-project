package com.game.redpacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * 抢红包游戏主窗口
 */
public class RedPacketGameFrame extends JFrame {
    private RedPacketGamePanel gamePanel;
    private JLabel statusBar;
    private JButton startButton;
    private JButton pauseButton;
    private JButton restartButton;
    private JButton exitButton;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel moneyLabel;

    // 游戏配置
    private int gameDuration = 60; // 默认60秒游戏时间
    private GameEventListener externalEventListener;

    public RedPacketGameFrame() {
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupListeners();
        showWelcomeDialog();
    }

    public RedPacketGameFrame(GameEventListener externalListener) {
        this();
        this.externalEventListener = externalListener;
    }

    private void initializeFrame() {
        setTitle("鸭子抢红包");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // 设置应用图标
        setIconImage(createAppIcon());
    }

    private Image createAppIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();

        // 绘制红包图标
        g2d.setColor(Color.RED);
        g2d.fillRoundRect(4, 4, 24, 24, 8, 8);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("¥", 12, 18);

        g2d.dispose();
        return icon;
    }

    private void initializeComponents() {
        // 创建游戏面板
        gamePanel = new RedPacketGamePanel();
        gamePanel.setPreferredSize(new Dimension(800, 600));

        // 使用对比度更好的颜色
        startButton = createStyledButton("开始游戏", new Color(20, 50, 30));   // 更暗的绿色
        pauseButton = createStyledButton("暂停游戏", new Color(100, 20, 0));    // 更暗的橙色
        restartButton = createStyledButton("重新开始", new Color(10, 50, 100));  // 更暗的蓝色
        exitButton = createStyledButton("退出游戏", new Color(100, 20, 20));    // 更暗的红色

        // 初始状态设置
        pauseButton.setEnabled(false);
        restartButton.setEnabled(false);

        // 创建状态标签
        scoreLabel = createStatusLabel("分数: 0");
        timeLabel = createStatusLabel("时间: 60秒");
        moneyLabel = createStatusLabel("金额: ¥0.00");

        // 设置游戏事件监听器
        gamePanel.setGameEventListener(new RedPacketGamePanel.GameEventListener() {
            @Override
            public void onGameStart() {
                updateStatusBar("游戏进行中...");
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                restartButton.setEnabled(true);
                if (externalEventListener != null) {
                    externalEventListener.onGameStart();
                }
            }

            @Override
            public void onGamePause() {
                updateStatusBar("游戏已暂停");
                startButton.setText("继续游戏");
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                if (externalEventListener != null) {
                    externalEventListener.onGamePause();
                }
            }

            @Override
            public void onGameResume() {
                updateStatusBar("游戏继续...");
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                if (externalEventListener != null) {
                    externalEventListener.onGameResume();
                }
            }

            @Override
            public void onGameOver(int score, double totalMoney, int packetsCollected) {
                updateStatusBar("游戏结束! 最终分数: " + score);
                startButton.setEnabled(false);
                pauseButton.setEnabled(false);
                restartButton.setEnabled(true);

                // 显示游戏结果对话框
                showGameOverDialog(score, totalMoney, packetsCollected);

                if (externalEventListener != null) {
                    externalEventListener.onGameOver(score, totalMoney, packetsCollected);
                }
            }

            @Override
            public void onGameRestart() {
                updateStatusBar("游戏重新开始");
                startButton.setText("开始游戏");
                startButton.setEnabled(false);
                pauseButton.setEnabled(false);
                restartButton.setEnabled(false);
                if (externalEventListener != null) {
                    externalEventListener.onGameRestart();
                }
            }

            @Override
            public void onGameExit() {
                if (externalEventListener != null) {
                    externalEventListener.onGameExit();
                }
                dispose();
            }

            @Override
            public void onRedPacketCollected(RedPacket packet, double amount) {
                updateGameInfo();
                if (externalEventListener != null) {
                    externalEventListener.onRedPacketCollected(packet, amount);
                }
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);

        // 使用更对比的颜色组合
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);  // 保持白色文字


        button.setFont(new Font("微软雅黑", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),  // 添加边框增强对比
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 改进鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color hoverColor = isLightColor(bgColor) ? bgColor.darker() : bgColor.brighter();
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
    // 辅助方法：判断颜色是否为浅色
    private boolean isLightColor(Color color) {
        // 计算颜色的亮度（0-1之间，1为最亮）
        double brightness = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        // 如果亮度大于0.5，认为是浅色
        return brightness > 0.5;
    }


    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.BOLD, 12));
        label.setForeground(Color.BLUE);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(5, 5));

        // 顶部面板 - 游戏信息
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.NORTH);

        // 中央面板 - 游戏区域
        add(gamePanel, BorderLayout.CENTER);

        // 底部面板 - 控制按钮和状态栏
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.setBackground(new Color(240, 240, 240));

        // 游戏标题
        JLabel titleLabel = new JLabel("鸭子抢红包", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(200, 0, 0));

        // 游戏信息面板
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        statsPanel.setOpaque(false);
        statsPanel.add(scoreLabel);
        statsPanel.add(timeLabel);
        statsPanel.add(moneyLabel);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        // 状态栏
        statusBar = new JLabel("准备开始游戏 - 使用方向键或WASD控制坦克移动，空格键暂停");
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        statusBar.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);

        return panel;
    }

    private void setupListeners() {
        // 开始/继续游戏按钮
        startButton.addActionListener(e -> {
            if (gamePanel.getGameState() == RedPacketGamePanel.GameState.READY) {
                gamePanel.startGame();
                startButton.setEnabled(false);
            } else if (gamePanel.getGameState() == RedPacketGamePanel.GameState.PAUSED) {
                gamePanel.resumeGame();
                startButton.setText("开始游戏");
            }
        });

        // 暂停游戏按钮
        pauseButton.addActionListener(e -> {
            gamePanel.pauseGame();
            startButton.setText("继续游戏");
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });

        // 重新开始按钮
        restartButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "确定要重新开始游戏吗？当前进度将丢失。",
                    "重新开始游戏",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                gamePanel.restartGame();
                updateGameInfo();
            }
        });

        // 退出游戏按钮
        exitButton.addActionListener(e -> exitGame());

        // 窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });

        // 定时更新游戏信息
        Timer infoTimer = new Timer(100, e -> updateGameInfo());
        infoTimer.start();
    }

    private void updateGameInfo() {
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText("分数: " + gamePanel.getScore());
            timeLabel.setText("时间: " + formatTime(gamePanel.getRemainingTime()));
            moneyLabel.setText("金额: ¥" + String.format("%.2f", gamePanel.getTotalMoneyCollected()));
        });
    }

    private void updateStatusBar(String message) {
        SwingUtilities.invokeLater(() -> {
            statusBar.setText(message);
        });
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        return String.format("%02d秒", seconds);
    }

    private void showWelcomeDialog() {
        JOptionPane.showMessageDialog(
                this,
                "欢迎来到鸭子抢红包！\n\n" +
                        "游戏规则：\n" +
                        "• 使用方向键或WASD控制鸭子移动\n" +
                        "• 收集各种形状的红包获得金钱和分数\n" +
                        "• 躲避障碍物，在限定时间内获得最高分\n" +
                        "• 按空格键暂停游戏\n\n" +
                        "红包类型：\n" +
                        "• 方形红包：基础金额\n" +
                        "• 圆形红包：中等金额\n" +
                        "• 三角形红包：较高金额\n" +
                        "• 星形红包：最高金额\n\n" +
                        "祝您游戏愉快！",
                "游戏说明",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showGameOverDialog(int score, double totalMoney, int packetsCollected) {
        String message = String.format(
                "游戏结束！\n\n" +
                        "最终成绩：\n" +
                        "• 得分：%d分\n" +
                        "• 收集红包：%d个\n" +
                        "• 总金额：¥%.2f元\n\n" +
                        "是否要重新开始游戏？",
                score, packetsCollected, totalMoney
        );

        int result = JOptionPane.showConfirmDialog(
                this,
                message,
                "游戏结束",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            gamePanel.restartGame();
        }
    }

    private void exitGame() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定要退出游戏吗？",
                "退出游戏",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            if (externalEventListener != null) {
                externalEventListener.onGameExit();
            }
            dispose();
        }
    }

    /**
     * 设置游戏时长
     */
    public void setGameDuration(int seconds) {
        this.gameDuration = seconds;
        gamePanel.setGameDuration(seconds * 1000L);
    }

    /**
     * 显示游戏窗口
     */
    public void showGame() {
        setVisible(true);
        gamePanel.requestFocusInWindow(); // 确保游戏面板获得焦点
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

    /**
     * 主方法 - 用于独立测试
     */
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            RedPacketGameFrame gameFrame = new RedPacketGameFrame();
            gameFrame.showGame();
        });
    }
}