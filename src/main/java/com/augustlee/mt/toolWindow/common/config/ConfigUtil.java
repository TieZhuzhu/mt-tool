package com.augustlee.mt.toolWindow.common.config;

/**
 * 配置工具类
 * 提供便捷的配置读取方法，内部调用 ConfigReader 进行实际读取
 *
 * <p>配置优先级（从高到低）：</p>
 * <ol>
 *   <li>系统属性（-Dkey=value）- 运行时通过 JVM 参数设置</li>
 *   <li>application.properties - 主要的默认值来源，统一管理配置</li>
 *   <li>代码硬编码默认值 - 最后的兜底值</li>
 * </ol>
 *
 * <p>使用方式：</p>
 * <pre>{@code
 * // 读取日志配置
 * boolean enableConsoleLog = ConfigUtil.isConsoleLogEnabled();
 * boolean enableAsyncLog = ConfigUtil.isAsyncLogEnabled();
 * 
 * // 读取其他配置
 * String value = ConfigUtil.getString("mt.tool.some.key", "defaultValue");
 * int timeout = ConfigUtil.getInt("mt.tool.timeout", 30);
 * }</pre>
 *
 * @see Config
 * @see ConfigReader
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public final class ConfigUtil {
    
    /**
     * 私有构造函数，防止实例化
     */
    private ConfigUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    
    // ==================== 日志配置便捷方法 ====================
    
    /**
     * 是否启用控制台日志输出
     * 
     * <p>配置来源（按优先级）：</p>
     * <ol>
     *   <li>系统属性：-Dmt.tool.console.log=true/false（最高优先级）</li>
     *   <li>application.properties：mt.tool.console.log=true/false（主要默认值）</li>
     *   <li>代码兜底值：true（最后兜底）</li>
     * </ol>
     * 
     * @return true 表示启用控制台日志输出，false 表示禁用
     */
    public static boolean isConsoleLogEnabled() {
        return ConfigReader.getBooleanProperty(
                Config.CONSOLE_LOG_KEY,
                true  // 最后的兜底值（仅在 application.properties 中也没有配置时使用）
        );
    }
    
    /**
     * 是否启用异步日志输出
     * 
     * <p>配置来源（按优先级）：</p>
     * <ol>
     *   <li>系统属性：-Dmt.tool.console.log.async=true/false（最高优先级）</li>
     *   <li>application.properties：mt.tool.console.log.async=true/false（主要默认值）</li>
     *   <li>代码兜底值：true（最后兜底）</li>
     * </ol>
     * 
     * @return true 表示启用异步日志输出，false 表示禁用
     */
    public static boolean isAsyncLogEnabled() {
        return ConfigReader.getBooleanProperty(
                Config.ASYNC_LOG_KEY,
                true  // 最后的兜底值（仅在 application.properties 中也没有配置时使用）
        );
    }
    
    // ==================== 通用配置读取方法（委托给 ConfigReader）====================
    
    /**
     * 读取布尔类型的配置
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static boolean getBoolean(String key, boolean fallbackDefaultValue) {
        return ConfigReader.getBooleanProperty(key, fallbackDefaultValue);
    }
    
    /**
     * 读取字符串类型的配置
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static String getString(String key, String fallbackDefaultValue) {
        return ConfigReader.getStringProperty(key, fallbackDefaultValue);
    }
    
    /**
     * 读取整数类型的配置
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static int getInt(String key, int fallbackDefaultValue) {
        return ConfigReader.getIntProperty(key, fallbackDefaultValue);
    }
    
    /**
     * 读取长整数类型的配置
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static long getLong(String key, long fallbackDefaultValue) {
        return ConfigReader.getLongProperty(key, fallbackDefaultValue);
    }
    
    /**
     * 读取双精度浮点数类型的配置
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static double getDouble(String key, double fallbackDefaultValue) {
        return ConfigReader.getDoubleProperty(key, fallbackDefaultValue);
    }
}

