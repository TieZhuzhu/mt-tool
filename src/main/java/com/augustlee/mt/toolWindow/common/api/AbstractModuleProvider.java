package com.augustlee.mt.toolWindow.common.api;

/**
 * 模块提供者抽象基类
 * 提供受保护的构造函数，防止外部私自创建实例
 * 子类必须通过 init() 方法创建实例
 *
 * <p><b>使用说明：</b></p>
 * <ul>
 *   <li>所有实现类应继承此类，而不是直接实现 ModuleProvider 接口</li>
 *   <li>如果子类不需要额外初始化，可以不写构造函数（会自动调用父类的 protected 构造函数）</li>
 *   <li>如果子类需要初始化，应写 protected 构造函数并调用 super()</li>
 *   <li>所有子类必须提供静态 init() 方法用于创建实例</li>
 * </ul>
 *
 * @see AbstractModuleProvider
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public abstract class AbstractModuleProvider implements ModuleProvider {

    /**
     * 受保护的构造函数，防止外部私自创建实例
     * 子类必须通过 init() 方法创建实例
     */
    protected AbstractModuleProvider() {
    }
}

