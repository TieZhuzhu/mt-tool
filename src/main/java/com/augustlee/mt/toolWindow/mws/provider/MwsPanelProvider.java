package com.augustlee.mt.toolWindow.mws.provider;

import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * MWS 模块内部面板提供者接口
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
 * public class MyPanelProvider implements MwsPanelProvider {
 *     private MyPanelProvider() {
 *         // 私有构造函数，防止私自创建
 *     }
 *
 *     public static MyPanelProvider init() {
 *         return new MyPanelProvider();
 *     }
 *
 *     // 实现接口方法...
 * }
 * }</pre>
 *
 * @see MwsPanelProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public interface MwsPanelProvider {

    /**
     * 获取面板显示名称
     *
     * @return 显示名称
     */
    String getDisplayName();

    /**
     * 创建面板
     *
     * @param project
     *                IntelliJ 项目对象
     * @return 面板
     */
    JPanel createPanel(Project project);

    /**
     * 获取面板优先级
     *
     * @return 优先级数值
     */
    int getPriority();
}

