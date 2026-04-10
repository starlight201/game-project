package com.game.attendance;

import com.game.database.DatabaseConfig;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TeacherTangAttendanceSystem extends JFrame {
    private DatabaseConfig dbConfig;
    private JPanel mainPanel;
    private JLabel currentStudentLabel;
    private JLabel studentPhotoLabel;
    private JLabel studentIdLabel;
    private JLabel statusLabel;
    private JButton startRollCallButton;
    private JButton pauseButton;
    private JButton viewRecordsButton;
    private JButton statisticsButton;
    private JTextArea logArea;
    private Timer rollCallTimer;
    private List<Student> allStudents;
    private List<Student> selectedStudents;
    private int currentIndex;
    private RollCallSession currentSession;
    private Map<String, Integer> attendanceCountMap;

    public TeacherTangAttendanceSystem() {
        dbConfig = new DatabaseConfig();
        initializeDatabase();
        initializeUI();
        loadStudents();
    }

    private void initializeDatabase() {
        try (Connection conn = dbConfig.getConnection()) {
            Statement stmt = conn.createStatement();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据库初始化失败: " + e.getMessage());
        }
    }

    private void initializeUI() {
        setTitle("唐老师点名系统");

        setSize(1200, 800);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel = new JPanel(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout());
        startRollCallButton = new JButton("开始点名");
        pauseButton = new JButton("暂停");
        viewRecordsButton = new JButton("查看考勤记录");
        statisticsButton = new JButton("考勤统计");

        pauseButton.setEnabled(false);

        startRollCallButton.addActionListener(e -> showRollCallDialog());
        pauseButton.addActionListener(e -> togglePause());
        viewRecordsButton.addActionListener(e -> showAttendanceRecords());
        statisticsButton.addActionListener(e -> showStatistics());

        controlPanel.add(startRollCallButton);
        controlPanel.add(pauseButton);
        controlPanel.add(viewRecordsButton);
        controlPanel.add(statisticsButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        JPanel displayPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        studentPhotoLabel = new JLabel(" ", JLabel.CENTER);
        studentPhotoLabel.setPreferredSize(new Dimension(200, 200));
        studentPhotoLabel.setBorder(BorderFactory.createEtchedBorder());
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        displayPanel.add(studentPhotoLabel, gbc);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        currentStudentLabel = new JLabel("等待开始点名...", JLabel.CENTER);
        currentStudentLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        currentStudentLabel.setForeground(Color.BLUE);

        studentIdLabel = new JLabel("", JLabel.CENTER);
        studentIdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));

        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        statusLabel.setForeground(Color.RED);

        infoPanel.add(currentStudentLabel);
        infoPanel.add(studentIdLabel);
        infoPanel.add(statusLabel);

        gbc.gridx = 1; gbc.gridy = 0;
        displayPanel.add(infoPanel, gbc);

        mainPanel.add(displayPanel, BorderLayout.CENTER);

        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(new TitledBorder("点名日志"));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadStudents() {
        allStudents = new ArrayList<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT student_id, name, photo_path FROM students")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("photo_path")
                );
                allStudents.add(student);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "加载学生数据失败: " + e.getMessage());
        }
    }

    private void showRollCallDialog() {
        JDialog dialog = new JDialog(this, "选择点名方式", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

        ButtonGroup callTypeGroup = new ButtonGroup();
        JRadioButton fullCallRadio = new JRadioButton("全点");
        JRadioButton partialCallRadio = new JRadioButton("抽点");
        callTypeGroup.add(fullCallRadio);
        callTypeGroup.add(partialCallRadio);
        fullCallRadio.setSelected(true);

        panel.add(new JLabel("点名方式:"));
        panel.add(fullCallRadio);
        panel.add(partialCallRadio);

        JPanel countPanel = new JPanel(new FlowLayout());
        JLabel countLabel = new JLabel("抽取人数:");
        String[] counts = {"10", "15", "20", "自定义"};
        JComboBox<String> countCombo = new JComboBox<>(counts);
        countCombo.setEnabled(false);
        countCombo.setSelectedIndex(0);

        JTextField customCountField = new JTextField(5);
        customCountField.setEnabled(false);

        ButtonGroup strategyGroup = new ButtonGroup();
        JRadioButton sequentialRadio = new JRadioButton("顺序点名");
        JRadioButton randomRadio = new JRadioButton("随机选取");
        JRadioButton absentMostRadio = new JRadioButton("优先选择旷课次数最多的同学");
        JRadioButton attendedLeastRadio = new JRadioButton("优先选择点到次数最少的同学");
        strategyGroup.add(sequentialRadio);
        strategyGroup.add(randomRadio);
        strategyGroup.add(absentMostRadio);
        strategyGroup.add(attendedLeastRadio);
        sequentialRadio.setSelected(true);

        // 事件监听器：处理全点/抽点切换
        partialCallRadio.addActionListener(e -> {
            boolean isPartial = partialCallRadio.isSelected();
            countCombo.setEnabled(isPartial);
            if ("自定义".equals(countCombo.getSelectedItem())) {
                customCountField.setEnabled(isPartial);
            } else {
                customCountField.setEnabled(false);
            }

            // 抽点模式下禁用顺序点名，全点模式下启用
            sequentialRadio.setEnabled(!isPartial);
            if (isPartial && sequentialRadio.isSelected()) {
                randomRadio.setSelected(true);
            }
        });

        // 事件监听器：处理全点/抽点切换（全点被选中时）
        fullCallRadio.addActionListener(e -> {
            boolean isPartial = partialCallRadio.isSelected();
            // 当切换到全点时，启用顺序点名选项
            if (!isPartial) {
                sequentialRadio.setEnabled(true);
                // 如果没有其他策略被选中，则选中顺序点名
                if (!randomRadio.isSelected() && !absentMostRadio.isSelected() && !attendedLeastRadio.isSelected()) {
                    sequentialRadio.setSelected(true);
                }
            }
        });

        countCombo.addActionListener(e -> {
            boolean isPartial = partialCallRadio.isSelected();
            customCountField.setEnabled(isPartial && "自定义".equals(countCombo.getSelectedItem()));
        });

        countPanel.add(countLabel);
        countPanel.add(countCombo);
        countPanel.add(customCountField);
        panel.add(countPanel);

        panel.add(new JLabel("点名策略:"));
        panel.add(sequentialRadio);
        panel.add(randomRadio);
        panel.add(absentMostRadio);
        panel.add(attendedLeastRadio);

        JButton okButton = new JButton("确定");
        okButton.addActionListener(e -> {
            String callType = fullCallRadio.isSelected() ? "全点" : "抽点";
            String strategy;
            if (sequentialRadio.isSelected()) {
                strategy = "顺序点名";
            } else if (randomRadio.isSelected()) {
                strategy = "随机选取";
            } else if (absentMostRadio.isSelected()) {
                strategy = "优先旷课多";
            } else {
                strategy = "优先点到少";
            }

            int studentCount = 0;
            if ("抽点".equals(callType)) {
                if ("自定义".equals(countCombo.getSelectedItem())) {
                    try {
                        studentCount = Integer.parseInt(customCountField.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "请输入有效的数字");
                        return;
                    }
                } else {
                    studentCount = Integer.parseInt((String)countCombo.getSelectedItem());
                }
            } else {
                studentCount = allStudents.size();
            }

            startRollCall(callType, strategy, studentCount);
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private void startRollCall(String callType, String strategy, int studentCount) {
        currentSession = new RollCallSession(callType, strategy, studentCount);
        saveSessionToDB();

        selectedStudents = selectStudentsByStrategy(strategy, studentCount);
        currentIndex = 0;

        startRollCallProcess();
    }

    private List<Student> selectStudentsByStrategy(String strategy, int count) {
        List<Student> result = new ArrayList<>();

        if ("全点".equals(currentSession.callType)) {
            result.addAll(allStudents);
            switch (strategy) {
                case "顺序点名":
                    // 保持原始顺序，不做处理
                    break;
                case "随机选取":
                    Collections.shuffle(result);
                    break;
                case "优先旷课多":
                    loadAttendanceCounts();
                    result.sort((s1, s2) ->
                            attendanceCountMap.getOrDefault(s2.studentId + "_absent", 0)
                                    .compareTo(attendanceCountMap.getOrDefault(s1.studentId + "_absent", 0)));
                    break;
                case "优先点到少":
                    loadAttendanceCounts();
                    result.sort((s1, s2) ->
                            attendanceCountMap.getOrDefault(s1.studentId + "_attended", 0)
                                    .compareTo(attendanceCountMap.getOrDefault(s2.studentId + "_attended", 0)));
                    break;
            }
        } else {
            List<Student> candidates = new ArrayList<>(allStudents);
            switch (strategy) {
                case "随机选取":
                    Collections.shuffle(candidates);
                    result = candidates.subList(0, Math.min(count, candidates.size()));
                    break;
                case "优先旷课多":
                    loadAttendanceCounts();
                    candidates.sort((s1, s2) ->
                            attendanceCountMap.getOrDefault(s2.studentId + "_absent", 0)
                                    .compareTo(attendanceCountMap.getOrDefault(s1.studentId + "_absent", 0)));
                    result = candidates.subList(0, Math.min(count, candidates.size()));
                    break;
                case "优先点到少":
                    loadAttendanceCounts();
                    candidates.sort((s1, s2) ->
                            attendanceCountMap.getOrDefault(s1.studentId + "_attended", 0)
                                    .compareTo(attendanceCountMap.getOrDefault(s2.studentId + "_attended", 0)));
                    result = candidates.subList(0, Math.min(count, candidates.size()));
                    break;
            }
        }

        return result;
    }

    private void loadAttendanceCounts() {
        attendanceCountMap = new HashMap<>();
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT student_id, attendance_status, COUNT(*) as cnt FROM attendance_records GROUP BY student_id, attendance_status")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String status = rs.getString("attendance_status");
                int count = rs.getInt("cnt");

                if ("出勤".equals(status) || "迟到".equals(status)) {
                    attendanceCountMap.put(studentId + "_attended",
                            attendanceCountMap.getOrDefault(studentId + "_attended", 0) + count);
                } else if ("旷课".equals(status)) {
                    attendanceCountMap.put(studentId + "_absent",
                            attendanceCountMap.getOrDefault(studentId + "_absent", 0) + count);
                } else if ("请假".equals(status)) {
                    attendanceCountMap.put(studentId + "_leave",
                            attendanceCountMap.getOrDefault(studentId + "_leave", 0) + count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startRollCallProcess() {
        if (selectedStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可点名的学生！");
            return;
        }

        startRollCallButton.setEnabled(false);
        pauseButton.setEnabled(true);

        showCurrentStudent();

        logArea.append("开始点名：" + selectedStudents.get(currentIndex).name + "\n");

        rollCallTimer = new Timer(2000, e -> {
            nextStudent();
        });
        rollCallTimer.start();
    }

    private void showCurrentStudent() {
        if (currentIndex >= selectedStudents.size()) {
            finishRollCall();
            return;
        }

        Student currentStudent = selectedStudents.get(currentIndex);
        currentStudentLabel.setText(currentStudent.name);
        studentIdLabel.setText("学号: " + currentStudent.studentId);
        statusLabel.setText("等待应答...");

        studentPhotoLabel.setText("<HTML><div style='font-size:14px; padding:50px;'>" +
                currentStudent.name.substring(0, 1) + "</div></HTML>");
        studentPhotoLabel.setBackground(Color.LIGHT_GRAY);
        studentPhotoLabel.setOpaque(true);
    }

    private void nextStudent() {
        if (currentIndex < selectedStudents.size()) {
            Student currentStudent = selectedStudents.get(currentIndex);

            Object[] options = {"到", "请假", "旷课", "迟到"};
            int choice = JOptionPane.showOptionDialog(this,
                    "学生: " + currentStudent.name + " (" + currentStudent.studentId + ")\n请选择考勤状态:",
                    "考勤确认",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            String status = "";
            switch (choice) {
                case 0: status = "出勤"; break;
                case 1: status = "请假"; break;
                case 2: status = "旷课"; break;
                case 3: status = "迟到"; break;
                default: status = "旷课"; break;
            }

            statusLabel.setText("状态: " + status);
            logArea.append(currentStudent.name + " - " + status + "\n");

            saveAttendanceRecord(currentStudent.studentId, currentStudent.name, status);

            currentIndex++;
            showCurrentStudent();
        } else {
            finishRollCall();
        }
    }

    private void saveAttendanceRecord(String studentId, String studentName, String status) {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO attendance_records (student_id, student_name, attendance_status, call_time, session_id) VALUES (?, ?, ?, NOW(), ?)")) {

            stmt.setString(1, studentId);
            stmt.setString(2, studentName);
            stmt.setString(3, status);
            stmt.setInt(4, currentSession.sessionId);
            stmt.executeUpdate();

            updateAttendanceStats(studentId, status);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateAttendanceStats(String studentId, String status) {
        String column = "";
        switch (status) {
            case "出勤": column = "total_attended"; break;
            case "请假": column = "total_leave"; break;
            case "旷课": column = "total_absent"; break;
            case "迟到": column = "total_late"; break;
        }

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE student_attendance_stats SET " + column + " = " + column + " + 1 WHERE student_id = ?")) {

            stmt.setString(1, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveSessionToDB() {
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO roll_call_sessions (session_name, start_time, call_type, strategy, student_count) VALUES (?, NOW(), ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, "点名-" + new java.util.Date());
            stmt.setString(2, currentSession.callType);

            // 处理策略：顺序点名在数据库中存储为"随机选取"
            String dbStrategy = currentSession.strategy;
            if ("顺序点名".equals(dbStrategy)) {
                dbStrategy = "随机选取"; // 数据库兼容处理
            }
            stmt.setString(3, dbStrategy);

            stmt.setInt(4, currentSession.studentCount);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                currentSession.sessionId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void togglePause() {
        if (rollCallTimer.isRunning()) {
            rollCallTimer.stop();
            pauseButton.setText("继续");
        } else {
            rollCallTimer.restart();
            pauseButton.setText("暂停");
        }
    }

    private void finishRollCall() {
        if (rollCallTimer != null && rollCallTimer.isRunning()) {
            rollCallTimer.stop();
        }

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE roll_call_sessions SET end_time = NOW() WHERE session_id = ?")) {

            stmt.setInt(1, currentSession.sessionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "点名完成！共点名 " + selectedStudents.size() + " 名学生。");

        startRollCallButton.setEnabled(true);
        pauseButton.setEnabled(false);
        pauseButton.setText("暂停");
    }

    private void showAttendanceRecords() {
        JDialog dialog = new JDialog(this, "考勤记录", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("记录ID");
        model.addColumn("学生ID");
        model.addColumn("学生姓名");
        model.addColumn("考勤状态");
        model.addColumn("点名时间");
        model.addColumn("所属批次");

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ar.record_id, ar.student_id, ar.student_name, ar.attendance_status, " +
                             "ar.call_time, rs.session_name FROM attendance_records ar " +
                             "LEFT JOIN roll_call_sessions rs ON ar.session_id = rs.session_id " +
                             "ORDER BY ar.call_time DESC LIMIT 100")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("record_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("attendance_status"),
                        rs.getTimestamp("call_time").toString(),
                        rs.getString("session_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "查询考勤记录失败: " + e.getMessage());
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("按学生姓名搜索:");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("搜索");

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                filterAttendanceRecords(model, keyword);
            } else {
                refreshAttendanceRecords(model);
            }
        });

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshAttendanceRecords(model));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        dialog.add(searchPanel, BorderLayout.NORTH);

        dialog.setVisible(true);
    }

    private void filterAttendanceRecords(DefaultTableModel model, String keyword) {
        model.setRowCount(0);

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ar.record_id, ar.student_id, ar.student_name, ar.attendance_status, " +
                             "ar.call_time, rs.session_name FROM attendance_records ar " +
                             "LEFT JOIN roll_call_sessions rs ON ar.session_id = rs.session_id " +
                             "WHERE ar.student_name LIKE ? ORDER BY ar.call_time DESC")) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("record_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("attendance_status"),
                        rs.getTimestamp("call_time").toString(),
                        rs.getString("session_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "搜索考勤记录失败: " + e.getMessage());
        }
    }

    private void refreshAttendanceRecords(DefaultTableModel model) {
        model.setRowCount(0);

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT ar.record_id, ar.student_id, ar.student_name, ar.attendance_status, " +
                             "ar.call_time, rs.session_name FROM attendance_records ar " +
                             "LEFT JOIN roll_call_sessions rs ON ar.session_id = rs.session_id " +
                             "ORDER BY ar.call_time DESC LIMIT 100")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("record_id"),
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("attendance_status"),
                        rs.getTimestamp("call_time").toString(),
                        rs.getString("session_name")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "刷新考勤记录失败: " + e.getMessage());
        }
    }

    private void showStatistics() {
        JDialog dialog = new JDialog(this, "考勤统计", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("学生ID");
        model.addColumn("姓名");
        model.addColumn("总出勤");
        model.addColumn("请假");
        model.addColumn("旷课");
        model.addColumn("迟到");
        model.addColumn("出勤率");

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.student_id, s.name, " +
                             "COALESCE(sas.total_attended, 0) as total_attended, " +
                             "COALESCE(sas.total_leave, 0) as total_leave, " +
                             "COALESCE(sas.total_absent, 0) as total_absent, " +
                             "COALESCE(sas.total_late, 0) as total_late " +
                             "FROM students s LEFT JOIN student_attendance_stats sas " +
                             "ON s.student_id = sas.student_id ORDER BY s.name")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String name = rs.getString("name");
                int attended = rs.getInt("total_attended");
                int leave = rs.getInt("total_leave");
                int absent = rs.getInt("total_absent");
                int late = rs.getInt("total_late");

                int total = attended + leave + absent + late;
                double rate = total > 0 ? (double)(attended + late) / total * 100 : 0;

                model.addRow(new Object[]{
                        studentId,
                        name,
                        attended,
                        leave,
                        absent,
                        late,
                        String.format("%.2f%%", rate)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "查询考勤统计失败: " + e.getMessage());
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT " +
                             "SUM(total_attended) as sum_attended, " +
                             "SUM(total_leave) as sum_leave, " +
                             "SUM(total_absent) as sum_absent, " +
                             "SUM(total_late) as sum_late " +
                             "FROM student_attendance_stats")) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int sumAttended = rs.getInt("sum_attended");
                int sumLeave = rs.getInt("sum_leave");
                int sumAbsent = rs.getInt("sum_absent");
                int sumLate = rs.getInt("sum_late");

                int totalRecords = sumAttended + sumLeave + sumAbsent + sumLate;
                double overallRate = totalRecords > 0 ? (double)(sumAttended + sumLate) / totalRecords * 100 : 0;

                summaryPanel.add(new JLabel("总出勤: " + sumAttended, JLabel.CENTER));
                summaryPanel.add(new JLabel("请假: " + sumLeave, JLabel.CENTER));
                summaryPanel.add(new JLabel("旷课: " + sumAbsent, JLabel.CENTER));
                summaryPanel.add(new JLabel("迟到: " + sumLate, JLabel.CENTER));
                summaryPanel.add(new JLabel("总记录: " + totalRecords, JLabel.CENTER));
                summaryPanel.add(new JLabel("整体出勤率: " + String.format("%.2f%%", overallRate), JLabel.CENTER));
                summaryPanel.add(new JLabel(""));
                summaryPanel.add(new JLabel(""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dialog.add(summaryPanel, BorderLayout.NORTH);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TeacherTangAttendanceSystem().setVisible(true);
        });
    }
}
