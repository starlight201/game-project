package com.game.codecounter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class CodeAnalyzerGUI extends JFrame {
    // 文件扩展名与语言映射
    private static final Map<String, String> LANGUAGE_EXTENSIONS = new HashMap<>();
    static {
        LANGUAGE_EXTENSIONS.put("java", "Java");
        LANGUAGE_EXTENSIONS.put("py", "Python");
        LANGUAGE_EXTENSIONS.put("cpp", "C++");
        LANGUAGE_EXTENSIONS.put("c", "C");
        LANGUAGE_EXTENSIONS.put("js", "JavaScript");
        LANGUAGE_EXTENSIONS.put("html", "HTML");
        LANGUAGE_EXTENSIONS.put("css", "CSS");
        LANGUAGE_EXTENSIONS.put("php", "PHP");
        LANGUAGE_EXTENSIONS.put("rb", "Ruby");
        LANGUAGE_EXTENSIONS.put("go", "Go");
        LANGUAGE_EXTENSIONS.put("rs", "Rust");
        LANGUAGE_EXTENSIONS.put("cs", "C#");
    }

    // 界面组件
    private JTextField pathField;
    private JButton browseButton;
    private JButton analyzeButton;
    private JPanel chartPanel;
    private JTextArea resultArea;
    private JTabbedPane tabbedPane;
    private JPanel barChartPanel;
    private JPanel pieChartPanel;
    private JPanel statsPanel;
    private JPanel tablePanel;
    private JTable resultTable;
    private JButton exportButton;

    // 统计数据
    private Map<String, Integer> languageLines = new HashMap<>();
    private Map<String, Object> pythonFunctionStats = new HashMap<>();
    private Map<String, LanguageDetailedStats> detailedStats = new HashMap<>();

    public CodeAnalyzerGUI() {
        setTitle("代码统计工具");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        // 路径输入组件
        pathField = new JTextField(30);
        browseButton = new JButton("浏览...");
        analyzeButton = new JButton("开始统计");

        // 结果展示组件
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        resultArea.setBorder(new TitledBorder("统计结果"));

        // 图表面板
        chartPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        barChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        barChartPanel.setPreferredSize(new Dimension(400, 300));
        barChartPanel.setBorder(new TitledBorder("代码行数柱状图"));

        pieChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPieChart(g);
            }
        };
        pieChartPanel.setPreferredSize(new Dimension(400, 300));
        pieChartPanel.setBorder(new TitledBorder("代码行数饼状图"));

        statsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawStatsChart(g);
            }
        };
        statsPanel.setPreferredSize(new Dimension(400, 300));
        statsPanel.setBorder(new TitledBorder("Python函数统计"));

        // 表格面板
        tablePanel = new JPanel(new BorderLayout());
        resultTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBorder(new TitledBorder("统计结果表格"));

        // 导出按钮
        exportButton = new JButton("导出表格");
        exportButton.addActionListener(new ExportAction());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);

        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加标签页
        tabbedPane.addTab("柱状图", barChartPanel);
        tabbedPane.addTab("饼状图", pieChartPanel);
        tabbedPane.addTab("Python函数统计", statsPanel);
        tabbedPane.addTab("数据表格", tablePanel);

        // 添加事件监听器
        browseButton.addActionListener(new BrowseAction());
        analyzeButton.addActionListener(new AnalyzeAction());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // 顶部面板 - 路径选择
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.add(new JLabel("文件夹路径:"));
        topPanel.add(pathField);
        topPanel.add(browseButton);
        topPanel.add(analyzeButton);

        add(topPanel, BorderLayout.NORTH);

        // 中间面板 - 图表和结果
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setPreferredSize(new Dimension(300, 200));
        centerPanel.add(resultScrollPane, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    // 浏览文件夹动作
    private class BrowseAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setDialogTitle("选择要统计的文件夹");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                pathField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    // 分析动作
    private class AnalyzeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String folderPath = pathField.getText().trim();
            if (folderPath.isEmpty()) {
                JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                        "请输入文件夹路径", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                        "文件夹不存在或路径不是目录", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 清空之前的结果
            languageLines.clear();
            detailedStats.clear();
            pythonFunctionStats.clear();
            resultArea.setText("");

            // 开始分析
            analyzeFolder(folder);

            // 计算最终统计值
            calculateFinalStats();

            // 显示结果
            displayResults();

            // 更新表格
            updateTable();

            // 重绘图表
            barChartPanel.repaint();
            pieChartPanel.repaint();
            statsPanel.repaint();
        }
    }

    // 递归分析文件夹
    private void analyzeFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                analyzeFolder(file);
            } else {
                analyzeFile(file);
            }
        }
    }

    // 分析单个文件
    private void analyzeFile(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) return;

        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        String language = LANGUAGE_EXTENSIONS.get(extension);

        if (language != null) {
            // 获取详细统计信息
            FileDetailedInfo fileInfo = countFileDetailedLines(file, language);

            // 更新语言行数统计
            languageLines.put(language, languageLines.getOrDefault(language, 0) + fileInfo.totalLines);

            // 更新详细统计
            LanguageDetailedStats stats = detailedStats.getOrDefault(language, new LanguageDetailedStats());
            stats.fileCount++;
            stats.totalLines += fileInfo.totalLines;
            stats.blankLines += fileInfo.blankLines;
            stats.commentLines += fileInfo.commentLines;
            stats.codeLines += fileInfo.codeLines;

            // 如果是Python文件，分析函数
            if ("Python".equals(language)) {
                PythonFunctionStats pythonStats = analyzePythonFunctions(file);
                stats.functionCount += pythonStats.functionCount;
                stats.functionLengths.addAll(pythonStats.functionLengths);
            }

            detailedStats.put(language, stats);
        }
    }

    // 详细统计文件行数
    private FileDetailedInfo countFileDetailedLines(File file, String language) {
        FileDetailedInfo info = new FileDetailedInfo();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean inBlockComment = false;

            while ((line = reader.readLine()) != null) {
                info.totalLines++;
                String trimmedLine = line.trim();

                // 统计空行
                if (trimmedLine.isEmpty()) {
                    info.blankLines++;
                    continue;
                }

                // 根据语言统计注释
                if (isCommentLine(trimmedLine, language, inBlockComment)) {
                    info.commentLines++;
                } else {
                    info.codeLines++;
                }

                // 更新块注释状态
                inBlockComment = updateCommentState(trimmedLine, language, inBlockComment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }

    // 判断是否为注释行 - 修改版，支持Python三引号注释
    private boolean isCommentLine(String line, String language, boolean inBlockComment) {
        if (inBlockComment) {
            return true;
        }

        switch (language) {
            case "Java":
            case "C":
            case "C++":
            case "C#":
            case "JavaScript":
            case "Go":
            case "Rust":
                return line.startsWith("//") || line.startsWith("/*");

            case "Python":
                // 单行注释
                if (line.startsWith("#")) {
                    return true;
                }
                // 三引号注释（多行注释）
                if (line.startsWith("\"\"\"") || line.startsWith("'''")) {
                    // 检查是否在同一行结束
                    if (line.endsWith("\"\"\"") || line.endsWith("'''")) {
                        return true;
                    }
                    // 如果不在同一行结束，则开始块注释
                    // 块注释状态会在updateCommentState中处理
                    return true;
                }
                return false;

            case "HTML":
                return line.startsWith("<!--");

            case "CSS":
                return line.startsWith("/*") || line.startsWith("//");

            case "PHP":
                return line.startsWith("//") || line.startsWith("#") || line.startsWith("/*");

            case "Ruby":
                return line.startsWith("#");

            default:
                return false;
        }
    }

    // 更新注释状态 - 修改版
    private boolean updateCommentState(String line, String language, boolean inBlockComment) {
        boolean newState = inBlockComment;

        switch (language) {
            case "Java":
            case "C":
            case "C++":
            case "C#":
            case "JavaScript":
            case "Go":
            case "Rust":
            case "CSS":
            case "PHP":
                if (!inBlockComment && line.contains("/*")) {
                    newState = true;
                }
                if (inBlockComment && line.contains("*/")) {
                    newState = false;
                }
                break;

            case "Python":
                // 处理三引号注释
                if (!inBlockComment && (line.startsWith("\"\"\"") || line.startsWith("'''"))) {
                    // 检查是否在同一行结束
                    if (!(line.endsWith("\"\"\"") || line.endsWith("'''"))) {
                        newState = true;
                    }
                } else if (inBlockComment && (line.endsWith("\"\"\"") || line.endsWith("'''"))) {
                    newState = false;
                }
                break;

            case "HTML":
                if (!inBlockComment && line.contains("<!--")) {
                    newState = true;
                }
                if (inBlockComment && line.contains("-->")) {
                    newState = false;
                }
                break;
        }

        return newState;
    }

    // 分析Python函数 - 修改版
    private PythonFunctionStats analyzePythonFunctions(File file) {
        PythonFunctionStats stats = new PythonFunctionStats();

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<FunctionInfo> functionInfos = new ArrayList<>();

            // 用于跟踪当前函数定义
            FunctionInfo currentFunction = null;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                String originalLine = lines.get(i);

                // 跳过空行和注释
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // 检查函数定义
                if (isPythonFunctionDefinition(line)) {
                    // 结束当前函数（如果有）
                    if (currentFunction != null) {
                        currentFunction.endLine = i - 1;
                        functionInfos.add(currentFunction);
                    }

                    // 开始新函数
                    currentFunction = new FunctionInfo();
                    currentFunction.name = extractPythonFunctionName(line);
                    currentFunction.startLine = i;
                    currentFunction.indentLevel = getIndentationLevel(originalLine);
                    currentFunction.isMethod = false; // 稍后检查

                } else if (currentFunction != null) {
                    // 检查函数是否结束
                    int currentIndent = getIndentationLevel(originalLine);

                    // 如果当前缩进小于等于函数定义缩进，且不是空行和注释，则函数结束
                    // 注意：这里需要检查真正的缩进，考虑空行和注释的跳过
                    if (!line.isEmpty() && !line.startsWith("#") && currentIndent <= currentFunction.indentLevel) {
                        currentFunction.endLine = i - 1;
                        functionInfos.add(currentFunction);
                        currentFunction = null;
                    }
                }
            }

            // 处理最后一个函数
            if (currentFunction != null) {
                currentFunction.endLine = lines.size() - 1;
                functionInfos.add(currentFunction);
            }

            // 计算函数长度 - 修改：使用正确的范围
            for (FunctionInfo func : functionInfos) {
                int length = calculatePythonFunctionLength(lines, func);
                if (length > 0) {
                    stats.functionLengths.add(length);
                    stats.functionCount++;
                }
            }

            // 存储详细的Python函数统计信息
            if (!functionInfos.isEmpty()) {
                Map<String, Object> detailedStats = new HashMap<>();
                List<Integer> functionLengths = new ArrayList<>();
                List<Integer> methodLengths = new ArrayList<>();

                for (FunctionInfo func : functionInfos) {
                    int length = calculatePythonFunctionLength(lines, func);
                    if (length > 0) {
                        // 检查是否是类方法
                        if (isPythonMethod(lines, func.startLine)) {
                            methodLengths.add(length);
                            func.isMethod = true;
                        } else {
                            functionLengths.add(length);
                        }
                    }
                }

                if (!functionLengths.isEmpty()) {
                    detailedStats.put("function_lengths", functionLengths);
                    detailedStats.put("function_count", functionLengths.size());
                    detailedStats.put("function_min", Collections.min(functionLengths));
                    detailedStats.put("function_max", Collections.max(functionLengths));
                    detailedStats.put("function_mean", calculateMean(functionLengths));
                    detailedStats.put("function_median", calculateMedian(functionLengths));
                }

                if (!methodLengths.isEmpty()) {
                    detailedStats.put("method_lengths", methodLengths);
                    detailedStats.put("method_count", methodLengths.size());
                    detailedStats.put("method_min", Collections.min(methodLengths));
                    detailedStats.put("method_max", Collections.max(methodLengths));
                    detailedStats.put("method_mean", calculateMean(methodLengths));
                    detailedStats.put("method_median", calculateMedian(methodLengths));
                }

                detailedStats.put("total_functions", functionLengths.size() + methodLengths.size());
                pythonFunctionStats.put(file.getName(), detailedStats);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stats;
    }

    // 检查是否是Python函数定义 - 修改版，排除lambda表达式
    private boolean isPythonFunctionDefinition(String line) {
        // 排除lambda表达式
        if (line.contains("lambda ") && !line.contains("def ")) {
            return false;
        }

        return line.startsWith("def ") ||
                line.startsWith("async def ");
    }

    // 提取Python函数名
    private String extractPythonFunctionName(String line) {
        String trimmed = line.trim();

        if (trimmed.startsWith("async ")) {
            trimmed = trimmed.substring(6).trim();
        }

        if (trimmed.startsWith("def ")) {
            trimmed = trimmed.substring(4).trim();
        }

        int parenIndex = trimmed.indexOf('(');
        if (parenIndex != -1) {
            return trimmed.substring(0, parenIndex).trim();
        }

        int colonIndex = trimmed.indexOf(':');
        if (colonIndex != -1) {
            return trimmed.substring(0, colonIndex).trim();
        }

        return trimmed;
    }

    // 检查是否是Python类方法
    private boolean isPythonMethod(List<String> lines, int functionLine) {
        // 向上查找类定义
        for (int i = functionLine - 1; i >= 0; i--) {
            String line = lines.get(i).trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // 如果找到类定义，则是类方法
            if (line.startsWith("class ")) {
                return true;
            }

            // 如果遇到缩进小于等于0的行，说明不在任何类中
            int indent = getIndentationLevel(lines.get(i));
            if (indent == 0 && !line.startsWith("class ")) {
                break;
            }
        }

        return false;
    }

    // 计算缩进级别
    private int getIndentationLevel(String line) {
        int indent = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ' ') {
                indent++;
            } else if (c == '\t') {
                indent += 4; // 假设tab等于4个空格
            } else {
                break;
            }
        }
        return indent;
    }

    // 计算Python函数长度 - 修改：使用正确的范围 [start+1, end-1]
    private int calculatePythonFunctionLength(List<String> lines, FunctionInfo func) {
        // 函数体范围 [start+1, end-1]
        int start = func.startLine + 1;
        int end = func.endLine - 1;

        // 确保范围有效
        if (start > end || end >= lines.size()) {
            return 0;
        }

        int length = 0;
        boolean inDocstring = false;

        for (int i = start; i <= end; i++) {
            String line = lines.get(i).trim();

            // 跳过空行
            if (line.isEmpty()) {
                continue;
            }

            // 处理文档字符串
            if (!inDocstring && (line.startsWith("\"\"\"") || line.startsWith("'''"))) {
                inDocstring = true;
                // 检查是否在同一行结束
                if (line.endsWith("\"\"\"") || line.endsWith("'''")) {
                    inDocstring = false;
                }
                continue;
            }

            if (inDocstring) {
                if (line.endsWith("\"\"\"") || line.endsWith("'''")) {
                    inDocstring = false;
                }
                continue;
            }

            // 跳过单行注释
            if (line.startsWith("#")) {
                continue;
            }

            // 跳过装饰器
            if (line.startsWith("@")) {
                continue;
            }

            // 有效代码行
            length++;
        }

        return length;
    }

    // 计算平均值
    private double calculateMean(List<Integer> list) {
        if (list.isEmpty()) return 0;
        return list.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    // 计算中位数
    private double calculateMedian(List<Integer> list) {
        if (list.isEmpty()) return 0;

        Collections.sort(list);
        int size = list.size();

        if (size % 2 == 0) {
            return (list.get(size/2 - 1) + list.get(size/2)) / 2.0;
        } else {
            return list.get(size/2);
        }
    }

    // 在分析完成后计算统计值
    private void calculateFinalStats() {
        for (LanguageDetailedStats stats : detailedStats.values()) {
            if (!stats.functionLengths.isEmpty()) {
                stats.maxLines = Collections.max(stats.functionLengths);
                stats.minLines = Collections.min(stats.functionLengths);
                stats.meanLines = calculateMean(stats.functionLengths);
                stats.medianLines = calculateMedian(stats.functionLengths);
            }
        }
    }

    // 显示结果
    private void displayResults() {
        StringBuilder result = new StringBuilder();
        result.append("代码行数统计:\n");
        result.append("============\n");

        int totalLines = 0;
        int totalFiles = 0;
        for (Map.Entry<String, Integer> entry : languageLines.entrySet()) {
            LanguageDetailedStats stats = detailedStats.get(entry.getKey());
            if (stats != null) {
                result.append(String.format("%s: %d 行 (%d 个文件)\n", entry.getKey(), entry.getValue(), stats.fileCount));
                totalLines += entry.getValue();
                totalFiles += stats.fileCount;
            }
        }
        result.append(String.format("总计: %d 个文件, %d 行\n\n", totalFiles, totalLines));

        // 显示详细统计
        result.append("详细统计:\n");
        result.append("============\n");

        for (Map.Entry<String, LanguageDetailedStats> entry : detailedStats.entrySet()) {
            LanguageDetailedStats stats = entry.getValue();
            result.append(String.format("\n%s:\n", entry.getKey()));
            result.append(String.format("  源文件数: %d\n", stats.fileCount));
            result.append(String.format("  总行数: %d\n", stats.totalLines));
            result.append(String.format("  代码行: %d (%.1f%%)\n", stats.codeLines,
                    stats.totalLines > 0 ? (double)stats.codeLines / stats.totalLines * 100 : 0));
            result.append(String.format("  注释行: %d (%.1f%%)\n", stats.commentLines,
                    stats.totalLines > 0 ? (double)stats.commentLines / stats.totalLines * 100 : 0));
            result.append(String.format("  空行: %d (%.1f%%)\n", stats.blankLines,
                    stats.totalLines > 0 ? (double)stats.blankLines / stats.totalLines * 100 : 0));

            if (stats.functionCount > 0) {
                result.append(String.format("  函数个数: %d\n", stats.functionCount));
                result.append(String.format("  函数长度统计: 最小%d行, 最大%d行, 平均%.2f行, 中位%.2f行\n",
                        stats.minLines, stats.maxLines, stats.meanLines, stats.medianLines));
            }
        }

        result.append("\n");

        if (!pythonFunctionStats.isEmpty()) {
            result.append("Python函数统计:\n");
            result.append("==================\n");

            int totalFunctions = 0;
            int totalMethods = 0;

            for (Map.Entry<String, Object> fileEntry : pythonFunctionStats.entrySet()) {
                String fileName = fileEntry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, Object> stats = (Map<String, Object>) fileEntry.getValue();

                result.append(String.format("文件: %s\n", fileName));

                if (stats.containsKey("function_count")) {
                    int functionCount = (int) stats.get("function_count");
                    totalFunctions += functionCount;
                    result.append(String.format("  函数数量: %d\n", functionCount));
                    result.append(String.format("  函数长度: 最小%d行, 最大%d行, 平均%.2f行, 中位%.2f行\n",
                            stats.get("function_min"), stats.get("function_max"),
                            stats.get("function_mean"), stats.get("function_median")));
                }

                if (stats.containsKey("method_count")) {
                    int methodCount = (int) stats.get("method_count");
                    totalMethods += methodCount;
                    result.append(String.format("  方法数量: %d\n", methodCount));
                    result.append(String.format("  方法长度: 最小%d行, 最大%d行, 平均%.2f行, 中位%.2f行\n",
                            stats.get("method_min"), stats.get("method_max"),
                            stats.get("method_mean"), stats.get("method_median")));
                }

                result.append("\n");
            }

            result.append(String.format("总计: %d 个函数 (%d 个普通函数, %d 个类方法)\n\n",
                    totalFunctions + totalMethods, totalFunctions, totalMethods));

        } else {
            result.append("未找到Python函数定义\n");
        }

        resultArea.setText(result.toString());
    }

    // 更新表格数据
    private void updateTable() {
        StatsTableModel model = new StatsTableModel();
        model.setData(detailedStats);
        resultTable.setModel(model);

        // 设置表格样式
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("宋体", Font.BOLD, 12));
        resultTable.setFont(new Font("宋体", Font.PLAIN, 12));

        // 自动调整列宽
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setPreferredWidth(80);
        }
    }

    // 导出动作
    private class ExportAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (detailedStats.isEmpty()) {
                JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                        "没有数据可导出", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("导出表格数据");
            fileChooser.setSelectedFile(new File("代码统计结果.csv"));

            int returnValue = fileChooser.showSaveDialog(CodeAnalyzerGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                exportToCSV(file);
            }
        }
    }

    // 导出为CSV文件
    private void exportToCSV(File file) {
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            // 写入表头
            writer.println("语言,源文件数,总行数,代码行数,空行数,注释行数,代码占比(%),注释占比(%),空行占比(%),函数个数,最大值,最小值,均值,中位数");

            // 写入数据
            for (Map.Entry<String, LanguageDetailedStats> entry : detailedStats.entrySet()) {
                LanguageDetailedStats stats = entry.getValue();
                writer.printf("%s,%d,%d,%d,%d,%d,%s,%s,%s,%d,%s,%s,%s,%s%n",
                        entry.getKey(),
                        stats.fileCount,
                        stats.totalLines,
                        stats.codeLines,
                        stats.blankLines,
                        stats.commentLines,
                        String.format("%.1f", stats.totalLines > 0 ? (double)stats.codeLines / stats.totalLines * 100 : 0),
                        String.format("%.1f", stats.totalLines > 0 ? (double)stats.commentLines / stats.totalLines * 100 : 0),
                        String.format("%.1f", stats.totalLines > 0 ? (double)stats.blankLines / stats.totalLines * 100 : 0),
                        stats.functionCount,
                        stats.maxLines > 0 ? String.valueOf(stats.maxLines) : "-",
                        stats.minLines > 0 ? String.valueOf(stats.minLines) : "-",
                        stats.meanLines > 0 ? String.format("%.2f", stats.meanLines) : "-",
                        stats.medianLines > 0 ? String.format("%.2f", stats.medianLines) : "-");
            }

            JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                    "数据已成功导出到: " + file.getAbsolutePath(),
                    "导出成功", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(CodeAnalyzerGUI.this,
                    "导出失败: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 绘制柱状图
    private void drawBarChart(Graphics g) {
        if (languageLines.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("暂无数据", 150, 150);
            return;
        }

        int width = barChartPanel.getWidth();
        int height = barChartPanel.getHeight();
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        // 清除背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 绘制坐标轴
        g.setColor(Color.BLACK);
        g.drawLine(padding, height - padding, width - padding, height - padding); // X轴
        g.drawLine(padding, padding, padding, height - padding); // Y轴

        // 计算最大值用于缩放
        int maxValue = languageLines.values().stream().max(Integer::compare).orElse(1);

        // 绘制柱状图
        String[] languages = languageLines.keySet().toArray(new String[0]);
        int barWidth = chartWidth / (languages.length * 2);

        for (int i = 0; i < languages.length; i++) {
            int value = languageLines.get(languages[i]);
            int barHeight = (int) ((double) value / maxValue * chartHeight);
            int x = padding + i * (barWidth * 2) + barWidth / 2;
            int y = height - padding - barHeight;

            // 绘制柱子
            g.setColor(new Color(70, 130, 180));
            g.fillRect(x, y, barWidth, barHeight);

            // 绘制数值
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(value), x, y - 5);

            // 绘制标签
            g.drawString(languages[i], x, height - padding + 15);
        }

        // 绘制标题
        g.setFont(new Font("宋体", Font.BOLD, 14));
        g.drawString("各语言代码行数统计", width / 2 - 50, 20);
    }

    // 绘制饼状图
    private void drawPieChart(Graphics g) {
        if (languageLines.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("暂无数据", 150, 150);
            return;
        }

        int width = pieChartPanel.getWidth();
        int height = pieChartPanel.getHeight();

        // 清除背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 计算总计
        int total = languageLines.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return;

        // 绘制饼图
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;

        int startAngle = 0;
        Color[] colors = {
                new Color(70, 130, 180),   // 钢蓝色
                new Color(220, 20, 60),    // 深红色
                new Color(34, 139, 34),    // 森林绿
                new Color(255, 140, 0),    // 深橙色
                new Color(148, 0, 211),    // 深紫色
                new Color(255, 215, 0),    // 金色
                new Color(0, 206, 209),    // 深绿松石色
                new Color(240, 128, 128),  // 浅珊瑚色
                new Color(154, 205, 50),   // 黄绿色
                new Color(139, 69, 19)     // saddle棕色
        };

        int colorIndex = 0;
        int i = 0;
        for (Map.Entry<String, Integer> entry : languageLines.entrySet()) {
            int angle = (int) (360.0 * entry.getValue() / total);

            g.setColor(colors[colorIndex % colors.length]);
            g.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, angle);

            // 绘制图例
            int legendX = 20;
            int legendY = 20 + i * 20;
            g.fillRect(legendX, legendY, 15, 15);
            g.setColor(Color.BLACK);
            g.drawString(String.format("%s: %.1f%%", entry.getKey(), 100.0 * entry.getValue() / total),
                    legendX + 20, legendY + 12);

            startAngle += angle;
            colorIndex++;
            i++;
        }

        // 绘制标题
        g.setFont(new Font("宋体", Font.BOLD, 14));
        g.drawString("代码行数分布", width / 2 - 30, centerY + radius + 30);
    }

    // 绘制Python函数统计图表
    private void drawStatsChart(Graphics g) {
        if (pythonFunctionStats.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("暂无Python函数数据", 150, 150);
            return;
        }

        int width = statsPanel.getWidth();
        int height = statsPanel.getHeight();
        int padding = 50;

        // 清除背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.PLAIN, 12));

        int y = padding;
        int totalFunctions = 0;
        int totalMethods = 0;

        for (Map.Entry<String, Object> entry : pythonFunctionStats.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = (Map<String, Object>) entry.getValue();

            g.drawString("文件: " + entry.getKey(), padding, y);
            y += 20;

            if (stats.containsKey("function_count")) {
                int count = (int) stats.get("function_count");
                totalFunctions += count;
                g.drawString(String.format("  函数: %d 个", count), padding, y);
                y += 15;
            }

            if (stats.containsKey("method_count")) {
                int count = (int) stats.get("method_count");
                totalMethods += count;
                g.drawString(String.format("  方法: %d 个", count), padding, y);
                y += 15;
            }

            y += 10;
        }

        g.drawString(String.format("总计: %d 个函数 (%d 函数 + %d 方法)",
                totalFunctions + totalMethods, totalFunctions, totalMethods), padding, y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            CodeAnalyzerGUI gui = new CodeAnalyzerGUI();
            gui.setVisible(true);
        });
    }
}

// 辅助类定义
class FileDetailedInfo {
    int totalLines = 0;
    int blankLines = 0;
    int commentLines = 0;
    int codeLines = 0;
}

class LanguageDetailedStats {
    int fileCount = 0;
    int totalLines = 0;
    int blankLines = 0;
    int commentLines = 0;
    int codeLines = 0;
    int functionCount = 0;
    List<Integer> functionLengths = new ArrayList<>();
    int maxLines = 0;
    int minLines = 0;
    double meanLines = 0;
    double medianLines = 0;
}

class PythonFunctionStats {
    int functionCount = 0;
    List<Integer> functionLengths = new ArrayList<>();
}

class FunctionInfo {
    String name;
    int startLine;
    int endLine;
    int indentLevel;
    boolean isMethod;
}

class StatsTableModel extends javax.swing.table.AbstractTableModel {
    private Map<String, LanguageDetailedStats> data = new HashMap<>();
    private String[] columnNames = {"语言", "文件数", "总行数", "代码行", "空行", "注释行", "函数数", "最大长度", "最小长度", "平均长度", "中位长度"};

    public void setData(Map<String, LanguageDetailedStats> data) {
        this.data = data;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String language = (String) data.keySet().toArray()[rowIndex];
        LanguageDetailedStats stats = data.get(language);

        switch (columnIndex) {
            case 0: return language;
            case 1: return stats.fileCount;
            case 2: return stats.totalLines;
            case 3: return stats.codeLines;
            case 4: return stats.blankLines;
            case 5: return stats.commentLines;
            case 6: return stats.functionCount;
            case 7: return stats.maxLines > 0 ? stats.maxLines : "-";
            case 8: return stats.minLines > 0 ? stats.minLines : "-";
            case 9: return stats.meanLines > 0 ? String.format("%.2f", stats.meanLines) : "-";
            case 10: return stats.medianLines > 0 ? String.format("%.2f", stats.medianLines) : "-";
            default: return null;
        }
    }
}