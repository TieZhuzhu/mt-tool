package com.augustlee.mt.toolWindow;

import com.augustlee.mt.toolWindow.common.api.ModuleProvider;
import com.augustlee.mt.toolWindow.code.CodeModuleProvider;
import com.augustlee.mt.toolWindow.mws.MwsModuleProvider;
import com.augustlee.mt.toolWindow.mws.provider.MwsPanelProvider;
import com.augustlee.mt.toolWindow.otherdevtools.OtherDevToolsModuleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 工具窗口内容类
 * 负责创建和布局各个模块的面板
 *
 * @see CalendarToolWindowContent
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class CalendarToolWindowContent {

    /**
     * 主面板
     */
    private final JPanel mainPanel = new JPanel();

    /**
     * 选项卡面板
     */
    private final JTabbedPane tabbedPane = new JBTabbedPane();

    /**
     * 模块提供者列表
     */
    private final List<ModuleProvider> moduleProviders;

    /**
     * 构造函数
     *
     * @param project
     *                IntelliJ 项目对象
     */
    public CalendarToolWindowContent(@NotNull Project project) {
        this.moduleProviders = initializeModules();
        initLayout();
        loadModules(project);
    }

    /**
     * 初始化模块
     * 注册所有可用的功能模块
     *
     * @return 模块提供者列表
     */
    private List<ModuleProvider> initializeModules() {
        List<ModuleProvider> providers = new ArrayList<>();
        // MWS 模块（包含多个面板）
        providers.add(MwsModuleProvider.init());

        // Code 模块
        providers.add(CodeModuleProvider.init());

        // OtherDevTools 模块
        providers.add(OtherDevToolsModuleProvider.init());

        return providers;
    }

    /**
     * 初始化布局
     */
    private void initLayout() {
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * 加载所有模块
     *
     * @param project
     *                IntelliJ 项目对象
     */
    private void loadModules(@NotNull Project project) {
        // 处理 MWS 模块的多个面板
        MwsModuleProvider mwsProvider = null;
        List<ModuleProvider> otherProviders = new ArrayList<>();

        for (ModuleProvider provider : moduleProviders) {
            if (provider instanceof MwsModuleProvider) {
                mwsProvider = (MwsModuleProvider) provider;
            } else {
                otherProviders.add(provider);
            }
        }

        // 加载 MWS 模块的所有面板
        if (mwsProvider != null && mwsProvider.isEnabled()) {
            mwsProvider.getPanelProviders().stream()
                    .sorted(Comparator.comparingInt(MwsPanelProvider::getPriority))
                    .forEach(panelProvider -> {
                        JPanel panel = panelProvider.createPanel(project);
                        tabbedPane.addTab(panelProvider.getDisplayName(), panel);
                    });
        }

        // 加载其他模块
        otherProviders.stream()
                .filter(ModuleProvider::isEnabled)
                .sorted(Comparator.comparingInt(ModuleProvider::getPriority))
                .forEach(provider -> {
                    JPanel panel = provider.createPanel(project);
                    tabbedPane.addTab(provider.getDisplayName(), panel);
                });
    }

    /**
     * 获取主面板
     *
     * @return 主面板
     */
    public JPanel getContentPanel() {
        return mainPanel;
    }
}

