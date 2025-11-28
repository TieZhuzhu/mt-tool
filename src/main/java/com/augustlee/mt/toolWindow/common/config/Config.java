package com.augustlee.mt.toolWindow.common.config;

/**
 * 配置参数接口
 * 定义所有配置键常量
 *
 * <p><b>配置优先级（从高到低）：</b></p>
 * <ol>
 *   <li><b>系统属性（-Dkey=value）</b> - 运行时通过 JVM 参数设置，最高优先级</li>
 *   <li><b>资源文件（application.properties）</b> - 主要的默认值来源，统一管理配置</li>
 *   <li><b>代码硬编码默认值</b> - 最后的兜底值，仅在 application.properties 中也没有配置时使用</li>
 * </ol>
 *
 * <p><b>使用方式：</b></p>
 * <pre>{@code
 * // 推荐：使用工具类（简洁易用）
 * boolean enableConsoleLog = ConfigUtil.isConsoleLogEnabled();
 * 
 * // 或者直接使用底层读取器
 * boolean enableConsoleLog = ConfigReader.getBooleanProperty(
 *     Config.CONSOLE_LOG_KEY, 
 *     true  // 最后的兜底值
 * );
 * }</pre>
 *
 * <p><b>配置管理：</b></p>
 * <ul>
 *   <li><b>主要配置来源：</b>在 src/main/resources/application.properties 中统一管理所有默认值</li>
 *   <li><b>运行时覆盖：</b>通过 JVM 参数 -Dmt.tool.console.log=false 临时覆盖</li>
 *   <li><b>代码兜底：</b>如果 application.properties 中也没有配置，才使用代码中的硬编码默认值</li>
 * </ul>
 *
 * @see ConfigReader
 * @see ConfigUtil
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public interface Config {
    
    // ==================== 日志配置键 ====================
    
    /**
     * 配置键：是否启用控制台日志输出
     * 
     * <p>配置位置：</p>
     * <ul>
     *   <li>主要：在 application.properties 中设置 mt.tool.console.log=true/false</li>
     *   <li>运行时：通过 -Dmt.tool.console.log=true/false 覆盖</li>
     * </ul>
     */
    String CONSOLE_LOG_KEY = "mt.tool.console.log";
    
    /**
     * 配置键：是否启用异步日志输出
     * 
     * <p>配置位置：</p>
     * <ul>
     *   <li>主要：在 application.properties 中设置 mt.tool.console.log.async=true/false</li>
     *   <li>运行时：通过 -Dmt.tool.console.log.async=true/false 覆盖</li>
     * </ul>
     */
    String ASYNC_LOG_KEY = "mt.tool.console.log.async";
}

