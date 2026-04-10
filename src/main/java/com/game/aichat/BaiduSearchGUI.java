package com.game.aichat;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BaiduSearchGUI extends JFrame {
    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private StyledDocument doc;
    private HTMLEditorKit kit;

    public BaiduSearchGUI() {
        initializeUI(); // UI组件和依赖项在此方法中初始化
        displayWelcomeMessage();
    }

    private void initializeUI() {
        setTitle("智能搜索助手 - 百度千帆");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 242, 245));

        // 聊天显示区域
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setContentType("text/html"); // 支持HTML和超链接
        chatPane.setBackground(Color.WHITE);
        chatPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chatPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                openBrowser(e.getURL().toString());
            }
        });

        // *** 修复点：在chatPane创建后立即初始化doc和kit ***
        doc = chatPane.getStyledDocument();
        kit = new HTMLEditorKit();

        scrollPane = new JScrollPane(chatPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(new Color(240, 242, 245));

        inputField = new JTextField();
        inputField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        inputField.setBackground(Color.WHITE);

        sendButton = new JButton("搜索");
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        sendButton.setBackground(new Color(4, 164, 86));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        inputPanel.add(new JLabel("输入问题:"), BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // 事件监听
        sendButton.addActionListener(e -> performSearch());
        inputField.addActionListener(e -> performSearch());

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(new Color(3, 140, 74));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(new Color(4, 164, 86));
            }
        });
    }

    private void displayWelcomeMessage() {
        String welcomeHtml = "<div style='font-family:微软雅黑; padding: 10px;'>" +
                "<h3 style='color: #333;'>欢迎使用智能搜索助手</h3>" +
                "<p style='color: #666;'>我可以帮您在互联网上搜索信息。请在下方输入框中输入您的问题。</p>" +
                "<p style='color: #999; font-size: 12px;'></p>" +
                "</div><hr>";
        appendToChatPane(welcomeHtml);
    }

    private void performSearch() {
        String query = inputField.getText().trim();
        if (query.isEmpty()) return;

        inputField.setText("");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // 显示用户查询
        String userQueryHtml = String.format(
                "<div style='margin: 10px 0; padding: 10px; background-color: #e1f5fe; border-radius: 8px; text-align: right;'>" +
                        "<b style='color: #01579b;'>我 [%s]:</b> %s" +
                        "</div>",
                timestamp, query
        );
        appendToChatPane(userQueryHtml);

        // 显示“正在搜索”提示
        String searchingHtml = "<div id='searching' style='margin: 10px 0; padding: 10px; color: #888;'><i>正在搜索中，请稍候...</i></div>";
        appendToChatPane(searchingHtml);

        // 在后台线程执行搜索
        new Thread(() -> {
            try {
                // 使用 SearchService 来执行搜索
                SearchService searchService = new SearchService();
                List<SearchResult> results = searchService.callSearchAPI(query);
                SwingUtilities.invokeLater(() -> {
                    // 移除“正在搜索”提示
                    removeMessageById("searching");
                    // 显示搜索结果
                    displaySearchResults(query, results);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    removeMessageById("searching");
                    String errorHtml = String.format(
                            "<div style='margin: 10px 0; padding: 10px; background-color: #ffebee; border-radius: 8px; color: #c62828;'>" +
                                    "<b>搜索失败:</b> %s" +
                                    "</div>",
                            e.getMessage()
                    );
                    appendToChatPane(errorHtml);
                });
            }
        }).start();
    }

    private void displaySearchResults(String query, List<SearchResult> results) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<div style='font-family:微软雅黑; margin: 10px 0;'>");
        htmlBuilder.append(String.format("<h4 style='color: #333;'>关于 \"%s\" 的搜索结果 (%d条):</h4>", query, results.size()));

        if (results.isEmpty()) {
            htmlBuilder.append("<p style='color: #888;'>未找到相关信息。</p>");
        } else {
            for (int i = 0; i < results.size(); i++) {
                SearchResult r = results.get(i);
                htmlBuilder.append("<div style='border: 1px solid #e0e0e0; border-radius: 8px; margin-bottom: 10px; padding: 15px; background-color: #fafafa;'>");
                htmlBuilder.append(String.format("<h5 style='margin: 0 0 5px 0;'><a href='%s' style='color: #1a73e8; text-decoration: none;'>%d. %s</a></h5>", r.url, i + 1, r.title));
                htmlBuilder.append(String.format("<p style='margin: 5px 0; color: #555; font-size: 13px;'>%s</p>", r.content));
                htmlBuilder.append(String.format("<p style='margin: 5px 0; color: #006621; font-size: 11px;'>%s</p>", r.url));
                htmlBuilder.append("</div>");
            }
        }
        htmlBuilder.append("</div><hr>");
        appendToChatPane(htmlBuilder.toString());
    }

    private void appendToChatPane(String html) {
        SwingUtilities.invokeLater(() -> {
            try {
                kit.insertHTML((HTMLDocument) doc, doc.getLength(), html, 0, 0, null);
                chatPane.setCaretPosition(doc.getLength());
            } catch (Exception e) {
                // 如果HTML插入失败，则追加纯文本
                chatPane.setText(chatPane.getText() + "\n" + html.replaceAll("<[^>]*>", ""));
            }
        });
    }

    // 简单的移除消息的方法，通过ID移除（这里用HTML的id属性模拟）
    private void removeMessageById(String id) {
        SwingUtilities.invokeLater(() -> {
            String text = chatPane.getText();
            // 这只是一个简单的实现，对于复杂的HTML文档，可能需要更强大的解析库
            // 但对于移除固定的"正在搜索"提示，这足够了
            String startTag = "id='" + id + "'";
            int startIndex = text.indexOf(startTag);
            if (startIndex != -1) {
                int divStart = text.lastIndexOf("<div", startIndex);
                int endIndex = text.indexOf("</div>", startIndex);
                if (divStart != -1 && endIndex != -1) {
                    try {
                        doc.remove(divStart, endIndex + 6 - divStart); // +6 for "</div>"
                    } catch (Exception e) {
                        e.printStackTrace(); // 忽略移除失败
                    }
                }
            }
        });
    }

    private void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                JOptionPane.showMessageDialog(this, "无法打开浏览器，请手动访问链接:\n" + url, "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "打开链接时出错: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
