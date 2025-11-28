package com.augustlee.mt.toolWindow.mws;

import com.augustlee.mt.toolWindow.common.api.AbstractModuleProvider;
import com.augustlee.mt.toolWindow.common.constants.ModulePriority;
import com.augustlee.mt.toolWindow.mws.provider.ApiCacheSearchPanelProvider;
import com.augustlee.mt.toolWindow.mws.provider.ApiSearchPanelProvider;
import com.augustlee.mt.toolWindow.mws.provider.MwsPanelProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MWS 模块提供者
 * 负责提供 API 搜索相关的功能模块
 * 注意：此模块包含多个面板（API Search Cache 和 API Search）
 *
 * @see MwsModuleProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class MwsModuleProvider extends AbstractModuleProvider {

    /**
     * 模块内的面板提供者列表
     */
    private final List<MwsPanelProvider> panelProviders;

    /**
     * 受保护的构造函数，防止外部私自创建实例
     * 必须通过 init() 方法创建实例
     */
    protected MwsModuleProvider() {
        this.panelProviders = new ArrayList<>();
    }

    /**
     * 初始化并创建 MwsModuleProvider 实例
     *
     * @return MwsModuleProvider 实例
     */
    public static MwsModuleProvider init() {
        MwsModuleProvider provider = new MwsModuleProvider();
        // 初始化面板提供者
        provider.panelProviders.add(ApiCacheSearchPanelProvider.init());
        provider.panelProviders.add(ApiSearchPanelProvider.init());
        return provider;
    }

    @Override
    @NotNull
    public String getModuleName() {
        return "mws";
    }

    @Override
    @NotNull
    public String getDisplayName() {
        // MWS 模块包含多个面板，这里返回第一个面板的名称
        // 实际使用中，每个面板会单独注册
        return "API Search Cache";
    }

    @Override
    @NotNull
    public JPanel createPanel(@NotNull Project project) {
        // 返回第一个面板（API Search Cache）
        return panelProviders.get(0).createPanel(project);
    }

    /**
     * 获取所有面板提供者
     * 用于支持一个模块提供多个面板的场景
     *
     * @return 面板提供者列表
     */
    public List<MwsPanelProvider> getPanelProviders() {
        return panelProviders;
    }

    @Override
    public int getPriority() {
        return ModulePriority.MWS.getPriority();
    }
}

