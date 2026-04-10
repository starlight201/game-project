package com.game.dressup;

import com.game.database.DatabaseConfig;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 装扮管理器
 * 管理用户的装扮、库存、金钱和状态
 */
public class DressUpManager {
    private static DressUpManager instance;

    // 当前选中的鸭子
    private Person currentDuck;

    // 所有可用的装扮项
    private Map<String, DressUpItem> allItems = new HashMap<>();

    // 用户拥有的装扮项
    private Set<String> ownedItems = new HashSet<>();

    // 当前应用的装扮
    private List<DressUpItem> currentOutfit = new ArrayList<>();

    // 用户金钱
    private double money = 0.0; // 从数据库加载

    // 用户等级
    private int level = 1;

    // 装扮历史
    private List<List<DressUpItem>> outfitHistory = new ArrayList<>();

    // 监听器列表
    private List<DressUpListener> listeners = new ArrayList<>();

    // 私有构造方法
    private DressUpManager() {
        initializeDefaultItems();
        initializeShopItemsInDatabase(); // 初始化商店装扮
        loadUserDataFromDatabase();
    }

    /**
     * 获取单例实例
     */
    public static synchronized DressUpManager getInstance() {
        if (instance == null) {
            instance = new DressUpManager();
        }
        return instance;
    }

    /**
     * 初始化商店中的装扮项
     * 将默认装扮项插入到数据库中，如果它们不存在的话
     */
    private void initializeShopItemsInDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 创建装扮商品表
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS shop_items (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    item_name VARCHAR(255) UNIQUE NOT NULL,
                    item_description TEXT,
                    item_price DECIMAL(10, 2),
                    style_points INT,
                    item_type VARCHAR(50),
                    image_path VARCHAR(255),
                    unlock_level INT DEFAULT 1
                )
            """;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableQuery);
            }

            // 准备插入装扮项的查询
            String insertItemQuery = """
                INSERT IGNORE INTO shop_items 
                (item_name, item_description, item_price, style_points, item_type, image_path, unlock_level) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement stmt = conn.prepareStatement(insertItemQuery)) {
                // 插入所有默认装扮项
                for (DressUpItem item : allItems.values()) {
                    stmt.setString(1, item.getName());
                    stmt.setString(2, item.getDescription());
                    stmt.setDouble(3, item.getPrice());
                    stmt.setInt(4, item.getStylePoints());
                    stmt.setString(5, item.getType());
                    stmt.setString(6, item.getImagePath());
                    stmt.setInt(7, item.getUnlockLevel());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从数据库加载用户数据
     */
    private void loadUserDataFromDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 加载用户金钱
            String moneyQuery = "SELECT total_money FROM user WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(moneyQuery)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    money = rs.getDouble("total_money");
                }
            }

            // 加载用户拥有的装扮
            String ownedItemsQuery = "SELECT item_name FROM user_owned_items WHERE user_id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(ownedItemsQuery)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    ownedItems.add(itemName);
                    // 设置对应装扮为已拥有
                    DressUpItem item = allItems.get(itemName);
                    if (item != null) {
                        item.setOwned(true);
                    }
                }
            }

            // 加载用户等级
            String levelQuery = "SELECT level FROM user_levels WHERE user_id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(levelQuery)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    level = rs.getInt("level");
                }
            }

            // 加载当前装扮搭配
            loadCurrentOutfitFromDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
            // 如果数据库加载失败，使用默认值
            money = 0.0;
        }
    }

    /**
     * 保存用户数据到数据库
     */
    private void saveUserDataToDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 更新用户金钱
            String updateMoneyQuery = "UPDATE user SET total_money = ? WHERE id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(updateMoneyQuery)) {
                stmt.setDouble(1, money);
                stmt.executeUpdate();
            }

            // 更新用户等级
            String updateLevelQuery = "INSERT INTO user_levels (user_id, level) VALUES (1, ?) " +
                    "ON DUPLICATE KEY UPDATE level = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateLevelQuery)) {
                stmt.setInt(1, level);
                stmt.setInt(2, level);
                stmt.executeUpdate();
            }

            // 保存用户拥有的装扮
            String deleteOwnedItemsQuery = "DELETE FROM user_owned_items WHERE user_id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(deleteOwnedItemsQuery)) {
                stmt.executeUpdate();
            }

            String insertOwnedItemQuery = "INSERT INTO user_owned_items (user_id, item_name) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertOwnedItemQuery)) {
                for (String itemName : ownedItems) {
                    stmt.setInt(1, 1);
                    stmt.setString(2, itemName);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // 保存当前装扮搭配
            saveCurrentOutfitToDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存当前装扮到数据库
     */
    private void saveCurrentOutfitToDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 删除旧的当前装扮
            String deleteCurrentOutfitQuery = "DELETE FROM user_current_outfit WHERE user_id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(deleteCurrentOutfitQuery)) {
                stmt.executeUpdate();
            }

            // 保存当前装扮
            String insertOutfitQuery = "INSERT INTO user_current_outfit (user_id, item_name, item_order) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertOutfitQuery)) {
                int order = 0;
                for (DressUpItem item : currentOutfit) {
                    stmt.setInt(1, 1);
                    stmt.setString(2, item.getName());
                    stmt.setInt(3, order++);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从数据库加载当前装扮
     */
    private void loadCurrentOutfitFromDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "SELECT item_name FROM user_current_outfit WHERE user_id = 1 ORDER BY item_order";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    DressUpItem item = allItems.get(itemName);
                    if (item != null && ownedItems.contains(itemName)) {
                        currentOutfit.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化默认装扮项
     * 移除了图片路径查找逻辑，直接使用默认的图片名称
     */
    private void initializeDefaultItems() {
        // 帽子
        registerItem(new Hat("经典鸭舌帽", "时尚鸭舌帽，彰显个性", 50.0, 10, "hat_classic.png"));
        registerItem(new Hat("魔术师礼帽", "神秘的魔术师帽子", 120.0, 25, "hat_magician.png"));
        registerItem(new Hat("水手帽", "航海风格帽子", 80.0, 15, "hat_sailor.png"));
        registerItem(new Hat("生日帽", "派对必备", 30.0, 5, "hat_party.png"));
        registerItem(new Hat("王冠", "皇家尊贵", 500.0, 50, "hat_crown.png"));

        // 眼镜
        registerItem(new Glasses("圆框眼镜", "文艺复古", 60.0, 12, "glasses_round.png"));
        registerItem(new Glasses("墨镜", "酷炫有型", 100.0, 20, "glasses_sunglasses.png"));
        registerItem(new Glasses("单片眼镜", "绅士风度", 150.0, 30, "glasses_monocle.png"));
        registerItem(new Glasses("VR眼镜", "科技感十足", 300.0, 40, "glasses_vr.png"));

        // 围巾
        registerItem(new Scarf("条纹围巾", "经典条纹设计", 40.0, 8, "scarf_stripe.png"));
        registerItem(new Scarf("格子围巾", "英伦风格", 70.0, 15, "scarf_check.png"));
        registerItem(new Scarf("羊毛围巾", "温暖舒适", 90.0, 18, "scarf_wool.png"));
        registerItem(new Scarf("丝绸围巾", "高雅华丽", 200.0, 35, "scarf_silk.png"));

        // 领带
        registerItem(new Tie("红色领带", "正式场合", 60.0, 12, "tie_red.png"));
        registerItem(new Tie("蝴蝶结", "优雅别致", 80.0, 18, "tie_bow.png"));
        registerItem(new Tie("波点领带", "活泼可爱", 45.0, 10, "tie_polka.png"));
        registerItem(new Tie("金边领带", "奢华尊贵", 350.0, 45, "tie_gold.png"));

        // 手杖
        registerItem(new Cane("木质手杖", "经典木质", 120.0, 20, "cane_wood.png"));
        registerItem(new Cane("金属手杖", "现代感强", 200.0, 30, "cane_metal.png"));
        registerItem(new Cane("宝石手杖", "镶嵌宝石", 600.0, 60, "cane_gem.png"));
        registerItem(new Cane("魔法手杖", "闪闪发光", 800.0, 80, "cane_magic.png"));
    }

    /**
     * 注册装扮项
     */
    public void registerItem(DressUpItem item) {
        allItems.put(item.getName(), item);
    }

    /**
     * 获取所有装扮项
     */
    public List<DressUpItem> getAllItems() {
        return new ArrayList<>(allItems.values());
    }

    /**
     * 获取可用的装扮项（已拥有且满足等级要求）
     */
    public List<DressUpItem> getAvailableItems() {
        return allItems.values().stream()
                .filter(item -> ownedItems.contains(item.getName()) && item.getUnlockLevel() <= level)
                .sorted(Comparator.comparing(DressUpItem::getType).thenComparing(DressUpItem::getName))
                .collect(Collectors.toList());
    }

    /**
     * 获取可购买的装扮项（未拥有且满足等级要求）
     */
    public List<DressUpItem> getPurchasableItems() {
        return allItems.values().stream()
                .filter(item -> !ownedItems.contains(item.getName())
                        && item.getUnlockLevel() <= level
                        && item.isPurchasable())
                .sorted(Comparator.comparing(DressUpItem::getType).thenComparing(DressUpItem::getPrice))
                .collect(Collectors.toList());
    }

    /**
     * 购买装扮项
     */
    public boolean purchaseItem(String itemName) {
        DressUpItem item = allItems.get(itemName);
        if (item == null) {
            return false;
        }

        if (ownedItems.contains(itemName)) {
            return false; // 已经拥有
        }

        if (item.getUnlockLevel() > level) {
            return false; // 等级不足
        }

        if (money < item.getPrice()) {
            return false; // 金钱不足
        }

        // 扣除金钱
        money -= item.getPrice();
        ownedItems.add(itemName);
        item.setOwned(true);

        // 保存到数据库
        saveUserDataToDatabase();

        // 通知监听器
        notifyMoneyChanged();
        notifyInventoryChanged();

        return true;
    }

    /**
     * 应用装扮到当前鸭子
     */
    public boolean applyItemToDuck(String itemName) {
        if (currentDuck == null) {
            return false;
        }

        DressUpItem item = allItems.get(itemName);
        if (item == null || !ownedItems.contains(itemName)) {
            return false;
        }

        // 检查兼容性
        if (!currentDuck.isAccessoryCompatible(item)) {
            return false;
        }

        // 检查是否已穿戴同类型装扮
        String itemType = item.getType();
        for (DressUpItem existing : currentOutfit) {
            if (existing.getType().equals(itemType)) {
                // 移除同类型的旧装扮
                currentOutfit.remove(existing);
                break;
            }
        }

        // 添加新装扮
        currentOutfit.add(item);
        currentDuck.addAccessory(item);

        // 保存到历史
        saveOutfitToHistory();

        // 保存到数据库
        saveCurrentOutfitToDatabase();

        // 通知监听器
        notifyOutfitChanged();

        return true;
    }

    /**
     * 移除装扮
     */
    public boolean removeItemFromDuck(String itemName) {
        DressUpItem item = allItems.get(itemName);
        if (item == null) {
            return false;
        }

        if (currentOutfit.remove(item)) {
            currentDuck.removeAccessory(item);

            // 保存到数据库
            saveCurrentOutfitToDatabase();

            notifyOutfitChanged();
            return true;
        }

        return false;
    }

    /**
     * 清空当前装扮
     */
    public void clearCurrentOutfit() {
        currentOutfit.clear();
        if (currentDuck != null) {
            currentDuck.clearAccessories();
        }

        // 保存到数据库
        saveCurrentOutfitToDatabase();

        notifyOutfitChanged();
    }

    /**
     * 保存当前装扮到历史
     */
    public void saveOutfitToHistory() {
        if (!currentOutfit.isEmpty()) {
            outfitHistory.add(new ArrayList<>(currentOutfit));
            // 保持历史记录最多10条
            if (outfitHistory.size() > 10) {
                outfitHistory.remove(0);
            }
        }
    }

    /**
     * 获取装扮历史
     */
    public List<List<DressUpItem>> getOutfitHistory() {
        return new ArrayList<>(outfitHistory);
    }

    /**
     * 设置当前鸭子
     */
    public void setCurrentDuck(Person duck) {
        this.currentDuck = duck;
        this.currentOutfit.clear();
        if (duck != null) {
            this.currentOutfit.addAll(duck.getAccessories());
        }
        notifyDuckChanged();
    }

    /**
     * 获取当前鸭子
     */
    public Person getCurrentDuck() {
        return currentDuck;
    }

    /**
     * 获取当前装扮
     */
    public List<DressUpItem> getCurrentOutfit() {
        return new ArrayList<>(currentOutfit);
    }

    /**
     * 获取当前金钱
     */
    public double getMoney() {
        return money;
    }

    /**
     * 增加金钱
     */
    public void addMoney(double amount) {
        if (amount > 0) {
            money += amount;

            // 保存到数据库
            saveUserDataToDatabase();

            notifyMoneyChanged();
        }
    }

    /**
     * 消费金钱
     */
    public boolean spendMoney(double amount) {
        if (amount > 0 && money >= amount) {
            money -= amount;

            // 保存到数据库
            saveUserDataToDatabase();

            notifyMoneyChanged();
            return true;
        }
        return false;
    }

    /**
     * 获取用户等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置用户等级
     */
    public void setLevel(int level) {
        if (level > 0) {
            this.level = level;

            // 保存到数据库
            saveUserDataToDatabase();

            notifyLevelChanged();
        }
    }

    /**
     * 升级
     */
    public void levelUp() {
        level++;

        // 保存到数据库
        saveUserDataToDatabase();

        notifyLevelChanged();
    }

    /**
     * 获取当前装扮总价值
     */
    public double getCurrentOutfitValue() {
        if (currentDuck == null) {
            return 0;
        }
        return currentDuck.getTotalOutfitCost();
    }

    /**
     * 获取当前时尚评分
     */
    public int getCurrentStyleScore() {
        if (currentDuck == null) {
            return 0;
        }
        return currentDuck.getStyleScore();
    }

    /**
     * 保存当前装扮
     */
    public void saveCurrentOutfit(String outfitName) {
        // 保存到数据库
        saveOutfitToDatabase(outfitName);
        notifyOutfitSaved(outfitName);
    }

    /**
     * 加载保存的装扮
     */
    public boolean loadOutfit(String outfitName) {
        // 从数据库加载
        boolean success = loadOutfitFromDatabase(outfitName);
        if (success) {
            // 通知监听器
            notifyOutfitLoaded(outfitName);

            // 同步到当前鸭子
            if (currentDuck != null) {
                currentDuck.clearAccessories();
                for (DressUpItem item : currentOutfit) {
                    currentDuck.addAccessory(item);
                }
            }

            return true;
        }
        return false;
    }

    /**
     * 保存搭配到数据库
     */
    private void saveOutfitToDatabase(String outfitName) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // 删除已存在的搭配
            String deleteQuery = "DELETE FROM user_saved_outfits WHERE user_id = 1 AND outfit_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, outfitName);
                stmt.executeUpdate();
            }

            // 保存新的搭配
            String insertQuery = "INSERT INTO user_saved_outfits (user_id, outfit_name, item_name, item_order) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                int order = 0;
                for (DressUpItem item : currentOutfit) {
                    stmt.setInt(1, 1);
                    stmt.setString(2, outfitName);
                    stmt.setString(3, item.getName());
                    stmt.setInt(4, order++);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从数据库加载搭配
     */
    private boolean loadOutfitFromDatabase(String outfitName) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "SELECT item_name FROM user_saved_outfits WHERE user_id = 1 AND outfit_name = ? ORDER BY item_order";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, outfitName);
                ResultSet rs = stmt.executeQuery();

                List<DressUpItem> newOutfit = new ArrayList<>();
                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    DressUpItem item = allItems.get(itemName);
                    if (item != null && ownedItems.contains(itemName)) {
                        newOutfit.add(item);
                    }
                }

                // 更新当前装扮
                currentOutfit.clear();
                currentOutfit.addAll(newOutfit);

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取保存的搭配列表
     */
    public List<String> getSavedOutfits() {
        List<String> outfits = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "SELECT DISTINCT outfit_name FROM user_saved_outfits WHERE user_id = 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    outfits.add(rs.getString("outfit_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return outfits;
    }

    /**
     * 获取保存的搭配详细信息
     */
    public Map<String, List<DressUpItem>> getSavedOutfitsWithDetails() {
        Map<String, List<DressUpItem>> outfitsMap = new HashMap<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String query = "SELECT outfit_name, item_name FROM user_saved_outfits WHERE user_id = 1 ORDER BY outfit_name, item_order";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String outfitName = rs.getString("outfit_name");
                    String itemName = rs.getString("item_name");

                    // 获取装扮项对象
                    DressUpItem item = allItems.get(itemName);
                    if (item != null) {
                        outfitsMap.computeIfAbsent(outfitName, k -> new ArrayList<>()).add(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return outfitsMap;
    }

    /**
     * 查看已保存的装扮
     */
    public void viewSavedOutfits() {
        Map<String, List<DressUpItem>> savedOutfits = getSavedOutfitsWithDetails();

        if (savedOutfits.isEmpty()) {
            JOptionPane.showMessageDialog(null, "您还没有保存任何装扮搭配！", "查看装扮", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 创建一个对话框来显示保存的装扮
        JDialog dialog = new JDialog();
        dialog.setTitle("已保存的装扮");
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建列表来显示保存的装扮名称
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String outfitName : savedOutfits.keySet()) {
            listModel.addElement(outfitName);
        }

        JList<String> outfitList = new JList<>(listModel);
        outfitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(outfitList);
        listScrollPane.setPreferredSize(new Dimension(200, 400));

        // 创建详情面板
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBorder(BorderFactory.createTitledBorder("装扮详情"));
        JScrollPane detailScrollPane = new JScrollPane(detailPanel);
        detailScrollPane.setPreferredSize(new Dimension(350, 400));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loadButton = new JButton("加载搭配");
        JButton deleteButton = new JButton("删除搭配");
        JButton closeButton = new JButton("关闭");

        buttonPanel.add(loadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // 组合面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("保存的搭配"));
        leftPanel.add(listScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, detailScrollPane);
        splitPane.setDividerLocation(200);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 显示初始详情
        if (!listModel.isEmpty()) {
            outfitList.setSelectedIndex(0);
            String initialSelection = listModel.getElementAt(0);
            updateDetailPanel(detailPanel, savedOutfits.get(initialSelection));
        }

        // 添加选择监听器
        outfitList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedOutfit = outfitList.getSelectedValue();
                if (selectedOutfit != null) {
                    List<DressUpItem> outfitItems = savedOutfits.get(selectedOutfit);
                    updateDetailPanel(detailPanel, outfitItems);
                }
            }
        });

        // 加载按钮事件
        loadButton.addActionListener(e -> {
            String selectedOutfit = outfitList.getSelectedValue();
            if (selectedOutfit != null) {
                boolean success = loadOutfit(selectedOutfit);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "搭配【" + selectedOutfit + "】加载成功！",
                            "加载成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "加载搭配失败！",
                            "加载失败", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "请先选择一个搭配！",
                        "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 删除按钮事件
        deleteButton.addActionListener(e -> {
            String selectedOutfit = outfitList.getSelectedValue();
            if (selectedOutfit != null) {
                int result = JOptionPane.showConfirmDialog(dialog,
                        "确定要删除搭配【" + selectedOutfit + "】吗？",
                        "删除确认", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    deleteSavedOutfit(selectedOutfit);
                    // 更新列表
                    listModel.removeElement(selectedOutfit);
                    detailPanel.removeAll();
                    JLabel detailLabel = new JLabel("请选择一个装扮方案查看详情", JLabel.CENTER);
                    detailPanel.add(detailLabel);
                    detailPanel.revalidate();
                    detailPanel.repaint();

                    JOptionPane.showMessageDialog(dialog, "搭配【" + selectedOutfit + "】删除成功！",
                            "删除成功", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "请先选择一个搭配！",
                        "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 关闭按钮事件
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * 更新详情面板
     */
    private void updateDetailPanel(JPanel detailPanel, List<DressUpItem> outfitItems) {
        detailPanel.removeAll();

        if (outfitItems == null || outfitItems.isEmpty()) {
            JLabel label = new JLabel("暂无装扮详情");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            detailPanel.add(label);
        } else {
            // 计算总价值和总时尚分
            double totalValue = 0;
            int totalStyle = 0;

            for (DressUpItem item : outfitItems) {
                totalValue += item.getPrice();
                totalStyle += item.getStylePoints();

                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel itemLabel = new JLabel(item.getName() +
                        " (类型: " + item.getType() +
                        ", 价格: $" + String.format("%.2f", item.getPrice()) +
                        ", 时尚分: " + item.getStylePoints() + ")");

                itemPanel.add(itemLabel);
                itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailPanel.add(itemPanel);
            }

            // 添加总计信息
            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel totalLabel = new JLabel("<html><b>总计: 价值 $" + String.format("%.2f", totalValue) +
                    ", 时尚分 " + totalStyle + "</b></html>");
            totalPanel.add(totalLabel);
            totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            detailPanel.add(totalPanel);
        }

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    /**
     * 删除保存的搭配
     */
    private void deleteSavedOutfit(String outfitName) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String deleteQuery = "DELETE FROM user_saved_outfits WHERE user_id = 1 AND outfit_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setString(1, outfitName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加监听器
     */
    public void addListener(DressUpListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除监听器
     */
    public void removeListener(DressUpListener listener) {
        listeners.remove(listener);
    }

    /**
     * 通知金钱变化
     */
    private void notifyMoneyChanged() {
        for (DressUpListener listener : listeners) {
            listener.onMoneyChanged(money);
        }
    }

    /**
     * 通知库存变化
     */
    private void notifyInventoryChanged() {
        for (DressUpListener listener : listeners) {
            listener.onInventoryChanged();
        }
    }

    /**
     * 通知装扮变化
     */
    private void notifyOutfitChanged() {
        for (DressUpListener listener : listeners) {
            listener.onOutfitChanged(currentOutfit);
        }
    }

    /**
     * 通知鸭子变化
     */
    private void notifyDuckChanged() {
        for (DressUpListener listener : listeners) {
            listener.onDuckChanged(currentDuck);
        }
    }

    /**
     * 通知等级变化
     */
    private void notifyLevelChanged() {
        for (DressUpListener listener : listeners) {
            listener.onLevelChanged(level);
        }
    }

    /**
     * 通知装扮保存
     */
    private void notifyOutfitSaved(String outfitName) {
        for (DressUpListener listener : listeners) {
            listener.onOutfitSaved(outfitName, currentOutfit);
        }
    }

    /**
     * 通知装扮加载
     */
    private void notifyOutfitLoaded(String outfitName) {
        for (DressUpListener listener : listeners) {
            listener.onOutfitLoaded(outfitName, currentOutfit);
        }
    }

    public List<DressUpItem> getAllUnlockedItems() {
        return allItems.values().stream()
                .filter(item -> item.getUnlockLevel() <= level)
                .sorted(Comparator.comparing(DressUpItem::getType).thenComparing(DressUpItem::getName))
                .collect(Collectors.toList());
    }

    /**
     * 装扮监听器接口
     */
    public interface DressUpListener {
        void onMoneyChanged(double newAmount);
        void onInventoryChanged();
        void onOutfitChanged(List<DressUpItem> newOutfit);
        void onDuckChanged(Person newDuck);
        void onLevelChanged(int newLevel);
        void onOutfitSaved(String outfitName, List<DressUpItem> outfit);
        void onOutfitLoaded(String outfitName, List<DressUpItem> outfit);
    }
}