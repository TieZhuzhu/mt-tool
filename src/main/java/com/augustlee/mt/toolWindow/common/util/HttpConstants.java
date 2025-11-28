package com.augustlee.mt.toolWindow.common.util;

/**
 * HTTP 相关常量接口
 * 用于统一管理 HTTP 请求中的硬编码字符串
 *
 * @see HttpConstants
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public interface HttpConstants {

    /**
     * Cookie 请求头名称
     */
    String HEADER_COOKIE = "Cookie";

    /**
     * Content-Type 请求头名称
     */
    String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Authorization 请求头名称
     */
    String HEADER_AUTHORIZATION = "Authorization";

    /**
     * 默认的 Content-Type 值
     */
    String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
}
