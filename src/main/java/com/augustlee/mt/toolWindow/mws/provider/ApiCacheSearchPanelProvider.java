package com.augustlee.mt.toolWindow.mws.provider;

import com.augustlee.mt.toolWindow.common.state.ApiPathState;
import com.augustlee.mt.toolWindow.common.state.CookieInputState;
import com.augustlee.mt.toolWindow.mws.constants.MwsPanelPriority;
import com.augustlee.mt.toolWindow.mws.panel.ApiCacheSearchPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * API Cache Search 面板提供者
 *
 * @see ApiCacheSearchPanelProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class ApiCacheSearchPanelProvider extends AbstractMwsPanelProvider {

    /**
     * 初始化并创建 ApiCacheSearchPanelProvider 实例
     *
     * @return ApiCacheSearchPanelProvider 实例
     */
    public static ApiCacheSearchPanelProvider init() {
        return new ApiCacheSearchPanelProvider();
    }

    @Override
    public String getDisplayName() {
        return "API Search Cache";
    }

    @Override
    public JPanel createPanel(Project project) {
        CookieInputState cookieState = ApplicationManager.getApplication()
                .getService(CookieInputState.class);
        ApiPathState apiPathState = ApplicationManager.getApplication()
                .getService(ApiPathState.class);

        return new ApiCacheSearchPanel(project, cookieState, apiPathState).getMainJPanel();
    }

    @Override
    public int getPriority() {
        return MwsPanelPriority.API_SEARCH_CACHE.getPriority();
    }
}

