package com.augustlee.mt.toolWindow.code;

import com.augustlee.mt.toolWindow.code.panel.ApiMavenSearchPanel;
import com.augustlee.mt.toolWindow.common.api.AbstractModuleProvider;
import com.augustlee.mt.toolWindow.common.constants.ModulePriority;
import com.augustlee.mt.toolWindow.common.state.CookieInputState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Code 模块提供者
 * 负责提供 Maven 相关的功能模块
 *
 * @see CodeModuleProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class CodeModuleProvider extends AbstractModuleProvider {

    /**
     * 初始化并创建 CodeModuleProvider 实例
     *
     * @return CodeModuleProvider 实例
     */
    public static CodeModuleProvider init() {
        return new CodeModuleProvider();
    }

    @Override
    @NotNull
    public String getModuleName() {
        return "code";
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return "API Maven Search";
    }

    @Override
    @NotNull
    public JPanel createPanel(@NotNull Project project) {
        CookieInputState cookieState = ApplicationManager.getApplication()
                .getService(CookieInputState.class);

        return new ApiMavenSearchPanel(project, cookieState).getMainJPanel();
    }

    @Override
    public int getPriority() {
        return ModulePriority.CODE.getPriority();
    }
}

