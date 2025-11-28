package com.augustlee.mt.toolWindow.mws.constants;

/**
 * MWS 模块面板优先级枚举
 * 用于定义各个面板在选项卡中的显示顺序
 * 数值越小，优先级越高，显示位置越靠前
 *
 * @see MwsPanelPriority
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public enum MwsPanelPriority {

    /**
     * API Search Cache 面板优先级
     */
    API_SEARCH_CACHE(1, "API Search Cache 面板"),

    /**
     * API Search 面板优先级
     */
    API_SEARCH(2, "API Search 面板");

    /**
     * 优先级数值
     * 数值越小，优先级越高
     */
    private final int priority;

    /**
     * 面板描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param priority
     *                优先级数值
     * @param description
     *                面板描述
     */
    MwsPanelPriority(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }

    /**
     * 获取优先级数值
     *
     * @return 优先级数值
     */
    public int getPriority() {
        return priority;
    }

    /**
     * 获取面板描述
     *
     * @return 面板描述
     */
    public String getDescription() {
        return description;
    }
}
