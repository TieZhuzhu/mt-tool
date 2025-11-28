package com.augustlee.mt.toolWindow.code.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.augustlee.mt.toolWindow.code.art.MavenVersionManager;
import com.augustlee.mt.toolWindow.code.art.cache.MavenVersionCacheManager;
import com.augustlee.mt.toolWindow.code.art.cache.MavenVersionCacheResult;
import com.augustlee.mt.toolWindow.code.art.enums.ComponentNameEnum;
import com.augustlee.mt.toolWindow.code.art.vo.MavenVersionVO;
import com.augustlee.mt.toolWindow.code.art.vo.MavenVersionDataVO;
import com.augustlee.mt.toolWindow.code.art.vo.MavenVersionItemVO;
import com.augustlee.mt.toolWindow.common.state.CookieInputState;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Maven组件版本搜索工具面板
 *
 * @see ApiMavenSearchPanel
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class ApiMavenSearchPanel {

    private final JPanel MAIN_PANEL = new JPanel();

    private final JLabel COOKIE_LABEL = new JLabel("Cookie：");
    private final JTextArea COOKIE_TEXT_AREA = new JTextArea(8, 30);

    private final JLabel COMPONENT_NAME_LABEL = new JLabel("Component Name：");
    private final JComboBox<String> COMPONENT_NAME_COMBO_BOX = new JComboBox<>();

    private final JLabel PAGE_NUM_LABEL = new JLabel("Page Num：");
    private final JTextField PAGE_NUM_TEXT_FIELD = new JTextField(10);
    private final JLabel PAGE_SIZE_LABEL = new JLabel("Page Size：");
    private final JTextField PAGE_SIZE_TEXT_FIELD = new JTextField(10);

    private final JButton SEARCH_BUTTON = new JButton("Search");

    private final JLabel INFO_LABEL = new JLabel("Total: 0 | Page: 0/0");
    private final JCheckBox IGNORE_SNAPSHOT_CHECKBOX = new JCheckBox("是否忽略SNAPSHOT", true);
    private final JBTable RESULT_TABLE = new JBTable();
    private final DefaultTableModel TABLE_MODEL = new DefaultTableModel();
    private final JTextArea ERROR_TEXT_AREA = new JTextArea(3, 50);
    private final JPanel ERROR_PANEL = new JPanel();
    private final JButton TOGGLE_VIEW_BUTTON = new JButton("显示原始信息");
    private final JTextArea RAW_RESPONSE_AREA = new JTextArea(10, 50);
    private final JScrollPane RAW_RESPONSE_SCROLL_PANE = new JBScrollPane(RAW_RESPONSE_AREA);
    private final JPanel RAW_RESPONSE_PANEL = new JPanel();

    private Project project;
    private CookieInputState cookieState;
    private final MavenVersionCacheManager cacheManager = new MavenVersionCacheManager();

    // 保存原始信息文本
    private String originalInfoText = "";

    // 用于恢复信息标签的定时器
    private Timer restoreTimer;

    // 保存原始响应JSON
    private String rawResponseJson = "";

    // 当前显示模式：true=可视化（表格），false=原始信息（JSON）
    private boolean isVisualizationMode = true;

    public ApiMavenSearchPanel(Project project, CookieInputState cookieState) {
        this.project = project;
        this.cookieState = cookieState;
        this.initLayout();
        this.initComponent();
    }

    public JPanel getMainJPanel() {
        return this.MAIN_PANEL;
    }

    private void initLayout() {
        // 创建主面板布局
        MAIN_PANEL.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：Cookie标签和多行文本框
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        MAIN_PANEL.add(COOKIE_LABEL, gbc);

        // Cookie文本区域（设置weighty为0）
        JBScrollPane cookieScrollPane = new JBScrollPane(COOKIE_TEXT_AREA);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.BOTH;
        MAIN_PANEL.add(cookieScrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第二行：Component Name标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        MAIN_PANEL.add(COMPONENT_NAME_LABEL, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        MAIN_PANEL.add(COMPONENT_NAME_COMBO_BOX, gbc);

        // 第三行：Page Num 和 Page Size
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        MAIN_PANEL.add(PAGE_NUM_LABEL, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pagePanel.add(PAGE_NUM_TEXT_FIELD);
        pagePanel.add(Box.createHorizontalStrut(10));
        pagePanel.add(PAGE_SIZE_LABEL);
        pagePanel.add(PAGE_SIZE_TEXT_FIELD);
        MAIN_PANEL.add(pagePanel, gbc);

        // 第四行：搜索按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        MAIN_PANEL.add(SEARCH_BUTTON, gbc);

        // 第五行：信息标签和复选框（同一行，左右分布）
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        INFO_LABEL.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        MAIN_PANEL.add(INFO_LABEL, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        MAIN_PANEL.add(IGNORE_SNAPSHOT_CHECKBOX, gbc);

        // 第六行：结果显示表格
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        setupTable();
        JScrollPane resultScrollPane = new JBScrollPane(RESULT_TABLE);
        // 设置表格的最小高度，避免压缩上方组件
        resultScrollPane.setMinimumSize(new Dimension(0, 200));
        MAIN_PANEL.add(resultScrollPane, gbc);

        // 第七行：错误信息区域（初始隐藏）
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        setupErrorPanel();
        ERROR_PANEL.setVisible(false);
        // 设置错误面板的最大高度，避免被拉伸
        ERROR_PANEL.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        MAIN_PANEL.add(ERROR_PANEL, gbc);

        // 第八行：原始响应信息区域（初始隐藏）
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        setupRawResponsePanel();
        RAW_RESPONSE_PANEL.setVisible(false);
        // 设置原始响应面板的最大高度
        RAW_RESPONSE_PANEL.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        MAIN_PANEL.add(RAW_RESPONSE_PANEL, gbc);
    }

    private void setupErrorPanel() {
        ERROR_PANEL.setLayout(new BorderLayout());
        ERROR_PANEL.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.RED, 1),
            "错误信息",
            0, 0,
            new Font(Font.SANS_SERIF, Font.BOLD, 11),
            Color.RED
        ));

        ERROR_TEXT_AREA.setEditable(false);
        ERROR_TEXT_AREA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        ERROR_TEXT_AREA.setForeground(Color.RED);
        // 浅红色背景
        ERROR_TEXT_AREA.setBackground(new Color(255, 240, 240));
        ERROR_TEXT_AREA.setLineWrap(true);
        ERROR_TEXT_AREA.setWrapStyleWord(true);

        JScrollPane errorScrollPane = new JBScrollPane(ERROR_TEXT_AREA);
        // 固定高度约80px，确保有足够空间显示错误信息
        errorScrollPane.setPreferredSize(new Dimension(0, 80));
        errorScrollPane.setMinimumSize(new Dimension(0, 60));
        errorScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        ERROR_PANEL.add(errorScrollPane, BorderLayout.CENTER);

        // 添加按钮面板（包含切换按钮和关闭按钮）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        // 添加切换按钮（初始隐藏）
        TOGGLE_VIEW_BUTTON.setVisible(false);
        TOGGLE_VIEW_BUTTON.addActionListener(e -> toggleViewMode());
        buttonPanel.add(TOGGLE_VIEW_BUTTON);

        // 添加关闭按钮
        JButton closeErrorButton = new JButton("×");
        closeErrorButton.setPreferredSize(new Dimension(25, 25));
        closeErrorButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        closeErrorButton.setForeground(Color.RED);
        closeErrorButton.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        closeErrorButton.addActionListener(e -> hideError());
        buttonPanel.add(closeErrorButton);

        ERROR_PANEL.add(buttonPanel, BorderLayout.EAST);
    }

    private void setupRawResponsePanel() {
        RAW_RESPONSE_PANEL.setLayout(new BorderLayout());
        RAW_RESPONSE_PANEL.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE, 1),
            "原始响应信息",
            0, 0,
            new Font(Font.SANS_SERIF, Font.BOLD, 11),
            Color.BLUE
        ));

        RAW_RESPONSE_AREA.setEditable(false);
        RAW_RESPONSE_AREA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        RAW_RESPONSE_AREA.setLineWrap(false);
        RAW_RESPONSE_AREA.setWrapStyleWord(false);

        RAW_RESPONSE_SCROLL_PANE.setPreferredSize(new Dimension(0, 250));
        RAW_RESPONSE_SCROLL_PANE.setMinimumSize(new Dimension(0, 200));
        RAW_RESPONSE_SCROLL_PANE.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        RAW_RESPONSE_PANEL.add(RAW_RESPONSE_SCROLL_PANE, BorderLayout.CENTER);
    }

    private void toggleViewMode() {
        isVisualizationMode = !isVisualizationMode;

        if (isVisualizationMode) {
            // 切换到可视化模式（表格）
            TOGGLE_VIEW_BUTTON.setText("显示原始信息");
            RAW_RESPONSE_PANEL.setVisible(false);
            RESULT_TABLE.setVisible(true);
        } else {
            // 切换到原始信息模式（JSON）
            TOGGLE_VIEW_BUTTON.setText("可视化信息");
            // 格式化 JSON（如果可能）
            String formattedJson = formatJson(rawResponseJson);
            RAW_RESPONSE_AREA.setText(formattedJson);
            RAW_RESPONSE_PANEL.setVisible(true);
            RESULT_TABLE.setVisible(false);
        }

        MAIN_PANEL.revalidate();
        MAIN_PANEL.repaint();
    }

    /**
     * 格式化 JSON 字符串，使其更易读
     */
    private String formatJson(String json) {
        if (json == null || json.isEmpty()) {
            return "";
        }

        // 如果是 HTML，直接返回
        if (json.trim().startsWith("<html") || json.trim().startsWith("<!DOCTYPE")) {
            return json;
        }

        try {
            // 尝试使用 fastjson 格式化
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(json);
            return com.alibaba.fastjson.JSON.toJSONString(jsonObject, true);
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
            return json;
        }
    }

    private void showError(String errorMessage) {
        ERROR_TEXT_AREA.setText(errorMessage);
        ERROR_PANEL.setVisible(true);
        // 显示切换按钮（如果有原始响应数据）
        if (rawResponseJson != null && !rawResponseJson.isEmpty()) {
            TOGGLE_VIEW_BUTTON.setVisible(true);
        }
        MAIN_PANEL.revalidate();
        MAIN_PANEL.repaint();
    }

    private void hideError() {
        ERROR_PANEL.setVisible(false);
        ERROR_TEXT_AREA.setText("");
        // 隐藏切换按钮和原始响应面板
        TOGGLE_VIEW_BUTTON.setVisible(false);
        RAW_RESPONSE_PANEL.setVisible(false);
        // 恢复可视化模式
        isVisualizationMode = true;
        TOGGLE_VIEW_BUTTON.setText("显示原始信息");
        RESULT_TABLE.setVisible(true);
        MAIN_PANEL.revalidate();
        MAIN_PANEL.repaint();
    }

    private void initComponent() {
        this.COOKIE_TEXT_AREA.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String content = COOKIE_TEXT_AREA.getText();
                if (cookieState != null) {
                    cookieState.setCookieContent(content);
                }
            }
        });
        this.COOKIE_TEXT_AREA.setLineWrap(true);
        if (cookieState != null) {
            String savedCookie = cookieState.getCookieContent();
            this.COOKIE_TEXT_AREA.setText(savedCookie);
        }

        // 初始化组件名称下拉框
        setupComponentNameComboBox();

        // 设置默认值
        this.PAGE_NUM_TEXT_FIELD.setText("1");
        this.PAGE_SIZE_TEXT_FIELD.setText("20");
        // 设置默认选中的组件（seashop-user-api）
        COMPONENT_NAME_COMBO_BOX.setSelectedItem(ComponentNameEnum.SEASHOP_USER_API.getFullName());

        this.SEARCH_BUTTON.addActionListener(this::searchMavenVersion);
    }

    /**
     * 初始化组件名称下拉框
     */
    private void setupComponentNameComboBox() {
        // 设置为可编辑，允许用户自定义输入
        COMPONENT_NAME_COMBO_BOX.setEditable(true);
        
        // 添加枚举中的所有组件名称（使用完整名称）
        for (ComponentNameEnum component : ComponentNameEnum.values()) {
            COMPONENT_NAME_COMBO_BOX.addItem(component.getFullName());
        }
        
        // 设置渲染器，显示更友好的名称
        COMPONENT_NAME_COMBO_BOX.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    String fullName = value.toString();
                    // 尝试从枚举中查找对应的显示名称
                    ComponentNameEnum component = ComponentNameEnum.findByFullName(fullName);
                    if (component != null) {
                        ((JLabel) renderer).setText(component.getDisplayName() + " (" + fullName + ")");
                    } else {
                        // 如果是自定义输入，直接显示
                        ((JLabel) renderer).setText(fullName);
                    }
                }
                return renderer;
            }
        });
    }

    private void setupTable() {
        // 设置表格列
        String[] columnNames = {"Version", "GroupId", "ArtifactId", "Publish Time", "Author", "Repositories", "Used Num"};
        TABLE_MODEL.setColumnIdentifiers(columnNames);
        RESULT_TABLE.setModel(TABLE_MODEL);

        // 设置表格属性
        RESULT_TABLE.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        RESULT_TABLE.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        RESULT_TABLE.setRowHeight(25);
        RESULT_TABLE.setShowGrid(true);
        RESULT_TABLE.setGridColor(JBColor.LIGHT_GRAY);

        // 设置列宽
        // Version
        RESULT_TABLE.getColumnModel().getColumn(0).setPreferredWidth(150);
        // GroupId
        RESULT_TABLE.getColumnModel().getColumn(1).setPreferredWidth(200);
        // ArtifactId
        RESULT_TABLE.getColumnModel().getColumn(2).setPreferredWidth(200);
        // Publish Time
        RESULT_TABLE.getColumnModel().getColumn(3).setPreferredWidth(150);
        // Author
        RESULT_TABLE.getColumnModel().getColumn(4).setPreferredWidth(120);
        // Repositories
        RESULT_TABLE.getColumnModel().getColumn(5).setPreferredWidth(150);
        // Used Num
        RESULT_TABLE.getColumnModel().getColumn(6).setPreferredWidth(80);

        // 设置表格不可编辑
        RESULT_TABLE.setDefaultEditor(Object.class, null);

        // 添加行选择监听器，点击行时复制版本号到剪贴板
        RESULT_TABLE.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = RESULT_TABLE.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < TABLE_MODEL.getRowCount()) {
                        // 获取版本号（第一列）
                        Object versionObj = TABLE_MODEL.getValueAt(selectedRow, 0);
                        if (versionObj != null) {
                            String version = versionObj.toString();

                            // 根据复选框状态决定是否去掉-SNAPSHOT后缀
                            if (IGNORE_SNAPSHOT_CHECKBOX.isSelected() && version.endsWith("-SNAPSHOT")) {
                                version = version.substring(0, version.length() - "-SNAPSHOT".length());
                            }

                            // 复制到剪贴板并显示提示
                            copyToClipboard(version, selectedRow);
                        }
                    }
                }
            }
        });
    }

    private void copyToClipboard(String text, int selectedRow) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);

            // 显示复制成功的提示
            showCopySuccessMessage(text);
        } catch (Exception e) {
            // 静默处理，避免影响用户体验
            e.printStackTrace();
        }
    }

    private void showCopySuccessMessage(String copiedText) {
        // 在 EDT 线程中更新UI
        SwingUtilities.invokeLater(() -> {
            // 取消之前的定时器（如果存在）
            if (restoreTimer != null) {
                restoreTimer.cancel();
            }

            // 保存当前信息文本
            originalInfoText = INFO_LABEL.getText();

            // 显示复制成功消息（使用绿色高亮）
            String message = "✓ 已复制到剪贴板: " + copiedText;
            INFO_LABEL.setText(message);
            // 绿色
            INFO_LABEL.setForeground(JBColor.GREEN);

            // 2秒后恢复原始信息
            restoreTimer = new Timer();
            restoreTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        INFO_LABEL.setText(originalInfoText);
                        // 恢复默认颜色
                        INFO_LABEL.setForeground(null);
                    });
                }
            }, 2000);
        });
    }

    /**
     * 从缓存加载并显示数据
     *
     * @return 是否成功从缓存加载
     */
    private boolean loadFromCacheAndDisplay(String componentName, int pageNum, int pageSize) {
        MavenVersionCacheResult cacheResult = cacheManager.getCache(componentName, pageNum, pageSize);
        if (cacheResult != null && cacheResult.getData() != null) {
            String cacheTime = MavenVersionCacheManager.formatCacheTime(cacheResult.getTimestamp());
            showCacheMessage(cacheTime);
            displayResult(cacheResult.getData(), true);
            return true;
        }
        return false;
    }

    /**
     * 显示查询结果
     *
     * @param result 查询结果
     * @param fromCache 是否来自缓存
     */
    private void displayResult(MavenVersionVO result, boolean fromCache) {
        if (result == null || result.getData() == null) {
            INFO_LABEL.setText("No versions found");
            TABLE_MODEL.setRowCount(0);
            return;
        }

        MavenVersionDataVO data = result.getData();
        // 更新信息标签
        updateInfoLabel(data);
        // 重置UI状态
        resetUIState();

        // 填充表格数据
        List<MavenVersionItemVO> items = data.getItems();
        if (items != null && !items.isEmpty()) {
            fillTableData(items);
        } else {
            // 如果 items 为空，清空表格并显示提示
            TABLE_MODEL.setRowCount(0);
            INFO_LABEL.setText("No versions found");
        }
    }

    /**
     * 更新信息标签
     */
    private void updateInfoLabel(MavenVersionDataVO data) {
        int total = data.getTn() != null ? data.getTn() : 0;
        int currentPage = data.getPn() != null ? data.getPn() : 1;
        int pageSizeFromData = data.getSn() != null ? data.getSn() : 20;
        int totalPages = (total + pageSizeFromData - 1) / pageSizeFromData;

        String infoText = String.format("Total: %d | Page: %d/%d | Page Size: %d",
                total, currentPage, totalPages, pageSizeFromData);
        INFO_LABEL.setText(infoText);
        originalInfoText = infoText;
        INFO_LABEL.setForeground(null);
    }

    /**
     * 重置UI状态
     */
    private void resetUIState() {
        hideError();
        isVisualizationMode = true;
        RESULT_TABLE.setVisible(true);
    }

    /**
     * 填充表格数据
     */
    private void fillTableData(List<MavenVersionItemVO> items) {
        for (MavenVersionItemVO item : items) {
            Object[] rowData = {
                    item.getVersion() != null ? item.getVersion() : "",
                    item.getGroupId() != null ? item.getGroupId() : "",
                    item.getArtifactId() != null ? item.getArtifactId() : "",
                    item.getPublishTime() != null ? item.getPublishTime() : "",
                    item.getAuthor() != null ? item.getAuthor() : "",
                    item.getRepositories() != null ? item.getRepositories() : "",
                    item.getUsedNum() != null ? item.getUsedNum() : 0
            };
            TABLE_MODEL.addRow(rowData);
        }
    }

    /**
     * 显示缓存加载提示
     */
    private void showCacheMessage(String cacheTime) {
        // 在 EDT 线程中更新UI
        SwingUtilities.invokeLater(() -> {
            // 取消之前的定时器（如果存在）
            if (restoreTimer != null) {
                restoreTimer.cancel();
            }
            
            // 保存当前信息文本
            String currentText = INFO_LABEL.getText();
            if (originalInfoText == null || originalInfoText.isEmpty()) {
                originalInfoText = currentText;
            }
            
            // 显示缓存加载消息（使用绿色高亮）
            String message = "✓ 已从缓存加载，缓存时间: " + cacheTime;
            INFO_LABEL.setText(message);
            // 绿色
            INFO_LABEL.setForeground(JBColor.GREEN);

            // 3秒后恢复原始信息
            restoreTimer = new Timer();
            restoreTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        INFO_LABEL.setText(originalInfoText);
                        // 恢复默认颜色
                        INFO_LABEL.setForeground(null);
                    });
                }
            }, 3000);
        });
    }

    private void searchMavenVersion(ActionEvent actionEvent) {
        // 从下拉框获取选中的值（可能是枚举值或自定义输入）
        Object selectedItem = COMPONENT_NAME_COMBO_BOX.getSelectedItem();
        String componentName = selectedItem != null ? selectedItem.toString().trim() : "";
        String pageNumStr = this.PAGE_NUM_TEXT_FIELD.getText().trim();
        String pageSizeStr = this.PAGE_SIZE_TEXT_FIELD.getText().trim();

        if (componentName.isEmpty()) {
            Messages.showErrorDialog(project, "Component Name cannot be empty", "Error");
            return;
        }

        int pageNum = 1;
        int pageSize = 20;

        try {
            if (!pageNumStr.isEmpty()) {
                pageNum = Integer.parseInt(pageNumStr);
            }
            if (!pageSizeStr.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        } catch (NumberFormatException e) {
            Messages.showErrorDialog(project, "Page Num and Page Size must be valid numbers", "Error");
            return;
        }

        try {
            this.SEARCH_BUTTON.setEnabled(false);
            INFO_LABEL.setText("Searching...");
            // 恢复默认颜色
            INFO_LABEL.setForeground(null);
            TABLE_MODEL.setRowCount(0);
            // 隐藏之前的错误信息
            hideError();
            // 清空原始响应
            rawResponseJson = "";

            // 从文本框获取 Cookie，去除换行符和多余空格
            String cookie = COOKIE_TEXT_AREA.getText().trim().replaceAll("\\s+", " ");
            
            MavenVersionManager manager = new MavenVersionManager(pageNum, pageSize, componentName, cookie);
            // 先获取原始响应并保存
            try {
                rawResponseJson = manager.getRawResponse();
            } catch (Exception e) {
                rawResponseJson = "";
            }

            // 然后执行并解析响应
            MavenVersionVO result = manager.execute();

            if (result == null || result.getData() == null) {
                // 如果查询返回空数据，尝试从缓存加载
                if (!loadFromCacheAndDisplay(componentName, pageNum, pageSize)) {
                    INFO_LABEL.setText("No data returned");
                    // 隐藏错误信息区域
                    hideError();
                    return;
                }
                return;
            }

            // 检查 items 是否为空
            MavenVersionDataVO data = result.getData();
            List<MavenVersionItemVO> items = data.getItems();
            if (items == null || items.isEmpty()) {
                // 如果 items 为空，尝试从缓存加载
                if (!loadFromCacheAndDisplay(componentName, pageNum, pageSize)) {
                    INFO_LABEL.setText("No versions found");
                    // 隐藏错误信息区域
                    hideError();
                    TABLE_MODEL.setRowCount(0);
                    return;
                }
                return;
            }

            // 保存到缓存
            cacheManager.saveCache(componentName, pageNum, pageSize, result);

            // 显示查询结果
            displayResult(result, false);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getClass().getSimpleName() + ": " + e.toString();
            }

            // 尝试从缓存加载
            MavenVersionCacheResult cacheResult = cacheManager.getCache(componentName, pageNum, pageSize);
            if (cacheResult != null && cacheResult.getData() != null) {
                // 从缓存加载成功
                MavenVersionVO cachedResult = cacheResult.getData();
                String cacheTime = MavenVersionCacheManager.formatCacheTime(cacheResult.getTimestamp());
                
                // 显示缓存提示
                showCacheMessage(cacheTime);
                
                // 填充缓存数据
                displayResult(cachedResult, true);
                
                // 显示错误信息（但不阻止使用缓存数据）
                String fullErrorMessage = "搜索失败（已从缓存加载）: " + errorMessage;
                if (e.getCause() != null) {
                    fullErrorMessage += "\n原因: " + e.getCause().getMessage();
                }
                showError(fullErrorMessage);
            } else {
                // 没有缓存，显示错误
                String shortMessage = errorMessage.length() > 100
                    ? errorMessage.substring(0, 100) + "..."
                    : errorMessage;
                Messages.showErrorDialog(project, shortMessage, "Search Failed");

                String fullErrorMessage = "错误: " + errorMessage;
                if (e.getCause() != null) {
                    fullErrorMessage += "\n原因: " + e.getCause().getMessage();
                }
                showError(fullErrorMessage);

                // 保持信息标签显示搜索状态或清空表格信息
                INFO_LABEL.setText("搜索失败，请查看错误信息");
                INFO_LABEL.setForeground(Color.RED);
                originalInfoText = "";
                TABLE_MODEL.setRowCount(0);
            }
            e.printStackTrace();
        } finally {
            this.SEARCH_BUTTON.setEnabled(true);
        }
    }
}

