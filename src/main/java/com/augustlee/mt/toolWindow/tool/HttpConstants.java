package com.augustlee.mt.toolWindow.tool;

/**
 * HTTP 相关常量类
 * 用于统一管理 HTTP 请求中的硬编码字符串
 */
public final class HttpConstants {

    /**
     * Cookie 请求头名称
     */
    public static final String HEADER_COOKIE = "Cookie";

    /**
     * Content-Type 请求头名称
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Authorization 请求头名称
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * 默认的 Content-Type 值
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * 私有构造函数，防止实例化
     */
    private HttpConstants() {
        throw new UnsupportedOperationException("常量类不能被实例化");
    }
}

