package com.augustlee.mt.toolWindow.otherdevtools.devfuncbtn;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ST菜单转换工具
 * 将菜单数据库SQL转换为Lion JSON格式
 *
 * @see MenuPrivilegeConverter
 * @author August Lee
 * @since 2025/11/27 11:51
 */
public class MenuPrivilegeConverter {

    /**
     * 主面板
     */
    private final JPanel MAIN_PANEL = new JPanel();

    /**
     * 菜单数据库SQL标签
     */
    private final JLabel SQL_LABEL = new JLabel("菜单数据库SQL：");

    /**
     * 菜单数据库SQL输入框
     */
    private final JTextArea SQL_TEXT_AREA = new JTextArea(10, 50);

    /**
     * 转换按钮
     */
    private final JButton CONVERT_BUTTON = new JButton("转换");

    /**
     * 结果区域面板
     */
    private final JPanel RESULT_PANEL = new JPanel();

    /**
     * 错误信息面板
     */
    private final JPanel ERROR_PANEL = new JPanel();

    /**
     * 错误信息文本区域
     */
    private final JTextArea ERROR_TEXT_AREA = new JTextArea(3, 50);

    /**
     * 项目对象
     */
    private Project project;

    /**
     * 构造函数
     *
     * @param project 项目对象
     */
    public MenuPrivilegeConverter(@NotNull Project project) {
        this.project = project;
        this.initLayout();
        this.initComponents();
    }

    /**
     * 获取主面板
     *
     * @return 主面板
     */
    public JPanel getMainPanel() {
        return MAIN_PANEL;
    }

    /**
     * 初始化布局
     */
    private void initLayout() {
        MAIN_PANEL.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：菜单数据库SQL标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        MAIN_PANEL.add(SQL_LABEL, gbc);

        // 第二行：SQL输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.BOTH;
        JBScrollPane sqlScrollPane = new JBScrollPane(SQL_TEXT_AREA);
        sqlScrollPane.setPreferredSize(new Dimension(0, 150));
        MAIN_PANEL.add(sqlScrollPane, gbc);

        // 第三行：转换按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        MAIN_PANEL.add(CONVERT_BUTTON, gbc);

        // 第四行：结果区域（初始隐藏）
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        setupResultPanel();
        RESULT_PANEL.setVisible(false);
        MAIN_PANEL.add(RESULT_PANEL, gbc);

        // 第五行：错误信息区域（初始隐藏）
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        setupErrorPanel();
        ERROR_PANEL.setVisible(false);
        ERROR_PANEL.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        MAIN_PANEL.add(ERROR_PANEL, gbc);
    }

    /**
     * 设置结果面板
     */
    private void setupResultPanel() {
        RESULT_PANEL.setLayout(new GridBagLayout());
        RESULT_PANEL.setBorder(BorderFactory.createTitledBorder("结果"));
    }

    /**
     * 设置错误面板
     */
    private void setupErrorPanel() {
        ERROR_PANEL.setLayout(new BorderLayout());
        ERROR_PANEL.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(JBColor.RED, 1),
                "错误信息",
                0, 0,
                new Font(Font.SANS_SERIF, Font.BOLD, 11),
                Color.RED
        ));

        ERROR_TEXT_AREA.setEditable(false);
        ERROR_TEXT_AREA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        ERROR_TEXT_AREA.setForeground(JBColor.RED);
        ERROR_TEXT_AREA.setBackground(new Color(255, 240, 240));
        ERROR_TEXT_AREA.setLineWrap(true);
        ERROR_TEXT_AREA.setWrapStyleWord(true);

        JBScrollPane errorScrollPane = new JBScrollPane(ERROR_TEXT_AREA);
        errorScrollPane.setPreferredSize(new Dimension(0, 80));
        errorScrollPane.setMinimumSize(new Dimension(0, 60));
        errorScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        ERROR_PANEL.add(errorScrollPane, BorderLayout.CENTER);

        // 添加关闭按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        JButton closeErrorButton = new JButton("×");
        closeErrorButton.setPreferredSize(new Dimension(25, 25));
        closeErrorButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        closeErrorButton.setForeground(JBColor.RED);
        closeErrorButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        closeErrorButton.addActionListener(e -> hideError());
        buttonPanel.add(closeErrorButton);
        ERROR_PANEL.add(buttonPanel, BorderLayout.EAST);
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        SQL_TEXT_AREA.setLineWrap(true);
        SQL_TEXT_AREA.setWrapStyleWord(true);
        SQL_TEXT_AREA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        CONVERT_BUTTON.addActionListener(e -> performConvert());
    }

    /**
     * 执行转换
     */
    private void performConvert() {
        String sql = SQL_TEXT_AREA.getText().trim();

        // 验证输入不能为空
        if (sql.isEmpty()) {
            Messages.showErrorDialog(project, "菜单数据库SQL不能为空", "输入错误");
            return;
        }

        try {
            // 隐藏错误信息
            hideError();

            // 执行转换逻辑
            ConversionResult result = convertSqlToJson(sql);

            // 显示结果
            displayResult(result);

        } catch (Exception e) {
            // 显示错误信息
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getClass().getSimpleName() + ": " + e.toString();
            }
            showError(errorMessage);
            e.printStackTrace();
        }
    }

    /**
     * 将SQL转换为JSON
     *
     * @param sql SQL字符串
     * @return 转换结果
     */
    private ConversionResult convertSqlToJson(String sql) {
        // 按 platformId 分组存储
        Map<Integer, List<Map<String, Object>>> groupedMap = new HashMap<>();

        // 平台
        groupedMap.put(0, new ArrayList<>());
        // 供应商
        groupedMap.put(1, new ArrayList<>());
        // 商家
        groupedMap.put(2, new ArrayList<>());

        // 正则更宽松：允许 NULL | '' | 'xxx' | 数字
        Pattern pattern = Pattern.compile(
                "VALUES \\(([^,]+), '([^']*)', '([^']*)', ([^,]+), ([^,]+), ([^,]+), b'([01])', ([^,]+), ([^,]+), ([^,]+), ([^,]+), ([^,]+), ([^,]+), ([^)]*)\\);"
        );

        Matcher matcher = pattern.matcher(sql);
        List<String> errors = new ArrayList<>();

        while (matcher.find()) {
            try {
                Map<String, Object> node = new HashMap<>();

                node.put("id", Long.parseLong(matcher.group(1).trim()));
                node.put("name", matcher.group(2));
                node.put("url", matcher.group(3));
                node.put("icon", parseValue(matcher.group(4)));
                node.put("action", parseValue(matcher.group(5)));
                node.put("parentId", parseValue(matcher.group(6)));
                node.put("whetherDelete", !"0".equals(matcher.group(7)));
                node.put("displaySequence", parseValue(matcher.group(8)));

                int platformId = Integer.parseInt(matcher.group(9).trim());
                node.put("platformId", platformId);
                node.put("powerType", parseValue(matcher.group(10)));

                // createTime 和 updateTime 不包含在输出中
                // node.put("createTime", parseValue(matcher.group(11)));
                // node.put("updateTime", parseValue(matcher.group(12)));

                node.put("shopType", parseValue(matcher.group(13)));
                node.put("activedIcon", parseValue(matcher.group(14)));

                if (groupedMap.containsKey(platformId)) {
                    groupedMap.get(platformId).add(node);
                } else {
                    errors.add("未知的 platformId: " + platformId);
                }
            } catch (Exception e) {
                errors.add("解析SQL行时出错: " + matcher.group(0) + " - " + e.getMessage());
            }
        }

        // 生成JSON字符串
        String platformPrivilegeJson = formatGroup("user.platformPrivilege", groupedMap.get(0));
        String sellerPrivilegeJson = formatGroup("user.sellerPrivilege", groupedMap.get(1));
        String shopPrivilegeJson = formatGroup("user.shopPrivilege", groupedMap.get(2));

        return new ConversionResult(platformPrivilegeJson, sellerPrivilegeJson, shopPrivilegeJson, errors);
    }

    /**
     * 公用的 NULL/字符串/数字 解析器
     *
     * @param raw 原始值
     * @return 解析后的值
     */
    private Object parseValue(String raw) {
        if (raw == null) {
            return null;
        }
        raw = raw.trim();
        if (raw.equalsIgnoreCase("NULL")) {
            return null;
        }
        if (raw.startsWith("'") && raw.endsWith("'")) {
            String val = raw.substring(1, raw.length() - 1);
            return val.isEmpty() ? null : val;
        }
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return raw;
        }
    }

    /**
     * 格式化分组数据为JSON数组字符串
     * 如果分组为空，返回空字符串（不显示）
     *
     * @param key 键名（仅用于标识，不包含在返回结果中）
     * @param group 分组数据
     * @return JSON数组字符串，如果分组为空则返回空字符串
     */
    private String formatGroup(String key, List<Map<String, Object>> group) {
        if (group.isEmpty()) {
            return "";
        }

        // 只返回JSON数组，不包含key前缀
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < group.size(); i++) {
            sb.append(mapToJson(group.get(i)));
            if (i < group.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 将Map转换为JSON字符串
     *
     * @param map Map对象
     * @return JSON字符串
     */
    private String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        List<String> entries = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            entries.add("\"" + escapeJson(entry.getKey()) + "\":" + valueToJson(entry.getValue()));
        }
        sb.append(String.join(", ", entries));
        sb.append("}");
        return sb.toString();
    }

    /**
     * 将值转换为JSON格式
     *
     * @param value 值对象
     * @return JSON字符串
     */
    private String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }

    /**
     * 转义JSON字符串中的特殊字符
     *
     * @param s 原始字符串
     * @return 转义后的字符串
     */
    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 显示结果
     *
     * @param result 转换结果
     */
    private void displayResult(ConversionResult result) {
        RESULT_PANEL.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        // 减少间距，优化布局
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // 显示三个结果区域
        if (!result.getPlatformPrivilegeJson().isEmpty()) {
            row = addResultRow(RESULT_PANEL, "user.platformPrivilege", result.getPlatformPrivilegeJson(), gbc, row);
        }
        if (!result.getSellerPrivilegeJson().isEmpty()) {
            row = addResultRow(RESULT_PANEL, "user.sellerPrivilege", result.getSellerPrivilegeJson(), gbc, row);
        }
        if (!result.getShopPrivilegeJson().isEmpty()) {
            row = addResultRow(RESULT_PANEL, "user.shopPrivilege", result.getShopPrivilegeJson(), gbc, row);
        }

        // 如果有错误，显示错误信息
        if (!result.getErrors().isEmpty()) {
            showError(String.join("\n", result.getErrors()));
        }

        RESULT_PANEL.setVisible(true);
        RESULT_PANEL.revalidate();
        RESULT_PANEL.repaint();
    }

    /**
     * 添加结果行
     *
     * @param panel 面板
     * @param labelText 标签文本
     * @param jsonText JSON文本
     * @param gbc GridBagConstraints
     * @param row 行号
     * @return 下一行号
     */
    private int addResultRow(JPanel panel, String labelText, String jsonText, GridBagConstraints gbc, int row) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(labelText + "：");
        panel.add(label, gbc);

        // 文本框和复制按钮的容器
        // 使用BorderLayout，文本框在中间，复制按钮紧贴右侧
        JPanel textAndButtonPanel = new JPanel(new BorderLayout(3, 0));
        JTextArea textArea = new JTextArea(2, 40);
        textArea.setText(jsonText);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        // 设置最小和最大高度，减少留白
        textArea.setRows(2);
        textArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JBScrollPane scrollPane = new JBScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(0, 50));
        scrollPane.setMinimumSize(new Dimension(0, 40));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        textAndButtonPanel.add(scrollPane, BorderLayout.CENTER);

        // 复制按钮（小按钮，紧贴文本框）
        JButton copyButton = new JButton("复制");
        copyButton.setPreferredSize(new Dimension(45, 30));
        copyButton.setMargin(new Insets(1, 3, 1, 3));
        copyButton.addActionListener(e -> copyToClipboard(jsonText));
        textAndButtonPanel.add(copyButton, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(textAndButtonPanel, gbc);

        return row + 1;
    }

    /**
     * 复制到剪贴板
     *
     * @param text 要复制的文本
     */
    private void copyToClipboard(String text) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);
            Messages.showInfoMessage(project, "已复制到剪贴板", "复制成功");
        } catch (Exception e) {
            Messages.showErrorDialog(project, "复制失败: " + e.getMessage(), "复制错误");
        }
    }

    /**
     * 显示错误信息
     *
     * @param errorMessage 错误信息
     */
    private void showError(String errorMessage) {
        ERROR_TEXT_AREA.setText(errorMessage);
        ERROR_PANEL.setVisible(true);
        MAIN_PANEL.revalidate();
        MAIN_PANEL.repaint();
    }

    /**
     * 隐藏错误信息
     */
    private void hideError() {
        ERROR_PANEL.setVisible(false);
        ERROR_TEXT_AREA.setText("");
        MAIN_PANEL.revalidate();
        MAIN_PANEL.repaint();
    }

    /**
     * 转换结果内部类
     */
    private static class ConversionResult {
        private final String platformPrivilegeJson;
        private final String sellerPrivilegeJson;
        private final String shopPrivilegeJson;
        private final List<String> errors;

        public ConversionResult(String platformPrivilegeJson, String sellerPrivilegeJson,
                                String shopPrivilegeJson, List<String> errors) {
            this.platformPrivilegeJson = platformPrivilegeJson;
            this.sellerPrivilegeJson = sellerPrivilegeJson;
            this.shopPrivilegeJson = shopPrivilegeJson;
            this.errors = errors;
        }

        public String getPlatformPrivilegeJson() {
            return platformPrivilegeJson;
        }

        public String getSellerPrivilegeJson() {
            return sellerPrivilegeJson;
        }

        public String getShopPrivilegeJson() {
            return shopPrivilegeJson;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}

