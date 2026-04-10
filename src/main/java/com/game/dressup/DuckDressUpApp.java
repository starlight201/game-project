package com.game.dressup;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * 鸭子装扮应用主类
 * 使用Swing实现GUI界面，管理鸭子装扮功能
 */
public class DuckDressUpApp extends JFrame implements DressUpManager.DressUpListener {

    // ========== 常量定义 ==========
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 900;
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Color PRIMARY_COLOR = new Color(25, 25, 112);
    private static final Color SECONDARY_COLOR = new Color(70, 130, 180);

    // ========== 成员变量 ==========

    // 管理器
    private DressUpManager dressUpManager;
    private DuckImageGenerator imageGenerator;
    private Person currentDuck;

    // UI组件
    private JPanel mainPanel;
    private JLabel duckImageLabel;
    private JTextArea infoTextArea;
    private JLabel moneyLabel;
    private JLabel levelLabel;
    private JLabel styleScoreLabel;
    private JLabel totalValueLabel;

    // 列表和模型
    private JList<DressUpItem> availableItemsList;
    private DefaultListModel<DressUpItem> availableItemsModel;
    private JList<DressUpItem> currentOutfitList;
    private DefaultListModel<DressUpItem> currentOutfitModel;

    // 按钮
    private JButton selectDonaldButton;
    private JButton selectLittleDuckButton;
    private JButton applyItemButton;
    private JButton removeItemButton;
    private JButton clearOutfitButton;
    private JButton purchaseItemButton;
    private JButton saveOutfitButton;
    private JButton viewSavedOutfitsButton;  // 新增：查看已保存装扮按钮
    private JButton shareButton;
    private JButton saveImageButton;
    private JButton exitButton;

    // 筛选控件
    private JComboBox<String> filterComboBox;
    private JCheckBox ownedOnlyCheckBox;

    // ========== 构造方法 ==========

    public DuckDressUpApp() {
        super("鸭子装扮工坊 - 打造专属时尚造型");
        initApplication();
    }

    // ========== 初始化方法 ==========

    private void initApplication() {
        setApplicationIcon();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);


        initComponents();
        setupLayout();
        setupListeners();
        setupManager();
        showWelcomeDialog();
    }

    private void initComponents() {
        dressUpManager = DressUpManager.getInstance();
        imageGenerator = new DuckImageGenerator();

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        initStatusLabels();
        initListModels();
        configureLists();
        initButtons();
        initFilterControls();
        initDisplayAreas();
        applyComponentStyles();
    }

    private void initStatusLabels() {
        moneyLabel = createStatusLabel("金钱: $0.00");
        levelLabel = createStatusLabel("等级: 1");
        styleScoreLabel = createStatusLabel("时尚评分: 0");
        totalValueLabel = createStatusLabel("装扮价值: $0.00");
    }

    private void initListModels() {
        availableItemsModel = new DefaultListModel<>();
        currentOutfitModel = new DefaultListModel<>();

        availableItemsList = new JList<>(availableItemsModel);
        currentOutfitList = new JList<>(currentOutfitModel);
    }

    private void configureLists() {
        availableItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableItemsList.setCellRenderer(new DressUpItemRenderer());
        availableItemsList.setFixedCellHeight(60);

        currentOutfitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        currentOutfitList.setCellRenderer(new DressUpItemRenderer());
        currentOutfitList.setFixedCellHeight(60);
    }

    private void initButtons() {
        selectDonaldButton = createStyledButton("选择唐小鸭一号", new Color(30, 144, 255));
        selectLittleDuckButton = createStyledButton("选择唐小鸭二号", new Color(255, 140, 0));
        applyItemButton = createStyledButton("应用装扮", new Color(50, 205, 50));
        removeItemButton = createStyledButton("移除装扮", new Color(220, 20, 60));
        clearOutfitButton = createStyledButton("清空装扮", new Color(255, 69, 0));
        purchaseItemButton = createStyledButton("购买装扮", new Color(255, 215, 0));
        saveOutfitButton = createStyledButton("保存装扮", new Color(138, 43, 226));
        viewSavedOutfitsButton = createStyledButton("查看已保存装扮", new Color(128, 0, 128)); // 新增：紫色按钮
        shareButton = createStyledButton("分享装扮", new Color(32, 178, 170));
        saveImageButton = createStyledButton("保存图片", new Color(70, 130, 180));
        exitButton = createStyledButton("退出", new Color(128, 128, 128));
    }

    private void initFilterControls() {
        String[] filters = {"全部", "帽子", "眼镜", "围巾", "领带", "手杖"};
        filterComboBox = new JComboBox<>(filters);
        ownedOnlyCheckBox = new JCheckBox("仅显示已拥有");
    }

    private void initDisplayAreas() {
        duckImageLabel = new JLabel("", JLabel.CENTER);
        duckImageLabel.setPreferredSize(new Dimension(450, 550)); // 放大鸭子形象框

        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setLineWrap(true);
        infoTextArea.setWrapStyleWord(true);
    }

    private void setupLayout() {
        setContentPane(createMainLayout());
        pack();
        setMinimumSize(new Dimension(1200, 800));
    }

    private void setupListeners() {
        setupButtonListeners();
        setupListSelectionListeners();
        setupFilterListeners();
        setupWindowListeners();
    }

    private void setupButtonListeners() {
        selectDonaldButton.addActionListener(e -> selectDuckOne());
        selectLittleDuckButton.addActionListener(e -> selectDuckTwo());
        applyItemButton.addActionListener(e -> applySelectedItem());
        removeItemButton.addActionListener(e -> removeSelectedItem());
        clearOutfitButton.addActionListener(e -> clearOutfit());
        purchaseItemButton.addActionListener(e -> purchaseSelectedItem());
        saveOutfitButton.addActionListener(e -> saveCurrentOutfit());
        viewSavedOutfitsButton.addActionListener(e -> viewSavedOutfits()); // 新增：查看已保存装扮事件
        shareButton.addActionListener(e -> shareOutfit());
        saveImageButton.addActionListener(e -> saveDuckImage());
        exitButton.addActionListener(e -> exitApplication());
    }

    private void setupListSelectionListeners() {
        availableItemsList.addListSelectionListener(e -> updateButtonStates());
        currentOutfitList.addListSelectionListener(e -> updateButtonStates());
    }

    private void setupFilterListeners() {
        filterComboBox.addActionListener(e -> filterItems());
        ownedOnlyCheckBox.addActionListener(e -> filterItems());
    }

    private void setupWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });
    }

    private void setupManager() {
        dressUpManager.addListener(this);
        selectDuckOne();
    }

    // ========== UI创建方法 ==========

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        panel.add(createTopPanel(), BorderLayout.NORTH);
        panel.add(createCenterPanel(), BorderLayout.CENTER);
        panel.add(createBottomPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMainLayout() {
        JPanel layoutPanel = new JPanel(new BorderLayout(0, 0));
        layoutPanel.setBackground(BACKGROUND_COLOR);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(BACKGROUND_COLOR);

        contentPanel.add(createTopPanel(), BorderLayout.NORTH);
        contentPanel.add(createCenterPanel(), BorderLayout.CENTER);
        contentPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        layoutPanel.add(contentPanel, BorderLayout.CENTER);
        return layoutPanel;
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = createTitleLabel("🦆 鸭子装扮工坊");
        JPanel statusPanel = createStatusPanel();

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        panel.add(createLeftPanel());
        panel.add(createCenterLeftPanel());
        panel.add(createRightPanel());

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createTitledBorder("🦆 鸭子选择"));
        panel.setBackground(Color.WHITE);

        JPanel duckSelectionPanel = createDuckSelectionPanel();
        JScrollPane imageScrollPane = createImageScrollPane();
        JScrollPane infoScrollPane = createInfoScrollPane();

        panel.add(duckSelectionPanel, BorderLayout.NORTH);
        panel.add(imageScrollPane, BorderLayout.CENTER);
        panel.add(infoScrollPane, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createTitledBorder("🛍️ 可用装扮"));
        panel.setBackground(Color.WHITE);

        JPanel filterPanel = createFilterPanel();
        JScrollPane availableItemsScrollPane = createAvailableItemsScrollPane();
        JPanel buttonPanel = createAvailableItemsButtonPanel();

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(availableItemsScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(createTitledBorder("👑 当前装扮"));
        panel.setBackground(Color.WHITE);

        JScrollPane currentOutfitScrollPane = createCurrentOutfitScrollPane();
        JPanel buttonPanel = createCurrentOutfitButtonPanel();

        panel.add(currentOutfitScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        panel.add(viewSavedOutfitsButton); // 新增：将按钮添加到底部面板
        panel.add(shareButton);
        panel.add(saveImageButton);
        panel.add(exitButton);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setBackground(BACKGROUND_COLOR);

        panel.add(moneyLabel);
        panel.add(levelLabel);
        panel.add(styleScoreLabel);
        panel.add(totalValueLabel);

        return panel;
    }

    private JPanel createDuckSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(selectDonaldButton);
        panel.add(selectLittleDuckButton);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(new JLabel("筛选:"));
        panel.add(filterComboBox);
        panel.add(ownedOnlyCheckBox);

        return panel;
    }

    private JPanel createAvailableItemsButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(applyItemButton);
        panel.add(purchaseItemButton);

        return panel;
    }

    private JPanel createCurrentOutfitButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(removeItemButton);
        panel.add(clearOutfitButton);
        panel.add(saveOutfitButton);

        return panel;
    }

    private JScrollPane createImageScrollPane() {
        JScrollPane scrollPane = new JScrollPane(duckImageLabel);
        scrollPane.setPreferredSize(new Dimension(450, 550)); // 放大鸭子形象框
        return scrollPane;
    }

    private JScrollPane createInfoScrollPane() {
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        scrollPane.setPreferredSize(new Dimension(450, 200)); // 与鸭子形象框保持一致
        return scrollPane;
    }

    private JScrollPane createAvailableItemsScrollPane() {
        JScrollPane scrollPane = new JScrollPane(availableItemsList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        return scrollPane;
    }

    private JScrollPane createCurrentOutfitScrollPane() {
        JScrollPane scrollPane = new JScrollPane(currentOutfitList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        return scrollPane;
    }

    // ========== 业务逻辑方法 ==========

    private void selectDuckOne() {
        currentDuck = new LittleDuck(1);
        dressUpManager.setCurrentDuck(currentDuck);
        updateDuckDisplay();
    }

    private void selectDuckTwo() {
        currentDuck = new LittleDuck(2);
        dressUpManager.setCurrentDuck(currentDuck);
        updateDuckDisplay();
    }


    private void applySelectedItem() {
        DressUpItem selectedItem = availableItemsList.getSelectedValue();
        if (selectedItem != null) {
            dressUpManager.applyItemToDuck(selectedItem.getName());
        }
    }

    private void removeSelectedItem() {
        DressUpItem selectedItem = currentOutfitList.getSelectedValue();
        if (selectedItem != null) {
            dressUpManager.removeItemFromDuck(selectedItem.getName());
        }
    }

    private void clearOutfit() {
        int result = JOptionPane.showConfirmDialog(this,
                "确定要清空当前所有装扮吗？", "清空装扮", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            dressUpManager.clearCurrentOutfit();
        }
    }

    private void purchaseSelectedItem() {
        DressUpItem selectedItem = availableItemsList.getSelectedValue();
        if (selectedItem == null || selectedItem.isOwned()) return;

        double price = selectedItem.getPrice();
        double money = dressUpManager.getMoney();

        if (money < price) {
            JOptionPane.showMessageDialog(
                    this,
                    "💰 金币不足！\n当前余额: $" + String.format("%.2f", money) +
                            "\n所需金额: $" + String.format("%.2f", price),
                    "购买失败",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                String.format("确定要购买【%s】吗？\n价格: $%.2f", selectedItem.getName(), price),
                "购买装扮",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            boolean success = dressUpManager.purchaseItem(selectedItem.getName());
            if (!success) {
                // 再次检查（如并发情况）
                JOptionPane.showMessageDialog(this, "购买失败，请重试！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveCurrentOutfit() {
        String outfitName = JOptionPane.showInputDialog(this,
                "请输入装扮名称:", "保存装扮", JOptionPane.QUESTION_MESSAGE);

        if (outfitName != null && !outfitName.trim().isEmpty()) {
            dressUpManager.saveCurrentOutfit(outfitName.trim());
            JOptionPane.showMessageDialog(this, "装扮保存成功！", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 新增：查看已保存装扮方法
    private void viewSavedOutfits() {
        dressUpManager.viewSavedOutfits();
    }

    private void shareOutfit() {
        if (currentDuck == null) return;

        String shareText = imageGenerator.generateShareText(
                currentDuck, dressUpManager.getCurrentOutfit());
        showShareDialog(shareText);
    }

    private void saveDuckImage() {
        if (currentDuck == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存鸭子图片");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG图片", "png"));

        String fileName = imageGenerator.generateFileName(currentDuck);
        fileChooser.setSelectedFile(new File(fileName));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getParent(), file.getName() + ".png");
            }

            saveImageToFile(file);
        }
    }

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(this,
                "确定要退出鸭子装扮工坊吗？", "退出", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            cleanup();
            System.exit(0);
        }
    }

    // ========== 数据更新方法 ==========

    private void updateDuckDisplay() {
        if (currentDuck == null) return;

        updateDuckImage();
        updateInfoDisplay();
        updateStatusBar();
        updateCurrentOutfitList();
    }

    private void updateDuckImage() {
        Image duckImage = currentDuck.draw();
        if (duckImage != null) {
            ImageIcon icon = new ImageIcon(duckImage.getScaledInstance(400, 500, Image.SCALE_SMOOTH)); // 放大显示
            duckImageLabel.setIcon(icon);
        }
    }

    private void updateInfoDisplay() {
        infoTextArea.setText(currentDuck.show());
    }

    private void updateStatusBar() {
        moneyLabel.setText(String.format("金钱: $%.2f", dressUpManager.getMoney()));
        levelLabel.setText(String.format("等级: %d", dressUpManager.getLevel()));
        styleScoreLabel.setText(String.format("时尚评分: %d", dressUpManager.getCurrentStyleScore()));
        totalValueLabel.setText(String.format("装扮价值: $%.2f", dressUpManager.getCurrentOutfitValue()));
    }

    private void updateCurrentOutfitList() {
        currentOutfitModel.clear();
        for (DressUpItem item : dressUpManager.getCurrentOutfit()) {
            currentOutfitModel.addElement(item);
        }
        updateButtonStates();
    }

    private void updateButtonStates() {
        updateApplyButtonState();
        updatePurchaseButtonState();
        updateRemoveButtonState();
        updateClearButtonState();
        updateSaveButtonState();
        updateViewSavedOutfitsButtonState(); // 新增：更新查看已保存装扮按钮状态
        updateActionButtonsState();
    }

    private void updateApplyButtonState() {
        DressUpItem selected = availableItemsList.getSelectedValue();
        applyItemButton.setEnabled(selected != null && selected.isOwned());
    }

    private void updatePurchaseButtonState() {
        DressUpItem selected = availableItemsList.getSelectedValue();
        purchaseItemButton.setEnabled(selected != null && !selected.isOwned() &&
                dressUpManager.getMoney() >= selected.getPrice());
    }

    private void updateRemoveButtonState() {
        removeItemButton.setEnabled(currentOutfitList.getSelectedValue() != null);
    }

    private void updateClearButtonState() {
        clearOutfitButton.setEnabled(!dressUpManager.getCurrentOutfit().isEmpty());
    }

    private void updateSaveButtonState() {
        saveOutfitButton.setEnabled(!dressUpManager.getCurrentOutfit().isEmpty());
    }

    // 新增：更新查看已保存装扮按钮状态
    private void updateViewSavedOutfitsButtonState() {
        viewSavedOutfitsButton.setEnabled(true);
    }

    private void updateActionButtonsState() {
        boolean hasDuck = currentDuck != null;
        shareButton.setEnabled(hasDuck);
        saveImageButton.setEnabled(hasDuck);
    }

    //    private void filterItems() {
//        String filter = (String) filterComboBox.getSelectedItem();
//        boolean ownedOnly = ownedOnlyCheckBox.isSelected();
//
//        List<DressUpItem> items = dressUpManager.getAvailableItems();
//        availableItemsModel.clear();
//
//        for (DressUpItem item : items) {
//            if (shouldDisplayItem(item, filter, ownedOnly)) {
//                availableItemsModel.addElement(item);
//            }
//        }
//    }
    private void filterItems() {
        String filter = (String) filterComboBox.getSelectedItem();
        boolean ownedOnly = ownedOnlyCheckBox.isSelected();

        // ✅ 改为获取所有已解锁的装扮（包括未购买的）
        List<DressUpItem> items = dressUpManager.getAllUnlockedItems();

        availableItemsModel.clear();
        for (DressUpItem item : items) {
            if (shouldDisplayItem(item, filter, ownedOnly)) {
                availableItemsModel.addElement(item);
            }
        }
    }

    private void refreshAvailableItems() {
        filterItems();
        updateButtonStates();
    }

    // ========== 辅助方法 ==========

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.BOLD, 14));
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); // ← 关键：文字始终为黑色
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        // 可选：设置禁用状态样式
        button.setDisabledIcon(button.getIcon()); // 保持图标
        button.setContentAreaFilled(true);
        return button;
    }

    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 2), title);
        border.setTitleFont(new Font("微软雅黑", Font.BOLD, 14));
        border.setTitleColor(PRIMARY_COLOR);
        return border;
    }

    private JLabel createTitleLabel(String title) {
        JLabel label = new JLabel(title, JLabel.CENTER);
        label.setFont(new Font("微软雅黑", Font.BOLD, 28));
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    private void showWelcomeDialog() {
        String message = "🎉 欢迎来到鸭子装扮工坊！\n\n开始你的时尚之旅吧！";
        JOptionPane.showMessageDialog(this, message, "欢迎", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cleanup() {
        imageGenerator.shutdown();
    }

    private void setApplicationIcon() {
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("resources/images/app_icon.png"));
        } catch (Exception e) {
            // 使用默认图标
        }
    }

    private void applyComponentStyles() {
        applyFontStyles();
        applyColorStyles();
    }

    private void applyFontStyles() {
        Font chineseFont = new Font("微软雅黑", Font.PLAIN, 12);
        UIManager.put("Button.font", chineseFont);
        UIManager.put("Label.font", chineseFont);
    }

    private void applyColorStyles() {
        UIManager.put("Panel.background", BACKGROUND_COLOR);
    }

    private void applyButtonStyle(JButton button, Color color) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    // ========== 对话框方法 ==========

    private int showConfirmationDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }

    private int showPurchaseConfirmation(DressUpItem item) {
        String message = String.format("确定要购买【%s】吗？\n价格: $%.2f",
                item.getName(), item.getPrice());
        return showConfirmationDialog(message, "购买装扮");
    }

    private String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    private void showShareDialog(String shareText) {
        JTextArea shareArea = new JTextArea(shareText, 10, 40);
        shareArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(shareArea);
        JOptionPane.showMessageDialog(this, scrollPane, "分享装扮", JOptionPane.INFORMATION_MESSAGE);
    }

    private File showSaveImageDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存鸭子图片");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG图片", "png"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void saveImageToFile(File file) {
        try {
            BufferedImage image = imageGenerator.generateDuckImage(
                    currentDuck, dressUpManager.getCurrentOutfit());
            ImageIO.write(image, "PNG", file);
            showSuccessMessage("图片保存成功！\n位置: " + file.getAbsolutePath());
        } catch (Exception ex) {
            showErrorMessage("保存失败: " + ex.getMessage());
        }
    }

    // ========== 事件处理方法 ==========

    private void handlePurchaseResult(boolean success) {
        if (success) {
            showSuccessMessage("购买成功！");
            refreshAvailableItems();
        } else {
            showErrorMessage("购买失败！金钱不足或等级不够。");
        }
    }

    private boolean shouldDisplayItem(DressUpItem item,String filter, boolean ownedOnly) {
        if (filter == null) return false;

        boolean typeMatch = filter.equals("全部") || item.getType().equals(filter.toLowerCase());
        boolean ownedMatch = !ownedOnly || item.isOwned();

        return typeMatch && ownedMatch;
    }

    // ========== 装扮管理器监听器实现 ==========

    @Override
    public void onMoneyChanged(double newAmount) {
        SwingUtilities.invokeLater(() -> {
            moneyLabel.setText(String.format("金钱: $%.2f", newAmount));
            updateButtonStates();
        });
    }

    @Override
    public void onInventoryChanged() {
        SwingUtilities.invokeLater(() -> {
            refreshAvailableItems();
        });
    }

    @Override
    public void onOutfitChanged(List<DressUpItem> newOutfit) {
        SwingUtilities.invokeLater(() -> {
            updateCurrentOutfitList();
            updateDuckDisplay();
        });
    }

    @Override
    public void onDuckChanged(Person newDuck) {
        SwingUtilities.invokeLater(() -> {
            currentDuck = newDuck;
            updateDuckDisplay();
        });
    }

    @Override
    public void onLevelChanged(int newLevel) {
        SwingUtilities.invokeLater(() -> {
            levelLabel.setText(String.format("等级: %d", newLevel));
        });
    }

    @Override
    public void onOutfitSaved(String outfitName, List<DressUpItem> outfit) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    String.format("装扮【%s】保存成功！", outfitName),
                    "保存成功", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    @Override
    public void onOutfitLoaded(String outfitName, List<DressUpItem> outfit) {
        // 加载后同步到当前鸭子
        SwingUtilities.invokeLater(() -> {
            if (currentDuck != null) {
                currentDuck.clearAccessories();
                for (DressUpItem item : outfit) {
                    currentDuck.addAccessory(item);
                }
            }
            updateDuckDisplay();
            JOptionPane.showMessageDialog(this,
                    String.format("装扮【%s】加载成功！", outfitName),
                    "加载成功", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // ========== 内部类 ==========

    /**
     * 装扮项列表渲染器
     * 自定义列表项显示样式
     */
    private class DressUpItemRenderer extends JPanel implements ListCellRenderer<DressUpItem> {
        private JLabel nameLabel;
        private JLabel priceLabel;
        private JLabel styleLabel;
        private JLabel typeLabel;
        private JLabel ownedLabel;

        public DressUpItemRenderer() {
            setLayout(new BorderLayout(5, 5));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setOpaque(true);

            // 初始化标签
            nameLabel = new JLabel();
            nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));

            priceLabel = new JLabel();
            priceLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            priceLabel.setForeground(Color.BLUE);

            styleLabel = new JLabel();
            styleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
            styleLabel.setForeground(new Color(0, 100, 0));

            typeLabel = new JLabel();
            typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 9));
            typeLabel.setForeground(Color.GRAY);

            ownedLabel = new JLabel();
            ownedLabel.setFont(new Font("微软雅黑", Font.BOLD, 9));

            // 创建信息面板
            JPanel infoPanel = new JPanel(new GridLayout(2, 2, 5, 2));
            infoPanel.setOpaque(false);
            infoPanel.add(priceLabel);
            infoPanel.add(styleLabel);
            infoPanel.add(typeLabel);
            infoPanel.add(ownedLabel);

            add(nameLabel, BorderLayout.NORTH);
            add(infoPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends DressUpItem> list,
                                                      DressUpItem item, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            // 设置背景色
            if (isSelected) {
                setBackground(new Color(220, 240, 255));
            } else {
                setBackground(index % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
            }

            // 设置边框
            if (isSelected) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 144, 255), 2),
                        BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
            } else {
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            }

            if (item != null) {
                // 设置文本内容
                nameLabel.setText(item.getName());
                priceLabel.setText(String.format("$%.2f", item.getPrice()));
                styleLabel.setText("+" + item.getStylePoints() + " 时尚");
                typeLabel.setText(item.getType().toUpperCase());

                // 设置拥有状态
                if (item.isOwned()) {
                    ownedLabel.setText("✓ 已拥有");
                    ownedLabel.setForeground(new Color(0, 128, 0));
                } else {
                    ownedLabel.setText("✗ 未拥有");
                    ownedLabel.setForeground(Color.RED);
                }

                // 根据类型设置颜色
                Color typeColor = getTypeColor(item.getType());
                nameLabel.setForeground(typeColor);
                typeLabel.setForeground(typeColor);
            }

            return this;
        }

        /**
         * 获取类型对应的颜色
         */
        private Color getTypeColor(String type) {
            switch (type.toLowerCase()) {
                case "hat": return new Color(139, 69, 19); // 棕色
                case "glasses": return new Color(105, 105, 105); // 灰色
                case "scarf": return new Color(220, 20, 60); // 红色
                case "tie": return new Color(30, 144, 255); // 蓝色
                case "cane": return new Color(160, 82, 45); // 褐色
                default: return Color.BLACK;
            }
        }
    }

    // ========== 主方法 ==========

    /**
     * 应用程序入口点
     */
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 在事件分发线程中启动应用
        SwingUtilities.invokeLater(() -> {
            try {
                new DuckDressUpApp().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "应用程序启动失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}