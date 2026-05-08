package com.augustlee.mt.toolWindow.mws.panel;

import com.alibaba.fastjson.JSON;
import com.augustlee.mt.toolWindow.common.dialog.CookieHelperDialog;
import com.augustlee.mt.toolWindow.common.log.ConsoleLogger;
import com.augustlee.mt.toolWindow.common.state.ApiPathState;
import com.augustlee.mt.toolWindow.common.state.CookieInputState;
import com.augustlee.mt.toolWindow.mws.dto.ApiIndexDTO;
import com.augustlee.mt.toolWindow.mws.dto.ClassIndexDTO;
import com.augustlee.mt.toolWindow.mws.service.SearchCacheManager;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * API 缓存搜索面板
 *
 * @see ApiCacheSearchPanel
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class ApiCacheSearchPanel {

    private static final ConsoleLogger LOG = ConsoleLogger.getInstance(ApiCacheSearchPanel.class);

    private static final String CACHE_SIZE = "Cache size: ";

    private final JPanel MAIN_PANEL = new JPanel();

    private final JLabel CACHE_SIZE_LABEL = new JLabel(CACHE_SIZE + "0");
    private final JButton REFRESH_BUTTON = new JButton("Refresh");
    private final JTextArea COOKIE_TEXT_AREA = new JTextArea(8, 30);

    private final JLabel API_LABEL = new JLabel("API：");
    private final JTextField API_TEXT_FIELD = new JTextField(30);

    private final JButton SEARCH_BUTTON = new JButton("Search");
    private final JButton GET_COOKIE_BUTTON = new JButton("自动获取Cookie");

    /**
     * 方法反查相关组件。
     */
    private final JLabel METHOD_LABEL = new JLabel("方法：");
    private final JTextField METHOD_TEXT_FIELD = new JTextField(30);
    private final JButton METHOD_SEARCH_BUTTON = new JButton("查询接口地址");
    private final JLabel METHOD_RESULT_HINT_LABEL = new JLabel("结果：点击列表项可复制接口地址");
    private final DefaultListModel<String> METHOD_RESULT_LIST_MODEL = new DefaultListModel<>();
    private final JList<String> METHOD_RESULT_LIST = new JList<>(METHOD_RESULT_LIST_MODEL);

    private final SearchCacheManager SEARCH_CACHE_MANAGER;

    private Project project;
    private CookieInputState cookieState;

    public ApiCacheSearchPanel(Project project, CookieInputState cookieState, ApiPathState apiPathState){
        this.SEARCH_CACHE_MANAGER = new SearchCacheManager(apiPathState);
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

        // 方法反查按钮（设置weighty为0）
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        MAIN_PANEL.add(METHOD_SEARCH_BUTTON, gbc);
        gbc.gridwidth = 1;

        // 结果提示
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        MAIN_PANEL.add(METHOD_RESULT_HINT_LABEL, gbc);
        gbc.gridwidth = 1;

        // 结果列表
        METHOD_RESULT_LIST.setVisibleRowCount(6);
        METHOD_RESULT_LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JBScrollPane resultScrollPane = new JBScrollPane(METHOD_RESULT_LIST);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        MAIN_PANEL.add(resultScrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加底部占位符将内容推至顶部
        gbc.gridy = 8;
        gbc.weighty = 1;
        MAIN_PANEL.add(Box.createGlue(), gbc);
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
        this.METHOD_RESULT_LIST.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                copySelectedApiPath();
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
        LOG.info("=== Refresh 按钮被点击 ===");

        if (project == null) {
            LOG.error("project 为 null，无法执行刷新操作");
            Messages.showErrorDialog((Project) null, "Project 未初始化，无法刷新", "Error");
            return;
        }

        this.SEARCH_BUTTON.setEnabled(false);
        this.REFRESH_BUTTON.setEnabled(false);
        this.METHOD_SEARCH_BUTTON.setEnabled(false);

        LOG.info("创建后台任务，project: " + project.getName());
        long refreshStartTime = System.currentTimeMillis();
        new Task.Backgroundable(project, "Refreshing API Cache", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                LOG.info("=== 后台任务开始执行 ===");
                indicator.setIndeterminate(true);
                indicator.setText("Refreshing API cache...");
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
                        METHOD_RESULT_HINT_LABEL.setText("结果：点击列表项可复制接口地址");
                        SEARCH_BUTTON.setEnabled(true);
                        REFRESH_BUTTON.setEnabled(true);
                        METHOD_SEARCH_BUTTON.setEnabled(true);

                        // 显示成功提示
                        String message = String.format(
                            "API 缓存刷新完成！\n\n" +
                            "缓存 API 数量：%d 个\n" +
                            "耗时：%.2f 秒",
                            apiCount,
                            duration / 1000.0
                        );
                        Messages.showInfoMessage(project, message, "Refresh Success");
                    });
                } catch (Exception e) {
                    LOG.error("刷新 API 缓存时发生错误", e);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog(project, "刷新失败：" + e.getMessage(), "Refresh Failed");
                        SEARCH_BUTTON.setEnabled(true);
                        REFRESH_BUTTON.setEnabled(true);
                        METHOD_SEARCH_BUTTON.setEnabled(true);
                    });
                }
            }
        }.queue();
        LOG.info("后台任务已提交到队列");
    }

    private void searchApi(ActionEvent actionEvent) {
        String path = this.API_TEXT_FIELD.getText();
        try{
            path = path.trim();

            // 输入验证
            if (path == null || path.isEmpty()) {
                Messages.showErrorDialog(project, "请输入 API 路径", "Error");
                return;
            }

            // 检查缓存是否为空
            int cacheSize = this.SEARCH_CACHE_MANAGER.getApiCount();
            if (cacheSize == 0) {
                Messages.showErrorDialog(project,
                    "API 缓存为空，请先点击 Refresh 按钮刷新缓存",
                    "Cache Empty");
                return;
            }

            ClassIndexDTO classIndexDTO = this.SEARCH_CACHE_MANAGER.getClassIndex(path);
            if(classIndexDTO == null){
                Messages.showErrorDialog(project,
                    "API not found: " + path + "\n\n" +
                    "当前缓存中有 " + cacheSize + " 个 API。\n" +
                    "请确认路径是否正确，或点击 Refresh 按钮刷新缓存。",
                    "API Not Found");
                return;
            }
            LOG.debug("找到 API: " + JSON.toJSONString(classIndexDTO));
            goToCode(classIndexDTO.getServiceName(), classIndexDTO.getMethodName(), project);
        } catch (Exception e) {
            Messages.showErrorDialog(project, e.getMessage(), "Search Failed");
            e.printStackTrace();
        }
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
            ClassIndexDTO classIndexDTO = this.parseMethodExpression(methodExpression);

            // 检查缓存是否为空
            int cacheSize = this.SEARCH_CACHE_MANAGER.getApiCount();
            if (cacheSize == 0) {
                clearMethodResult("结果：API 缓存为空，请先点击 Refresh 按钮刷新缓存");
                Messages.showErrorDialog(project,
                        "API 缓存为空，请先点击 Refresh 按钮刷新缓存",
                        "缓存为空");
                return;
            }

            List<ApiIndexDTO> apiIndexDTOList = this.SEARCH_CACHE_MANAGER.getApiIndexListByMethod(
                    classIndexDTO.getServiceName(),
                    classIndexDTO.getMethodName()
            );
            if (apiIndexDTOList == null || apiIndexDTOList.isEmpty()) {
                clearMethodResult("结果：未找到与该方法绑定的接口地址");
                Messages.showInfoMessage(project,
                        "未找到与该方法绑定的 API path",
                        "未找到结果");
                return;
            }

            Set<String> apiPathSet = new LinkedHashSet<>();
            for (ApiIndexDTO apiIndexDTO : apiIndexDTOList) {
                if (apiIndexDTO.getPath() != null && !apiIndexDTO.getPath().trim().isEmpty()) {
                    apiPathSet.add(apiIndexDTO.getPath().trim());
                }
            }

            METHOD_RESULT_LIST_MODEL.clear();
            for (String apiPath : apiPathSet) {
                METHOD_RESULT_LIST_MODEL.addElement(apiPath);
            }
            METHOD_RESULT_HINT_LABEL.setText("结果：共找到 " + METHOD_RESULT_LIST_MODEL.size() + " 个接口地址，点击列表项可复制");
        } catch (IllegalArgumentException e) {
            clearMethodResult("结果：方法表达式不合法");
            Messages.showErrorDialog(project, e.getMessage(), "方法格式错误");
        } catch (Exception e) {
            clearMethodResult("结果：查询失败");
            Messages.showErrorDialog(project, e.getMessage(), "查询失败");
            LOG.error("根据方法反查接口地址失败", e);
        }
    }

    /**
     * 解析方法表达式。
     *
     * @param expression 方法表达式
     * @return 解析后的类方法索引
     */
    private ClassIndexDTO parseMethodExpression(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("请输入方法全限定名，例如：com.foo.Service#method");
        }

        String normalizedExpression = expression.trim();
        int bracketIndex = normalizedExpression.indexOf('(');
        if (bracketIndex >= 0) {
            normalizedExpression = normalizedExpression.substring(0, bracketIndex).trim();
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
        return new ClassIndexDTO(serviceName, methodName);
    }

    /**
     * 复制当前选中的接口地址。
     */
    private void copySelectedApiPath() {
        String selectedPath = METHOD_RESULT_LIST.getSelectedValue();
        if (selectedPath == null || selectedPath.trim().isEmpty()) {
            return;
        }
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
        METHOD_RESULT_LIST_MODEL.clear();
        METHOD_RESULT_HINT_LABEL.setText(hintText);
    }

    private void goToCode(String serviceName, String methodName, Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (DumbService.isDumb(project)) {
                Messages.showErrorDialog(project, "IDE is updating indices. Please try again later.", "Error");
                return;
            }

            ApplicationManager.getApplication().runReadAction(() -> {
                // 1. 查找类
                PsiClass targetClass = JavaPsiFacade.getInstance(project)
                        .findClass(serviceName, GlobalSearchScope.allScope(project));

                if (targetClass == null) {
                    Messages.showErrorDialog(project, "Class not found: " + serviceName, "Error");
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
                    Messages.showErrorDialog(project, "Method not found: " + methodName, "Error");
                    return;
                }

                // 3. 导航到方法
                NavigationUtil.activateFileWithPsiElement(targetMethod, true);
            });
        });
    }

}
