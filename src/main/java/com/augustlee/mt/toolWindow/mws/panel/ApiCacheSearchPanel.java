package com.augustlee.mt.toolWindow.mws.panel;

import com.alibaba.fastjson.JSON;
import com.augustlee.mt.toolWindow.common.dialog.CookieHelperDialog;
import com.augustlee.mt.toolWindow.common.log.ConsoleLogger;
import com.augustlee.mt.toolWindow.common.state.ApiPathState;
import com.augustlee.mt.toolWindow.common.state.CookieInputState;
import com.augustlee.mt.toolWindow.mws.dto.ApiIndexDTO;
import com.augustlee.mt.toolWindow.mws.dto.ApiMethodQueryDTO;
import com.augustlee.mt.toolWindow.mws.dto.ApiMethodSearchResultDTO;
import com.augustlee.mt.toolWindow.mws.dto.ClassIndexDTO;
import com.augustlee.mt.toolWindow.mws.service.ApiMethodCallTraceService;
import com.augustlee.mt.toolWindow.mws.service.SearchCacheManager;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * API 缓存搜索面板
 *
 * @see ApiCacheSearchPanel
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class ApiCacheSearchPanel {

    private static final ConsoleLogger LOG = ConsoleLogger.getInstance(ApiCacheSearchPanel.class);

    private static final String CACHE_SIZE = "缓存数量：";

    private static final int DEFAULT_METHOD_TRACE_DEPTH = 5;

    private static final int MAX_METHOD_TRACE_DEPTH = 8;

    private static final int METHOD_RESULT_PATH_COLUMN_INDEX = 0;

    private static final int METHOD_RESULT_ENTRY_METHOD_COLUMN_INDEX = 1;

    private static final int METHOD_RESULT_HIT_TYPE_COLUMN_INDEX = 2;

    private static final int METHOD_RESULT_PATH_COLUMN_WIDTH = 260;

    private static final int METHOD_RESULT_ENTRY_METHOD_MIN_COLUMN_WIDTH = 320;

    private static final int METHOD_RESULT_HIT_TYPE_COLUMN_WIDTH = 120;

    private static final Color METHOD_RESULT_DIRECT_MATCH_COLOR = new JBColor(new Color(15, 110, 58), new Color(98, 180, 122));

    private static final Color METHOD_RESULT_TRACE_MATCH_COLOR = new JBColor(new Color(177, 92, 0), new Color(255, 183, 77));

    private static final Color METHOD_RESULT_EVEN_ROW_COLOR = new JBColor(new Color(255, 255, 255), new Color(60, 63, 65));

    private static final Color METHOD_RESULT_ODD_ROW_COLOR = new JBColor(new Color(247, 249, 252), new Color(65, 69, 71));

    private final JPanel MAIN_PANEL = new JPanel();

    private final JLabel CACHE_SIZE_LABEL = new JLabel(CACHE_SIZE + "0");
    private final JButton REFRESH_BUTTON = new JButton("刷新缓存");
    private final JTextArea COOKIE_TEXT_AREA = new JTextArea(8, 30);

    private final JLabel API_LABEL = new JLabel("API：");
    private final JTextField API_TEXT_FIELD = new JTextField(30);

    private final JButton SEARCH_BUTTON = new JButton("跳转方法");
    private final JButton GET_COOKIE_BUTTON = new JButton("自动获取Cookie");

    /**
     * 方法反查相关组件。
     */
    private final JLabel METHOD_LABEL = new JLabel("方法：");
    private final JTextField METHOD_TEXT_FIELD = new JTextField(30);
    private final JLabel METHOD_TRACE_DEPTH_LABEL = new JLabel("上钻层级：");
    private final JSpinner METHOD_TRACE_DEPTH_SPINNER = new JSpinner(new SpinnerNumberModel(DEFAULT_METHOD_TRACE_DEPTH, 0, MAX_METHOD_TRACE_DEPTH, 1));
    private final JButton METHOD_SEARCH_BUTTON = new JButton("查询接口地址");
    private final JLabel METHOD_RESULT_HINT_LABEL = new JLabel("结果：支持复制接口地址，双击或按钮可跳转入口方法");
    private final JTable METHOD_RESULT_TABLE = new JTable();
    private final JButton METHOD_COPY_BUTTON = new JButton("复制接口地址");
    private final JButton METHOD_GO_TO_METHOD_BUTTON = new JButton("跳转入口方法");

    private final SearchCacheManager SEARCH_CACHE_MANAGER;
    private final ApiMethodCallTraceService API_METHOD_CALL_TRACE_SERVICE;

    private Project project;
    private CookieInputState cookieState;
    private List<ApiMethodSearchResultDTO> methodSearchResultList = new ArrayList<>();

    public ApiCacheSearchPanel(Project project, CookieInputState cookieState, ApiPathState apiPathState){
        this.SEARCH_CACHE_MANAGER = new SearchCacheManager(apiPathState);
        this.API_METHOD_CALL_TRACE_SERVICE = new ApiMethodCallTraceService(project, this.SEARCH_CACHE_MANAGER);
        this.project = project;
        this.cookieState = cookieState;
        this.initLayout();
        this.initComponent();

        this.CACHE_SIZE_LABEL.setText(CACHE_SIZE + this.SEARCH_CACHE_MANAGER.getApiCount());
    }

    public JPanel getMainJPanel(){
        return this.MAIN_PANEL;
    }


    private void initLayout() {
        MAIN_PANEL.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：网关地址入口
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        MAIN_PANEL.add(this.buildGatewayLinkPanel(), gbc);
        gbc.gridwidth = 1;

        // 左侧容器
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.add(Box.createVerticalStrut(5));
        leftColumn.add(CACHE_SIZE_LABEL);
        leftColumn.add(Box.createVerticalStrut(5));
        leftColumn.add(GET_COOKIE_BUTTON);
        leftColumn.add(Box.createVerticalStrut(5));
        leftColumn.add(REFRESH_BUTTON);

        // 添加左侧容器（设置weighty为0防止垂直扩展）
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0;
        MAIN_PANEL.add(leftColumn, gbc);

        // Cookie文本区域（设置weighty为0）
        JBScrollPane cookieScrollPane = new JBScrollPane(COOKIE_TEXT_AREA);
        gbc.gridx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        MAIN_PANEL.add(cookieScrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // API标签和输入框（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        MAIN_PANEL.add(API_LABEL, gbc);

        gbc.gridx = 1;
        MAIN_PANEL.add(API_TEXT_FIELD, gbc);

        // 搜索按钮（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        MAIN_PANEL.add(SEARCH_BUTTON, gbc);
        gbc.gridwidth = 1;

        // 方法标签和输入框（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 0;
        MAIN_PANEL.add(METHOD_LABEL, gbc);

        gbc.gridx = 1;
        MAIN_PANEL.add(METHOD_TEXT_FIELD, gbc);

        // 上钻层级（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 0;
        MAIN_PANEL.add(METHOD_TRACE_DEPTH_LABEL, gbc);

        gbc.gridx = 1;
        JPanel methodTraceDepthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        methodTraceDepthPanel.setOpaque(false);
        methodTraceDepthPanel.add(METHOD_TRACE_DEPTH_SPINNER);
        MAIN_PANEL.add(methodTraceDepthPanel, gbc);

        // 方法反查按钮（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        MAIN_PANEL.add(METHOD_SEARCH_BUTTON, gbc);
        gbc.gridwidth = 1;

        // 结果提示
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        MAIN_PANEL.add(METHOD_RESULT_HINT_LABEL, gbc);
        gbc.gridwidth = 1;

        // 结果操作按钮
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        MAIN_PANEL.add(this.buildMethodResultActionPanel(), gbc);
        gbc.gridwidth = 1;

        // 结果列表
        JBScrollPane resultScrollPane = new JBScrollPane(METHOD_RESULT_TABLE);
        resultScrollPane.setPreferredSize(new Dimension(0, 220));
        resultScrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyMethodResultTableColumnWidth();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        MAIN_PANEL.add(resultScrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加底部占位符将内容推至顶部
        gbc.gridy = 10;
        gbc.weighty = 0;
        MAIN_PANEL.add(Box.createVerticalStrut(0), gbc);
    }

    /**
     * 构建网关地址入口面板
     *
     * @return 包含"网关地址："标签和可点击"点击查看"链接的面板
     */
    private JPanel buildGatewayLinkPanel() {
        final String GATEWAY_URL = "https://shepherd.mws-test.sankuai.com/api-group-manage";

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel gatewayLabel = new JLabel("网关地址：");
        JLabel gatewayLink = new JLabel("点击查看");
        gatewayLink.setForeground(new Color(0, 120, 215));
        gatewayLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gatewayLink.setToolTipText("点击查看网关");
        gatewayLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(GATEWAY_URL));
                } catch (Exception ex) {
                    Messages.showErrorDialog(project, "无法打开浏览器: " + ex.getMessage(), "跳转失败");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                gatewayLink.setText("<html><u>点击查看</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                gatewayLink.setText("点击查看");
            }
        });

        panel.add(gatewayLabel);
        panel.add(gatewayLink);
        return panel;
    }

    /**
     * 构建方法反查结果操作区域。
     *
     * @return 结果操作区域
     */
    private JPanel buildMethodResultActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.add(METHOD_COPY_BUTTON);
        panel.add(Box.createHorizontalStrut(8));
        panel.add(METHOD_GO_TO_METHOD_BUTTON);
        return panel;
    }

    private void initComponent(){

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
            this.COOKIE_TEXT_AREA.setText(cookieState.getCookieContent());
        }

        this.SEARCH_BUTTON.addActionListener(this::searchApi);
        this.REFRESH_BUTTON.addActionListener(this::refresh);
        this.METHOD_SEARCH_BUTTON.addActionListener(this::searchApiPathByMethod);
        JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(METHOD_TRACE_DEPTH_SPINNER, "0");
        METHOD_TRACE_DEPTH_SPINNER.setEditor(numberEditor);
        numberEditor.getTextField().setColumns(2);
        METHOD_TRACE_DEPTH_SPINNER.setPreferredSize(new Dimension(56, METHOD_TEXT_FIELD.getPreferredSize().height));
        this.initMethodResultTable();
        this.initMethodResultPopupMenu();
        this.METHOD_COPY_BUTTON.addActionListener(e -> copySelectedApiPath());
        this.METHOD_GO_TO_METHOD_BUTTON.addActionListener(e -> goToSelectedEntryMethod());
        this.METHOD_RESULT_TABLE.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2 && SwingUtilities.isLeftMouseButton(e)) {
                    goToSelectedEntryMethod();
                }
            }
        });
        this.GET_COOKIE_BUTTON.addActionListener(e -> {
            CookieHelperDialog dialog = new CookieHelperDialog(
                    project,
                    "https://shepherd.mws-test.sankuai.com/api-group-manage",
                    cookie -> {
                        COOKIE_TEXT_AREA.setText(cookie);
                        if (cookieState != null) {
                            cookieState.setCookieContent(cookie);
                        }
                    }
            );
            dialog.show();
        });
    }

    private void refresh(ActionEvent actionEvent) {
        LOG.info("=== 刷新缓存按钮被点击 ===");

        if (project == null) {
            LOG.error("project 为 null，无法执行刷新操作");
            Messages.showErrorDialog((Project) null, "Project 未初始化，无法刷新", "错误");
            return;
        }

        this.SEARCH_BUTTON.setEnabled(false);
        this.REFRESH_BUTTON.setEnabled(false);
        this.METHOD_SEARCH_BUTTON.setEnabled(false);
        this.METHOD_TRACE_DEPTH_SPINNER.setEnabled(false);

        LOG.info("创建后台任务，project: " + project.getName());
        long refreshStartTime = System.currentTimeMillis();
        new Task.Backgroundable(project, "刷新 API 缓存", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                LOG.info("=== 后台任务开始执行 ===");
                indicator.setIndeterminate(true);
                indicator.setText("正在刷新 API 缓存...");
                try {
                    LOG.info("调用 SEARCH_CACHE_MANAGER.refresh()...");
                    SEARCH_CACHE_MANAGER.refresh();
                    LOG.info("SEARCH_CACHE_MANAGER.refresh() 执行完成");

                    long refreshEndTime = System.currentTimeMillis();
                    long duration = refreshEndTime - refreshStartTime;
                    int apiCount = SEARCH_CACHE_MANAGER.getApiCount();

                    ApplicationManager.getApplication().invokeLater(() -> {
                        LOG.info("更新 UI，缓存大小: " + apiCount);
                        CACHE_SIZE_LABEL.setText(CACHE_SIZE + apiCount);
                        METHOD_RESULT_HINT_LABEL.setText("结果：支持复制接口地址，双击或按钮可跳转入口方法");
                        SEARCH_BUTTON.setEnabled(true);
                        REFRESH_BUTTON.setEnabled(true);
                        METHOD_SEARCH_BUTTON.setEnabled(true);
                        METHOD_TRACE_DEPTH_SPINNER.setEnabled(true);

                        // 显示成功提示
                        String message = String.format(
                            "API 缓存刷新完成！\n\n" +
                            "缓存 API 数量：%d 个\n" +
                            "耗时：%.2f 秒",
                            apiCount,
                            duration / 1000.0
                        );
                        Messages.showInfoMessage(project, message, "刷新成功");
                    });
                } catch (Exception e) {
                    LOG.error("刷新 API 缓存时发生错误", e);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog(project, "刷新失败：" + e.getMessage(), "刷新失败");
                        SEARCH_BUTTON.setEnabled(true);
                        REFRESH_BUTTON.setEnabled(true);
                        METHOD_SEARCH_BUTTON.setEnabled(true);
                        METHOD_TRACE_DEPTH_SPINNER.setEnabled(true);
                    });
                }
            }
        }.queue();
        LOG.info("后台任务已提交到队列");
    }

    private void searchApi(ActionEvent actionEvent) {
        String path = this.API_TEXT_FIELD.getText();
        try{
            // 输入验证
            if (path == null || path.trim().isEmpty()) {
                Messages.showErrorDialog(project, "请输入 API 路径", "错误");
                return;
            }
            path = path.trim();

            path = this.normalizeApiPath(path);
            this.API_TEXT_FIELD.setText(path);

            // 检查缓存是否为空
            int cacheSize = this.SEARCH_CACHE_MANAGER.getApiCount();
            if (cacheSize == 0) {
                Messages.showErrorDialog(project,
                    "API 缓存为空，请先点击 Refresh 按钮刷新缓存",
                    "缓存为空");
                return;
            }

            ClassIndexDTO classIndexDTO = this.SEARCH_CACHE_MANAGER.getClassIndex(path);
            if(classIndexDTO == null){
                Messages.showErrorDialog(project,
                    "未找到 API：" + path + "\n\n" +
                    "当前缓存中有 " + cacheSize + " 个 API。\n" +
                    "请确认路径是否正确，或点击 Refresh 按钮刷新缓存。",
                    "未找到结果");
                return;
            }
            LOG.debug("找到 API: " + JSON.toJSONString(classIndexDTO));
            goToCode(classIndexDTO.getServiceName(), classIndexDTO.getMethodName(), project);
        } catch (Exception e) {
            Messages.showErrorDialog(project, e.getMessage(), "查询失败");
            e.printStackTrace();
        }
    }

    /**
     * 规范化 API 路径。
     *
     * <p>当前缓存中的接口地址统一以“/”开头，因此当用户输入未带前导斜杠时，自动补齐。</p>
     *
     * @param path 用户输入的 API 路径
     * @return 规范化后的 API 路径
     */
    private String normalizeApiPath(String path) {
        if (path.startsWith("/")) {
            return path;
        }
        return "/" + path;
    }

    /**
     * 根据方法表达式反向查询接口地址。
     *
     * @param actionEvent 事件
     */
    private void searchApiPathByMethod(ActionEvent actionEvent) {
        String methodExpression = this.METHOD_TEXT_FIELD.getText();

        try {
            // 输入验证
            ApiMethodQueryDTO queryDTO = this.parseMethodExpression(methodExpression);
            int maxTraceDepth = this.getMethodTraceDepth();

            // 检查缓存是否为空
            int cacheSize = this.SEARCH_CACHE_MANAGER.getApiCount();
            if (cacheSize == 0) {
                clearMethodResult("结果：API 缓存为空，请先点击 Refresh 按钮刷新缓存");
                Messages.showErrorDialog(project,
                        "API 缓存为空，请先点击 Refresh 按钮刷新缓存",
                        "缓存为空");
                return;
            }

            if (project == null) {
                Messages.showErrorDialog((Project) null, "Project 未初始化，无法查询", "错误");
                return;
            }
            if (DumbService.isDumb(project)) {
                Messages.showErrorDialog(project, "IDE 正在更新索引，请稍后重试。", "索引中");
                return;
            }

            METHOD_RESULT_HINT_LABEL.setText("结果：正在查询，请稍候...");
            clearMethodResult("结果：正在查询，请稍候...");
            SEARCH_BUTTON.setEnabled(false);
            REFRESH_BUTTON.setEnabled(false);
            METHOD_SEARCH_BUTTON.setEnabled(false);
            METHOD_TRACE_DEPTH_SPINNER.setEnabled(false);

            new Task.Backgroundable(project, "根据方法查询接口地址", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setIndeterminate(true);
                    indicator.setText("正在上钻调用链并查询接口地址...");
                    try {
                        List<ApiMethodSearchResultDTO> resultList = ApplicationManager.getApplication().runReadAction(
                                (Computable<List<ApiMethodSearchResultDTO>>) () -> API_METHOD_CALL_TRACE_SERVICE.searchApiByMethod(queryDTO, maxTraceDepth)
                        );
                        ApplicationManager.getApplication().invokeLater(() -> {
                            updateMethodSearchResult(resultList);
                            SEARCH_BUTTON.setEnabled(true);
                            REFRESH_BUTTON.setEnabled(true);
                            METHOD_SEARCH_BUTTON.setEnabled(true);
                            METHOD_TRACE_DEPTH_SPINNER.setEnabled(true);
                        });
                    } catch (IllegalArgumentException e) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            clearMethodResult("结果：方法表达式不合法");
                            Messages.showErrorDialog(project, e.getMessage(), "方法格式错误");
                            SEARCH_BUTTON.setEnabled(true);
                            REFRESH_BUTTON.setEnabled(true);
                            METHOD_SEARCH_BUTTON.setEnabled(true);
                            METHOD_TRACE_DEPTH_SPINNER.setEnabled(true);
                        });
                    } catch (Exception e) {
                        LOG.error("根据方法反查接口地址失败", e);
                        ApplicationManager.getApplication().invokeLater(() -> {
                            clearMethodResult("结果：查询失败");
                            Messages.showErrorDialog(project, e.getMessage(), "查询失败");
                            SEARCH_BUTTON.setEnabled(true);
                            REFRESH_BUTTON.setEnabled(true);
                            METHOD_SEARCH_BUTTON.setEnabled(true);
                            METHOD_TRACE_DEPTH_SPINNER.setEnabled(true);
                        });
                    }
                }
            }.queue();
        } catch (IllegalArgumentException e) {
            clearMethodResult("结果：方法表达式不合法");
            Messages.showErrorDialog(project, e.getMessage(), "方法格式错误");
        }
    }

    /**
     * 解析方法表达式。
     *
     * @param expression 方法表达式
     * @return 解析后的方法查询参数
     */
    private ApiMethodQueryDTO parseMethodExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("请输入方法全限定名，例如：com.foo.Service#method");
        }

        String normalizedExpression = expression.trim();
        List<String> parameterTypeList = new ArrayList<>();
        boolean parameterSpecified = false;
        int bracketIndex = normalizedExpression.indexOf('(');
        if (bracketIndex >= 0) {
            int lastBracketIndex = normalizedExpression.lastIndexOf(')');
            String parameterExpression = lastBracketIndex > bracketIndex
                    ? normalizedExpression.substring(bracketIndex + 1, lastBracketIndex).trim()
                    : normalizedExpression.substring(bracketIndex + 1).trim();
            normalizedExpression = normalizedExpression.substring(0, bracketIndex).trim();
            parameterSpecified = true;
            parameterTypeList = this.parseParameterTypeList(parameterExpression);
        }

        String serviceName;
        String methodName;
        if (normalizedExpression.contains("#")) {
            String[] parts = normalizedExpression.split("#", 2);
            serviceName = parts.length > 0 ? parts[0].trim() : "";
            methodName = parts.length > 1 ? parts[1].trim() : "";
        } else {
            int lastDotIndex = normalizedExpression.lastIndexOf('.');
            if (lastDotIndex <= 0 || lastDotIndex == normalizedExpression.length() - 1) {
                throw new IllegalArgumentException("仅支持 类全限定名#方法名 或 类全限定名.方法名 格式");
            }
            serviceName = normalizedExpression.substring(0, lastDotIndex).trim();
            methodName = normalizedExpression.substring(lastDotIndex + 1).trim();
        }

        if (serviceName.isEmpty()) {
            throw new IllegalArgumentException("缺少类全限定名");
        }
        if (methodName.isEmpty()) {
            throw new IllegalArgumentException("缺少方法名");
        }
        if (!serviceName.contains(".")) {
            throw new IllegalArgumentException("请输入完整类名，例如：com.foo.Service#method");
        }
        return new ApiMethodQueryDTO(serviceName, methodName, parameterTypeList, parameterSpecified);
    }

    /**
     * 复制当前选中的接口地址。
     */
    private void copySelectedApiPath() {
        ApiMethodSearchResultDTO selectedResult = getSelectedMethodSearchResult();
        if (selectedResult == null || selectedResult.getPath() == null || selectedResult.getPath().trim().isEmpty()) {
            return;
        }
        String selectedPath = selectedResult.getPath().trim();
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(selectedPath), null);
        METHOD_RESULT_HINT_LABEL.setText("结果：已复制接口地址 " + selectedPath);
    }

    /**
     * 清空方法查询结果。
     *
     * @param hintText 提示文案
     */
    private void clearMethodResult(String hintText) {
        this.methodSearchResultList = new ArrayList<>();
        refreshMethodResultTable();
        METHOD_RESULT_HINT_LABEL.setText(hintText);
    }

    /**
     * 跳转到当前选中的入口方法。
     */
    private void goToSelectedEntryMethod() {
        ApiMethodSearchResultDTO selectedResult = getSelectedMethodSearchResult();
        if (selectedResult == null) {
            return;
        }
        goToCode(selectedResult.getEntryServiceName(), selectedResult.getEntryMethodName(), project);
    }

    /**
     * 获取方法上钻层级。
     *
     * @return 上钻层级
     */
    private int getMethodTraceDepth() {
        Object value = METHOD_TRACE_DEPTH_SPINNER.getValue();
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("上钻层级必须为数字");
        }
        int traceDepth = ((Number) value).intValue();
        if (traceDepth < 0 || traceDepth > MAX_METHOD_TRACE_DEPTH) {
            throw new IllegalArgumentException("上钻层级必须在 0 ~ " + MAX_METHOD_TRACE_DEPTH + " 之间");
        }
        return traceDepth;
    }

    /**
     * 更新方法反查结果。
     *
     * @param resultList 查询结果
     */
    private void updateMethodSearchResult(List<ApiMethodSearchResultDTO> resultList) {
        if (resultList == null || resultList.isEmpty()) {
            clearMethodResult("结果：未找到与该方法直接或间接关联的接口地址");
            Messages.showInfoMessage(project,
                    "未找到与该方法直接或间接关联的接口地址",
                    "未找到结果");
            return;
        }

        int directMatchCount = 0;
        int traceMatchCount = 0;
        this.methodSearchResultList = new ArrayList<>(resultList);
        for (ApiMethodSearchResultDTO resultDTO : this.methodSearchResultList) {
            if (resultDTO.isDirectMatch()) {
                directMatchCount++;
            } else {
                traceMatchCount++;
            }
        }
        refreshMethodResultTable();
        METHOD_RESULT_HINT_LABEL.setText("结果：共找到 "
                + this.methodSearchResultList.size()
                + " 个接口地址｜直接命中 "
                + directMatchCount
                + " 个｜上钻命中 "
                + traceMatchCount
                + " 个｜可复制接口地址，双击可跳转入口方法");
    }

    /**
     * 解析参数类型列表。
     *
     * @param parameterExpression 参数表达式
     * @return 参数类型列表
     */
    private List<String> parseParameterTypeList(String parameterExpression) {
        List<String> parameterTypeList = new ArrayList<>();
        if (parameterExpression == null || parameterExpression.trim().isEmpty()) {
            return parameterTypeList;
        }

        StringBuilder currentParameter = new StringBuilder();
        int genericDepth = 0;
        for (int i = 0; i < parameterExpression.length(); i++) {
            char currentChar = parameterExpression.charAt(i);
            if (currentChar == '<') {
                genericDepth++;
            } else if (currentChar == '>') {
                genericDepth = Math.max(0, genericDepth - 1);
            }

            if (currentChar == ',' && genericDepth == 0) {
                String parameterType = currentParameter.toString().trim();
                if (!parameterType.isEmpty()) {
                    parameterTypeList.add(parameterType);
                }
                currentParameter.setLength(0);
                continue;
            }
            currentParameter.append(currentChar);
        }

        String parameterType = currentParameter.toString().trim();
        if (!parameterType.isEmpty()) {
            parameterTypeList.add(parameterType);
        }
        return parameterTypeList;
    }

    /**
     * 初始化方法反查结果表格。
     */
    private void initMethodResultTable() {
        METHOD_RESULT_TABLE.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"接口地址", "入口方法", "命中方式"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        METHOD_RESULT_TABLE.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        METHOD_RESULT_TABLE.setRowHeight(30);
        METHOD_RESULT_TABLE.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        METHOD_RESULT_TABLE.setFillsViewportHeight(true);
        METHOD_RESULT_TABLE.setShowVerticalLines(false);
        METHOD_RESULT_TABLE.setShowHorizontalLines(true);
        METHOD_RESULT_TABLE.setGridColor(new JBColor(new Color(229, 233, 239), new Color(82, 82, 82)));
        METHOD_RESULT_TABLE.setIntercellSpacing(new Dimension(0, 1));
        METHOD_RESULT_TABLE.setSelectionBackground(new JBColor(new Color(220, 235, 252), new Color(70, 96, 125)));
        METHOD_RESULT_TABLE.setSelectionForeground(new JBColor(new Color(24, 24, 24), new Color(242, 242, 242)));
        METHOD_RESULT_TABLE.getTableHeader().setReorderingAllowed(false);
        METHOD_RESULT_TABLE.getTableHeader().setResizingAllowed(true);
        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_PATH_COLUMN_INDEX).setResizable(false);
        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_HIT_TYPE_COLUMN_INDEX).setResizable(false);
        applyMethodResultTableColumnWidth();
        METHOD_RESULT_TABLE.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyMethodResultTableColumnWidth();
            }
        });
        METHOD_RESULT_TABLE.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!(component instanceof JLabel label)) {
                    return component;
                }

                ApiMethodSearchResultDTO resultDTO = getMethodSearchResultByViewRow(row);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setFont(table.getFont());

                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? METHOD_RESULT_EVEN_ROW_COLOR : METHOD_RESULT_ODD_ROW_COLOR);
                    label.setForeground(table.getForeground());
                }

                if (resultDTO == null) {
                    label.setToolTipText(value == null ? null : value.toString());
                    return component;
                }

                if (column == METHOD_RESULT_PATH_COLUMN_INDEX) {
                    label.setText(resultDTO.getPath());
                    label.setToolTipText(resultDTO.getPath());
                    return component;
                }
                if (column == METHOD_RESULT_ENTRY_METHOD_COLUMN_INDEX) {
                    label.setText(resultDTO.getEntryMethodShortDisplayText());
                    label.setToolTipText(resultDTO.getEntryMethodDisplayText());
                    return component;
                }
                if (column == METHOD_RESULT_HIT_TYPE_COLUMN_INDEX) {
                    label.setText(resultDTO.getHitTypeDisplayText());
                    label.setToolTipText(resultDTO.getHitTypeDisplayText());
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    if (!isSelected) {
                        label.setForeground(resultDTO.isDirectMatch() ? METHOD_RESULT_DIRECT_MATCH_COLOR : METHOD_RESULT_TRACE_MATCH_COLOR);
                    }
                    return component;
                }

                label.setToolTipText(value == null ? null : value.toString());
                return component;
            }
        });
    }

    /**
     * 刷新方法反查结果表格。
     */
    private void refreshMethodResultTable() {
        Vector<String> columnNameVector = new Vector<>();
        columnNameVector.add("接口地址");
        columnNameVector.add("入口方法");
        columnNameVector.add("命中方式");

        Vector<Vector<Object>> dataVector = new Vector<>();
        for (ApiMethodSearchResultDTO resultDTO : this.methodSearchResultList) {
            Vector<Object> rowVector = new Vector<>();
            rowVector.add(resultDTO.getPath());
            rowVector.add(resultDTO.getEntryMethodShortDisplayText());
            rowVector.add(resultDTO.getHitTypeDisplayText());
            dataVector.add(rowVector);
        }

        DefaultTableModel tableModel = (DefaultTableModel) METHOD_RESULT_TABLE.getModel();
        tableModel.setDataVector(dataVector, columnNameVector);
        applyMethodResultTableColumnWidth();
        if (!this.methodSearchResultList.isEmpty()) {
            METHOD_RESULT_TABLE.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * 初始化方法反查结果右键菜单。
     */
    private void initMethodResultPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyApiPathMenuItem = new JMenuItem("复制接口地址");
        JMenuItem goToMethodMenuItem = new JMenuItem("跳转入口方法");
        copyApiPathMenuItem.addActionListener(e -> copySelectedApiPath());
        goToMethodMenuItem.addActionListener(e -> goToSelectedEntryMethod());
        popupMenu.add(copyApiPathMenuItem);
        popupMenu.add(goToMethodMenuItem);

        METHOD_RESULT_TABLE.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenuIfNecessary(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupMenuIfNecessary(e);
            }

            private void showPopupMenuIfNecessary(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    return;
                }
                int clickedRow = METHOD_RESULT_TABLE.rowAtPoint(e.getPoint());
                if (clickedRow >= 0) {
                    METHOD_RESULT_TABLE.setRowSelectionInterval(clickedRow, clickedRow);
                }
                boolean hasSelection = getSelectedMethodSearchResult() != null;
                copyApiPathMenuItem.setEnabled(hasSelection);
                goToMethodMenuItem.setEnabled(hasSelection);
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }

    /**
     * 应用方法反查结果表格列宽。
     */
    private void applyMethodResultTableColumnWidth() {
        int viewportWidth = METHOD_RESULT_TABLE.getParent() == null
                ? METHOD_RESULT_TABLE.getWidth()
                : METHOD_RESULT_TABLE.getParent().getWidth();
        int entryMethodColumnWidth = Math.max(
                METHOD_RESULT_ENTRY_METHOD_MIN_COLUMN_WIDTH,
                viewportWidth - METHOD_RESULT_PATH_COLUMN_WIDTH - METHOD_RESULT_HIT_TYPE_COLUMN_WIDTH
        );

        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_PATH_COLUMN_INDEX).setMinWidth(METHOD_RESULT_PATH_COLUMN_WIDTH);
        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_PATH_COLUMN_INDEX).setMaxWidth(METHOD_RESULT_PATH_COLUMN_WIDTH);
        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_PATH_COLUMN_INDEX).setPreferredWidth(METHOD_RESULT_PATH_COLUMN_WIDTH);

        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_ENTRY_METHOD_COLUMN_INDEX).setMinWidth(METHOD_RESULT_ENTRY_METHOD_MIN_COLUMN_WIDTH);
        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_ENTRY_METHOD_COLUMN_INDEX).setPreferredWidth(entryMethodColumnWidth);

        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_HIT_TYPE_COLUMN_INDEX).setMinWidth(METHOD_RESULT_HIT_TYPE_COLUMN_WIDTH);
        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_HIT_TYPE_COLUMN_INDEX).setMaxWidth(METHOD_RESULT_HIT_TYPE_COLUMN_WIDTH);
        METHOD_RESULT_TABLE.getColumnModel().getColumn(METHOD_RESULT_HIT_TYPE_COLUMN_INDEX).setPreferredWidth(METHOD_RESULT_HIT_TYPE_COLUMN_WIDTH);
    }

    /**
     * 根据表格视图行获取方法反查结果。
     *
     * @param viewRow 表格视图行号
     * @return 方法反查结果
     */
    private ApiMethodSearchResultDTO getMethodSearchResultByViewRow(int viewRow) {
        if (viewRow < 0) {
            return null;
        }
        int modelRow = METHOD_RESULT_TABLE.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= this.methodSearchResultList.size()) {
            return null;
        }
        return this.methodSearchResultList.get(modelRow);
    }

    /**
     * 获取当前选中的方法反查结果。
     *
     * @return 当前选中的方法反查结果
     */
    private ApiMethodSearchResultDTO getSelectedMethodSearchResult() {
        int selectedRow = METHOD_RESULT_TABLE.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return getMethodSearchResultByViewRow(selectedRow);
    }

    private void goToCode(String serviceName, String methodName, Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (DumbService.isDumb(project)) {
                Messages.showErrorDialog(project, "IDE 正在更新索引，请稍后重试。", "错误");
                return;
            }

            ApplicationManager.getApplication().runReadAction(() -> {
                // 1. 查找类
                PsiClass targetClass = JavaPsiFacade.getInstance(project)
                        .findClass(serviceName, GlobalSearchScope.allScope(project));

                if (targetClass == null) {
                    Messages.showErrorDialog(project, "未找到类：" + serviceName, "错误");
                    return;
                }

                // 2. 查找方法
                PsiMethod targetMethod = null;
                for (PsiMethod method : targetClass.getAllMethods()) {
                    if (methodName.equals(method.getName())) {
                        targetMethod = method;
                        break;
                    }
                }

                if (targetMethod == null) {
                    Messages.showErrorDialog(project, "未找到方法：" + methodName, "错误");
                    return;
                }

                // 3. 导航到方法
                NavigationUtil.activateFileWithPsiElement(targetMethod, true);
            });
        });
    }

}
