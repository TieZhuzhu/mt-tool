package com.augustlee.mt.toolWindow.mws.provider;

import com.augustlee.mt.toolWindow.common.state.CookieInputState;
import com.augustlee.mt.toolWindow.mws.constants.MwsPanelPriority;
import com.augustlee.mt.toolWindow.mws.panel.ApiSearchPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * API Search 面板提供者
 *
 * @see ApiSearchPanelProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class ApiSearchPanelProvider extends AbstractMwsPanelProvider {

    /**
     * 初始化并创建 ApiSearchPanelProvider 实例
     *
     * @return ApiSearchPanelProvider 实例
     */
    public static ApiSearchPanelProvider init() {
        return new ApiSearchPanelProvider();
    }

    @Override
    public String getDisplayName() {
        return "API Search";
    }

    @Override
    public JPanel createPanel(Project project) {
        CookieInputState cookieState = ApplicationManager.getApplication()
                .getService(CookieInputState.class);

        return new ApiSearchPanel(project, cookieState).getMainJPanel();
    }

    @Override
    public int getPriority() {
        return MwsPanelPriority.API_SEARCH.getPriority();
    }
}

