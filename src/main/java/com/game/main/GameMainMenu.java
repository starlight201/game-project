package com.game.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameMainMenu extends JFrame {
    private static final String APP_TITLE = "游戏中心 - 综合管理系统";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    public GameMainMenu() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(APP_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 设置主面板
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(new Color(240, 248, 255));

        // 添加标题
        JLabel titleLabel = new JLabel("游戏中心 - 综合管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建功能按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 30, 30));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // 创建功能按钮 - 修改按钮文字颜色为黑色
        JButton redPacketButton = createFunctionButton(
                "鸭子抢红包",
                "",
                new Color(220, 20, 60),
                e -> launchRedPacketGame()
        );

        JButton dressUpButton = createFunctionButton(
                "装扮鸭子",
                "",
                new Color(30, 144, 255),
                e -> launchDressUpGame()
        );

        JButton codeCounterButton = createFunctionButton(
                "代码量统计",
                "",
                new Color(34, 139, 34),
                e -> launchCodeAnalyzer()
        );

        JButton aiChatButton = createFunctionButton(
                "AI对话",
                "",
                new Color(255, 140, 0),
                e -> launchAIChat()
        );

        JButton attendanceButton = createFunctionButton(
                "唐老师点名",
                "",
                new Color(138, 43, 226),
                e -> launchAttendanceSystem()
        );

        JButton exitButton = createFunctionButton(
                "退出系统",
                "",
                new Color(128, 128, 128),
                e -> exitApplication()
        );

        // 添加按钮到面板
        buttonPanel.add(redPacketButton);
        buttonPanel.add(dressUpButton);
        buttonPanel.add(codeCounterButton);
        buttonPanel.add(aiChatButton);
        buttonPanel.add(attendanceButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // 添加底部信息
        JLabel footerLabel = new JLabel(
                "© 2025 游戏中心 - 数据已存储在数据库中",
                SwingConstants.CENTER
        );
        footerLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createFunctionButton(String title, String description,
                                         Color bgColor, ActionListener listener) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br/>" +
                description + "</center></html>");
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // 修改为黑色字体
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);

        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void launchRedPacketGame() {
        SwingUtilities.invokeLater(() -> {
            // 创建游戏窗口并设置监听器
            com.game.redpacket.RedPacketGameFrame game = new com.game.redpacket.RedPacketGameFrame(
                    new com.game.redpacket.RedPacketGameFrame.GameEventListener() {
                        @Override
                        public void onGameStart() {
                            // 游戏开始时的处理
                        }

                        @Override
                        public void onGamePause() {
                            // 游戏暂停时的处理
                        }

                        @Override
                        public void onGameResume() {
                            // 游戏继续时的处理
                        }

                        @Override
                        public void onGameOver(int score, double totalMoney, int packetsCollected) {
                            // 游戏结束时的处理
                        }

                        @Override
                        public void onGameRestart() {
                            // 游戏重启时的处理
                        }

                        @Override
                        public void onGameExit() {
                            // 游戏退出时显示主界面
                            SwingUtilities.invokeLater(() -> {
                                GameMainMenu.this.setVisible(true);
                            });
                        }

                        @Override
                        public void onRedPacketCollected(com.game.redpacket.RedPacket packet, double amount) {
                            // 收集红包时的处理
                        }
                    }
            );
            game.setVisible(true);
            // 隐藏主界面
            this.setVisible(false);
        });
    }

    private void launchDressUpGame() {
        SwingUtilities.invokeLater(() -> {
            com.game.dressup.DuckDressUpApp game = new com.game.dressup.DuckDressUpApp();
            game.setVisible(true);
            // 隐藏主界面
            this.setVisible(false);

            // 添加窗口关闭监听器，当游戏窗口关闭时显示主界面
            game.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    SwingUtilities.invokeLater(() -> {
                        GameMainMenu.this.setVisible(true);
                    });
                }
            });
        });
    }

    private void launchCodeAnalyzer() {
        SwingUtilities.invokeLater(() -> {
            com.game.codecounter.CodeAnalyzerGUI analyzer = new com.game.codecounter.CodeAnalyzerGUI();
            analyzer.setVisible(true);
            // 隐藏主界面
            this.setVisible(false);

            // 添加窗口关闭监听器
            analyzer.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    SwingUtilities.invokeLater(() -> {
                        GameMainMenu.this.setVisible(true);
                    });
                }
            });
        });
    }

    private void launchAIChat() {
        SwingUtilities.invokeLater(() -> {
            com.game.aichat.BaiduSearchGUI chat = new com.game.aichat.BaiduSearchGUI();
            chat.setVisible(true);
            // 隐藏主界面
            this.setVisible(false);

            // 添加窗口关闭监听器
            chat.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    SwingUtilities.invokeLater(() -> {
                        GameMainMenu.this.setVisible(true);
                    });
                }
            });
        });
    }

    private void launchAttendanceSystem() {
        SwingUtilities.invokeLater(() -> {
            com.game.attendance.TeacherTangAttendanceSystem attendance =
                    new com.game.attendance.TeacherTangAttendanceSystem();
            attendance.setVisible(true);
            // 隐藏主界面
            this.setVisible(false);

            // 添加窗口关闭监听器
            attendance.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    SwingUtilities.invokeLater(() -> {
                        GameMainMenu.this.setVisible(true);
                    });
                }
            });
        });
    }

    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "确定要退出游戏中心吗？",
                "退出确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 启动主菜单
        SwingUtilities.invokeLater(() -> {
            GameMainMenu mainMenu = new GameMainMenu();
            mainMenu.setVisible(true);
        });
    }
}
