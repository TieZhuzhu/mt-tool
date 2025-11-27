package com.augustlee.mt.toolWindow.otherdevtools;

import com.augustlee.mt.toolWindow.otherdevtools.devfuncbtn.MenuPrivilegeConverter;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Other Dev Tools 主面板
 * 包含功能区和工具实现区，支持多个开发工具的扩展
 *
 * @see OtherDevToolsPanel
 * @author August Lee
 * @since 2025/11/27 11:51
 */
public class OtherDevToolsPanel {

    /**
     * 主面板
     */
    private final JPanel MAIN_PANEL = new JPanel();

    /**
     * 功能区面板
     */
    private final JPanel FUNCTION_AREA_PANEL = new JPanel();

    /**
     * 工具实现区面板
     */
    private final JPanel TOOL_IMPLEMENTATION_PANEL = new JPanel();

    /**
     * 当前显示的工具面板
     */
    private JPanel currentToolPanel = null;

    /**
     * 项目对象
     */
    private Project project;

    /**
     * ST菜单转换工具
     */
    private MenuPrivilegeConverter menuPrivilegeConverter;

    /**
     * 构造函数
     *
     * @param project 项目对象
     */
    public OtherDevToolsPanel(@NotNull Project project) {
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
        // 主面板使用 BorderLayout，上下分布
        MAIN_PANEL.setLayout(new BorderLayout());

        // 功能区面板：使用 FlowLayout，按钮横向排列
        FUNCTION_AREA_PANEL.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        FUNCTION_AREA_PANEL.setBorder(BorderFactory.createTitledBorder("功能区"));

        // 工具实现区面板：使用 BorderLayout，动态显示工具
        TOOL_IMPLEMENTATION_PANEL.setLayout(new BorderLayout());
        TOOL_IMPLEMENTATION_PANEL.setBorder(BorderFactory.createTitledBorder("工具实现区"));

        // 将功能区放在上方，工具实现区放在下方
        MAIN_PANEL.add(FUNCTION_AREA_PANEL, BorderLayout.NORTH);
        MAIN_PANEL.add(TOOL_IMPLEMENTATION_PANEL, BorderLayout.CENTER);
    }

    /**
     * 初始化组件
     */
    private void initComponents() {
        // 创建 ST菜单转换 按钮
        JButton stMenuConvertButton = new JButton("ST菜单转换");
        stMenuConvertButton.addActionListener(e -> showMenuPrivilegeConverter());

        // 将按钮添加到功能区
        FUNCTION_AREA_PANEL.add(stMenuConvertButton);

        // 初始化工具实现区为空状态
        showEmptyToolPanel();
    }

    /**
     * 显示 ST菜单转换 工具
     */
    private void showMenuPrivilegeConverter() {
        // 如果工具还未创建，则创建它
        if (menuPrivilegeConverter == null) {
            menuPrivilegeConverter = new MenuPrivilegeConverter(project);
        }

        // 显示工具面板
        showToolPanel(menuPrivilegeConverter.getMainPanel());
    }

    /**
     * 显示指定的工具面板
     *
     * @param toolPanel 工具面板
     */
    private void showToolPanel(JPanel toolPanel) {
        // 移除当前显示的面板
        if (currentToolPanel != null) {
            TOOL_IMPLEMENTATION_PANEL.remove(currentToolPanel);
        }

        // 添加新的工具面板
        currentToolPanel = toolPanel;
        TOOL_IMPLEMENTATION_PANEL.add(toolPanel, BorderLayout.CENTER);

        // 刷新显示
        TOOL_IMPLEMENTATION_PANEL.revalidate();
        TOOL_IMPLEMENTATION_PANEL.repaint();
    }

    /**
     * 显示空状态面板
     */
    private void showEmptyToolPanel() {
        JLabel emptyLabel = new JLabel("请从功能区选择工具");
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setVerticalAlignment(SwingConstants.CENTER);
        emptyLabel.setForeground(JBColor.GRAY);

        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.add(emptyLabel, BorderLayout.CENTER);

        showToolPanel(emptyPanel);
    }
}

