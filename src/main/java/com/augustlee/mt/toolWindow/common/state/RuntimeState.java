package com.augustlee.mt.toolWindow.common.state;

import com.augustlee.mt.toolWindow.common.config.Config;

/**
 * 运行时状态类
 * 用于存储运行时可变的全局状态
 *
 * <p>注意：此类用于存储运行时状态，不是配置参数</p>
 * <p>配置参数请使用 {@link Config} 接口</p>
 *
 * @see Config
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public final class RuntimeState {
    
    /**
     * 私有构造函数，防止实例化
     */
    private RuntimeState() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    
    /**
     * Cookie 名称（运行时状态）
     * 
     * @deprecated 建议直接使用 {@link com.augustlee.mt.toolWindow.common.state.CookieInputState} 服务
     *             通过 ApplicationManager.getApplication().getService(CookieInputState.class) 获取
     */
    @Deprecated
    public static volatile String COOKIE_NAME = "";
}

