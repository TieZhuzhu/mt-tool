package com.augustlee.mt.toolWindow.common.api;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 模块提供者接口
 * 所有功能模块都需要实现此接口，以便被统一管理和加载
 *
 * <p><b>实现规范：</b></p>
 * <ul>
 *   <li>所有实现类必须提供私有或受保护的构造函数，防止私自创建实例</li>
 *   <li>所有实现类必须提供静态 init() 方法用于创建实例</li>
 *   <li>init() 方法的返回类型必须是实现类本身</li>
 * </ul>
 *
 * <p><b>示例：</b></p>
 * <pre>{@code
 * public class MyModuleProvider implements ModuleProvider {
 *     private MyModuleProvider() {
 *         // 私有构造函数，防止私自创建
 *     }
 *
 *     public static MyModuleProvider init() {
 *         return new MyModuleProvider();
 *     }
 *
 *     // 实现接口方法...
 * }
 * }</pre>
 *
 * @see ModuleProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public interface ModuleProvider {

    /**
     * 获取模块名称（唯一标识符）
     *
     * @return 模块名称
     */
    @NotNull
    String getModuleName();

    /**
     * 获取模块显示名称（用于选项卡标题）
     *
     * @return 显示名称
     */
    @NotNull
    String getDisplayName();

    /**
     * 创建模块面板
     *
     * @param project
     *                IntelliJ 项目对象
     * @return 模块面板
     */
    @NotNull
    JPanel createPanel(@NotNull Project project);

    /**
     * 模块是否启用
     * 可以通过此方法控制模块的启用/禁用
     *
     * @return true 表示启用，false 表示禁用
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 模块优先级（用于排序）
     * 数值越小，优先级越高，在选项卡中的位置越靠前
     *
     * @return 优先级数值
     */
    default int getPriority() {
        return 100;
    }
}

