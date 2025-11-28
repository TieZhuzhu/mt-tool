package com.augustlee.mt.toolWindow.common.constants;

/**
 * 模块优先级枚举
 * 用于定义各个功能模块在工具窗口中的显示顺序
 * 数值越小，优先级越高，显示位置越靠前
 *
 * @see ModulePriority
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public enum ModulePriority {

    /**
     * MWS 模块优先级
     * 包含 API Search Cache 和 API Search 两个面板
     */
    MWS(1, "MWS 模块"),

    /**
     * Code 模块优先级
     * 包含 API Maven Search 面板
     */
    CODE(3, "Code 模块"),

    /**
     * OtherDevTools 模块优先级
     * 包含其他开发工具面板
     */
    OTHER_DEV_TOOLS(4, "Other Dev Tools 模块");

    /**
     * 优先级数值
     * 数值越小，优先级越高
     */
    private final int priority;

    /**
     * 模块描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param priority
     *                优先级数值
     * @param description
     *                模块描述
     */
    ModulePriority(int priority, String description) {
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
     * 获取模块描述
     *
     * @return 模块描述
     */
    public String getDescription() {
        return description;
    }
}
