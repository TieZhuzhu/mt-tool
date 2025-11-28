package com.augustlee.mt.toolWindow.common.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * 配置读取工具类
 * 提供通用的系统属性读取方法
 *
 * <p>配置优先级：</p>
 * <ol>
 *   <li>系统属性（System.getProperty）- 最高优先级，可通过 JVM 参数传递（-Dkey=value）</li>
 *   <li>资源文件（application.properties）- 可通过 gradle.properties 在构建时注入</li>
 *   <li>硬编码默认值 - 如果以上都未设置，使用代码中的默认值</li>
 * </ol>
 *
 * <p>使用说明：</p>
 * <ul>
 *   <li>此类为底层读取实现，通常不直接使用</li>
 *   <li>建议通过 {@link ConfigUtil} 工具类来读取配置</li>
 *   <li>默认值可以通过以下方式配置：</li>
 *   <li>1. 在 gradle.properties 中设置（构建时注入到 application.properties）</li>
 *   <li>2. 在 application.properties 中直接设置</li>
 *   <li>3. 在代码中硬编码（作为最后的兜底值）</li>
 * </ul>
 * 
 * <p>直接使用示例（不推荐）：</p>
 * <pre>{@code
 * // 读取布尔类型配置
 * boolean enabled = ConfigReader.getBooleanProperty(Config.CONSOLE_LOG_KEY, Config.DEFAULT_CONSOLE_LOG_ENABLED);
 * 
 * // 读取字符串类型配置
 * String value = ConfigReader.getStringProperty("mt.tool.some.key", "defaultValue");
 * 
 * // 读取整数类型配置
 * int timeout = ConfigReader.getIntProperty("mt.tool.timeout", 30);
 * }</pre>
 *
 * @see Config
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public final class ConfigReader {
    
    /**
     * 资源文件中的配置（从 application.properties 加载）
     * 在构建时可以通过 gradle.properties 注入值
     */
    private static final Properties RESOURCE_PROPERTIES = loadResourceProperties();
    
    /**
     * 私有构造函数，防止实例化
     */
    private ConfigReader() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
    
    /**
     * 从资源文件加载配置
     * 
     * @return 加载的配置属性，如果加载失败则返回空的 Properties
     */
    private static Properties loadResourceProperties() {
        Properties props = new Properties();
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            // 资源文件不存在或加载失败时，使用空 Properties（会回退到硬编码默认值）
            // 不打印日志，避免在正常运行时产生噪音
        }
        return props;
    }
    
    /**
     * 从资源文件读取配置值
     * 
     * @param key 配置键
     * @return 配置值，如果不存在则返回 null
     */
    private static String getFromResource(String key) {
        return RESOURCE_PROPERTIES.getProperty(key);
    }
    
    /**
     * 读取布尔类型的配置
     * 
     * <p>读取顺序：</p>
     * <ol>
     *   <li>系统属性（System.getProperty）- 最高优先级</li>
     *   <li>资源文件（application.properties）- 主要的默认值来源</li>
     *   <li>硬编码默认值 - 最后的兜底值</li>
     * </ol>
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static boolean getBooleanProperty(String key, boolean fallbackDefaultValue) {
        // 1. 优先从系统属性读取（运行时覆盖）
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return Boolean.parseBoolean(systemValue);
        }
        
        // 2. 从资源文件读取（主要的默认值来源）
        String resourceValue = getFromResource(key);
        if (resourceValue != null && !resourceValue.trim().isEmpty()) {
            return Boolean.parseBoolean(resourceValue.trim());
        }
        
        // 3. 使用硬编码默认值（最后的兜底）
        return fallbackDefaultValue;
    }
    
    /**
     * 读取字符串类型的配置
     * 
     * <p>读取顺序：</p>
     * <ol>
     *   <li>系统属性（System.getProperty）- 最高优先级</li>
     *   <li>资源文件（application.properties）- 主要的默认值来源</li>
     *   <li>硬编码默认值 - 最后的兜底值</li>
     * </ol>
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static String getStringProperty(String key, String fallbackDefaultValue) {
        // 1. 优先从系统属性读取（运行时覆盖）
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        
        // 2. 从资源文件读取（主要的默认值来源）
        String resourceValue = getFromResource(key);
        if (resourceValue != null && !resourceValue.trim().isEmpty()) {
            return resourceValue.trim();
        }
        
        // 3. 使用硬编码默认值（最后的兜底）
        return fallbackDefaultValue;
    }
    
    /**
     * 读取整数类型的配置
     * 
     * <p>读取顺序：</p>
     * <ol>
     *   <li>系统属性（System.getProperty）- 最高优先级</li>
     *   <li>资源文件（application.properties）- 主要的默认值来源</li>
     *   <li>硬编码默认值 - 最后的兜底值</li>
     * </ol>
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static int getIntProperty(String key, int fallbackDefaultValue) {
        // 1. 优先从系统属性读取（运行时覆盖）
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            try {
                return Integer.parseInt(systemValue.trim());
            } catch (NumberFormatException e) {
                return fallbackDefaultValue;
            }
        }
        
        // 2. 从资源文件读取（主要的默认值来源）
        String resourceValue = getFromResource(key);
        if (resourceValue != null && !resourceValue.trim().isEmpty()) {
            try {
                return Integer.parseInt(resourceValue.trim());
            } catch (NumberFormatException e) {
                return fallbackDefaultValue;
            }
        }
        
        // 3. 使用硬编码默认值（最后的兜底）
        return fallbackDefaultValue;
    }
    
    /**
     * 读取长整数类型的配置
     * 
     * <p>读取顺序：</p>
     * <ol>
     *   <li>系统属性（System.getProperty）- 最高优先级</li>
     *   <li>资源文件（application.properties）- 主要的默认值来源</li>
     *   <li>硬编码默认值 - 最后的兜底值</li>
     * </ol>
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static long getLongProperty(String key, long fallbackDefaultValue) {
        // 1. 优先从系统属性读取（运行时覆盖）
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            try {
                return Long.parseLong(systemValue.trim());
            } catch (NumberFormatException e) {
                return fallbackDefaultValue;
            }
        }
        
        // 2. 从资源文件读取（主要的默认值来源）
        String resourceValue = getFromResource(key);
        if (resourceValue != null && !resourceValue.trim().isEmpty()) {
            try {
                return Long.parseLong(resourceValue.trim());
            } catch (NumberFormatException e) {
                return fallbackDefaultValue;
            }
        }
        
        // 3. 使用硬编码默认值（最后的兜底）
        return fallbackDefaultValue;
    }
    
    /**
     * 读取双精度浮点数类型的配置
     * 
     * <p>读取顺序：</p>
     * <ol>
     *   <li>系统属性（System.getProperty）- 最高优先级</li>
     *   <li>资源文件（application.properties）- 主要的默认值来源</li>
     *   <li>硬编码默认值 - 最后的兜底值</li>
     * </ol>
     * 
     * @param key 配置键
     * @param fallbackDefaultValue 硬编码兜底值（仅在 application.properties 中也没有配置时使用）
     * @return 配置值
     */
    public static double getDoubleProperty(String key, double fallbackDefaultValue) {
        // 1. 优先从系统属性读取（运行时覆盖）
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            try {
                return Double.parseDouble(systemValue.trim());
            } catch (NumberFormatException e) {
                return fallbackDefaultValue;
            }
        }
        
        // 2. 从资源文件读取（主要的默认值来源）
        String resourceValue = getFromResource(key);
        if (resourceValue != null && !resourceValue.trim().isEmpty()) {
            try {
                return Double.parseDouble(resourceValue.trim());
            } catch (NumberFormatException e) {
                return fallbackDefaultValue;
            }
        }
        
        // 3. 使用硬编码默认值（最后的兜底）
        return fallbackDefaultValue;
    }
}

