package com.augustlee.mt.toolWindow.otherdevtools;

import com.augustlee.mt.toolWindow.common.api.AbstractModuleProvider;
import com.augustlee.mt.toolWindow.common.constants.ModulePriority;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * OtherDevTools 模块提供者
 * 负责提供其他开发工具相关的功能模块
 *
 * @see OtherDevToolsModuleProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class OtherDevToolsModuleProvider extends AbstractModuleProvider {

    /**
     * 初始化并创建 OtherDevToolsModuleProvider 实例
     *
     * @return OtherDevToolsModuleProvider 实例
     */
    public static OtherDevToolsModuleProvider init() {
        return new OtherDevToolsModuleProvider();
    }

    @Override
    @NotNull
    public String getModuleName() {
        return "otherdevtools";
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return "Other Dev Tools";
    }

    @Override
    @NotNull
    public JPanel createPanel(@NotNull Project project) {
        return new OtherDevToolsPanel(project).getMainPanel();
    }

    @Override
    public int getPriority() {
        return ModulePriority.OTHER_DEV_TOOLS.getPriority();
    }
}

