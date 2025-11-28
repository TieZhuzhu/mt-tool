package com.augustlee.mt.toolWindow.tool;

import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class CurlToOkHttp {

    /**
     * 默认连接超时时间（秒）
     */
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 30;

    /**
     * 默认读取超时时间（秒）
     */
    private static final int DEFAULT_READ_TIMEOUT_SECONDS = 30;

    /**
     * OkHttpClient 缓存
     * Key: insecure 标志（true/false），Value: 对应的 OkHttpClient 实例
     */
    private static final Map<Boolean, OkHttpClient> CLIENT_CACHE = new ConcurrentHashMap<>();

    /**
     * 执行 curl 命令
     *
     * @param curlCommand
     *                    curl 命令字符串
     * @return HTTP 响应体内容
     * @throws IllegalArgumentException
     *                    当 curlCommand 为 null 或空字符串时抛出
     * @throws IllegalStateException
     *                    当 URL 为空或 SSL 上下文初始化失败时抛出
     * @throws IOException
     *                    当网络请求失败时抛出
     */
    public static String executeCurl(String curlCommand) throws IOException {
        // 输入验证
        validateCurlCommand(curlCommand);

        try {
            // 解析 curl 命令
            CurlRequest curlRequest = parseCurl(curlCommand);

            // 验证请求参数
            validateCurlRequest(curlRequest);

            // 执行 HTTP 请求并返回响应
            return executeHttpRequest(curlRequest);
        } catch (IllegalStateException e) {
            // 重新抛出 IllegalStateException
            throw e;
        } catch (IllegalArgumentException e) {
            // 重新抛出 IllegalArgumentException
            throw e;
        } catch (Exception e) {
            // 将其他异常包装为 IOException
            throw new IOException("执行 curl 命令时发生错误: " + e.getMessage(), e);
        }
    }

    /**
     * 验证 curl 命令输入
     *
     * @param curlCommand
     *                    curl 命令字符串
     * @throws IllegalArgumentException
     *                    当 curlCommand 为 null 或空字符串时抛出
     */
    private static void validateCurlCommand(String curlCommand) {
        if (curlCommand == null || curlCommand.trim().isEmpty()) {
            throw new IllegalArgumentException("curl 命令不能为空");
        }
    }

    /**
     * 验证解析后的 curl 请求参数
     *
     * @param curlRequest
     *                    curl 请求对象
     * @throws IllegalStateException
     *                    当 URL 为空时抛出
     */
    private static void validateCurlRequest(CurlRequest curlRequest) {
        if (curlRequest.url == null || curlRequest.url.trim().isEmpty()) {
            throw new IllegalStateException("curl 命令中缺少有效的 URL");
        }
    }

    /**
     * 执行 HTTP 请求并返回响应体内容
     *
     * @param curlRequest
     *                    curl 请求对象
     * @return HTTP 响应体内容
     * @throws IOException
     *                    当网络请求失败时抛出
     */
    private static String executeHttpRequest(CurlRequest curlRequest) throws IOException {
        // 获取或构建 OkHttpClient（支持忽略 SSL 证书验证）
        OkHttpClient client = getOrCreateOkHttpClient(curlRequest.insecure);

        // 构建请求
        Request request = buildRequest(curlRequest);

        // 执行请求并处理响应（使用 try-with-resources 确保资源正确关闭）
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }

            // 返回响应体内容
            return responseBody.string();
        }
    }

    private static CurlRequest parseCurl(String curlCommand) {
        CurlRequest request = new CurlRequest();
        String[] tokens = tokenizeCurlCommand(curlCommand);

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            switch (token) {
                case "-X":
                case "--request":
                    // 数组越界检查
                    if (i + 1 >= tokens.length) {
                        throw new IllegalArgumentException("缺少请求方法参数: " + token);
                    }
                    request.method = tokens[++i];
                    break;
                case "-H":
                case "--header":
                    // 数组越界检查
                    if (i + 1 >= tokens.length) {
                        throw new IllegalArgumentException("缺少请求头参数: " + token);
                    }
                    String[] header = tokens[++i].split(":", 2);
                    if (header.length == 2) {
                        request.headers.put(header[0].trim(), header[1].trim());
                    }
                    break;
                case "-d":
                case "--data":
                case "--data-ascii":
                case "--data-raw":
                    // 数组越界检查
                    if (i + 1 >= tokens.length) {
                        throw new IllegalArgumentException("缺少请求体参数: " + token);
                    }
                    request.body = tokens[++i];
                    break;
                case "-u":
                case "--user":
                    // 数组越界检查
                    if (i + 1 >= tokens.length) {
                        throw new IllegalArgumentException("缺少用户认证参数: " + token);
                    }
                    String[] creds = tokens[++i].split(":", 2);
                    if (creds.length == 2) {
                        request.username = creds[0];
                        request.password = creds[1];
                    }
                    break;
                case "-b":
                case "--cookie":
                    // 支持 -b 参数传递 Cookie
                    // 数组越界检查
                    if (i + 1 >= tokens.length) {
                        break;
                    }
                    String cookieValue = tokens[++i];
                    // 清理 Cookie 值：去除引号和换行符
                    if (cookieValue != null && !cookieValue.isEmpty()) {
                        // 去除首尾引号（如果有）
                        if ((cookieValue.startsWith("\"") && cookieValue.endsWith("\"")) ||
                            (cookieValue.startsWith("'") && cookieValue.endsWith("'"))) {
                            cookieValue = cookieValue.substring(1, cookieValue.length() - 1);
                        }
                        // 去除换行符和多余空格
                        cookieValue = cookieValue.replaceAll("\\s+", " ").trim();
                        request.cookie = cookieValue;
                    }
                    break;
                case "-k":
                case "--insecure":
                    request.insecure = true;
                    break;
                default:
                    if (token.startsWith("http://") || token.startsWith("https://")) {
                        request.url = token;
                    }
                    break;
            }
        }

        // 确保默认方法正确（如果有 body 则用 POST）
        if (request.method == null) {
            request.method = (request.body != null) ? "POST" : "GET";
        }
        return request;
    }

    private static String[] tokenizeCurlCommand(String curlCommand) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = '"';

        for (char c : curlCommand.toCharArray()) {
            if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else if ((c == '"' || c == '\'') && (!inQuotes || c == quoteChar)) {
                inQuotes = !inQuotes;
                quoteChar = c;
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) tokens.add(current.toString());

        // 移除开头的 "curl" 命令
        if (!tokens.isEmpty() && tokens.get(0).equals("curl")) {
            tokens.remove(0);
        }
        return tokens.toArray(new String[0]);
    }

    /**
     * 获取或创建 OkHttpClient 实例（带缓存）
     *
     * @param insecure
     *                 是否启用不安全模式（跳过SSL证书验证）
     *                 警告：仅用于开发/测试环境，生产环境禁用！
     *                 此模式会完全禁用SSL证书验证，存在严重安全风险：
     *                 - 可能遭受中间人攻击
     *                 - 无法识别伪造或过期的证书
     *                 - 传输的数据可能被窃听或篡改
     * @return 配置好的 OkHttpClient 实例
     * @throws IllegalStateException
     *                 当SSL上下文初始化失败时抛出
     */
    private static OkHttpClient getOrCreateOkHttpClient(boolean insecure) {
        return CLIENT_CACHE.computeIfAbsent(insecure, key ->
                buildOkHttpClient(key, DEFAULT_CONNECT_TIMEOUT_SECONDS, DEFAULT_READ_TIMEOUT_SECONDS)
        );
    }

    /**
     * 构建 OkHttpClient 实例
     *
     * @param insecure
     *                 是否启用不安全模式（跳过SSL证书验证）
     *                 警告：仅用于开发/测试环境，生产环境禁用！
     *                 此模式会完全禁用SSL证书验证，存在严重安全风险：
     *                 - 可能遭受中间人攻击
     *                 - 无法识别伪造或过期的证书
     *                 - 传输的数据可能被窃听或篡改
     * @param connectTimeoutSeconds
     *                 连接超时时间（秒）
     * @param readTimeoutSeconds
     *                 读取超时时间（秒）
     * @return 配置好的 OkHttpClient 实例
     * @throws IllegalArgumentException
     *                 当超时参数无效时抛出
     * @throws IllegalStateException
     *                 当SSL上下文初始化失败时抛出
     */
    private static OkHttpClient buildOkHttpClient(boolean insecure,
                                                   int connectTimeoutSeconds,
                                                   int readTimeoutSeconds) {
        // 参数验证
        if (connectTimeoutSeconds <= 0) {
            throw new IllegalArgumentException("连接超时时间必须大于 0，当前值: " + connectTimeoutSeconds);
        }
        if (readTimeoutSeconds <= 0) {
            throw new IllegalArgumentException("读取超时时间必须大于 0，当前值: " + readTimeoutSeconds);
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS);

        if (insecure) {
            // 记录安全警告
            // System.err.println("⚠️  警告: 已启用不安全模式，SSL证书验证已被禁用！存在严重安全风险！");
            // System.err.println("⚠️  警告: 此模式仅应在开发/测试环境使用，严禁在生产环境使用！");

            try {
                // 创建信任所有证书的 TrustManager
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                                // 不进行客户端证书验证
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                                // 不进行服务器证书验证
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // 使用 TLS 协议（而不是过时的 SSL）
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // 配置不安全的 SSL Socket Factory 和 Hostname Verifier
                builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> true);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("不支持的SSL/TLS协议", e);
            } catch (KeyManagementException e) {
                throw new IllegalStateException("SSL上下文初始化失败", e);
            } catch (Exception e) {
                throw new IllegalStateException("构建不安全SSL客户端时发生未知错误", e);
            }
        }
        return builder.build();
    }

    /**
     * 构建 HTTP 请求
     *
     * @param curlRequest
     *                    curl 请求对象
     * @return 构建好的 HTTP 请求
     * @throws IllegalStateException
     *                    当 URL 为空时抛出
     */
    private static Request buildRequest(CurlRequest curlRequest) {
        // 空值检查
        if (curlRequest.url == null || curlRequest.url.trim().isEmpty()) {
            throw new IllegalStateException("URL 不能为空");
        }

        Request.Builder builder = new Request.Builder().url(curlRequest.url);

        // 添加请求头
        addHeaders(builder, curlRequest);

        // 添加基本认证
        addBasicAuth(builder, curlRequest);

        // 设置请求体和请求方法
        setRequestBody(builder, curlRequest);

        return builder.build();
    }

    /**
     * 添加请求头（包括 Cookie）
     *
     * @param builder
     *               请求构建器
     * @param curlRequest
     *                curl 请求对象
     */
    private static void addHeaders(Request.Builder builder, CurlRequest curlRequest) {
        // 确定最终的 Cookie 值（优先使用 -b 参数，如果没有则使用 Header 中的 Cookie）
        String finalCookie = determineFinalCookie(curlRequest);

        // 添加所有请求头（排除 Cookie，因为 Cookie 会单独处理）
        for (Map.Entry<String, String> entry : curlRequest.headers.entrySet()) {
            if (!HttpConstants.HEADER_COOKIE.equalsIgnoreCase(entry.getKey())) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 添加 Cookie（如果存在）
        if (finalCookie != null && !finalCookie.isEmpty()) {
            builder.addHeader(HttpConstants.HEADER_COOKIE, finalCookie);
        }
    }

    /**
     * 确定最终的 Cookie 值
     * 优先级：-b 参数 > Header 中的 Cookie
     *
     * @param curlRequest
     *                    curl 请求对象
     * @return 最终的 Cookie 值，如果不存在则返回 null
     */
    private static String determineFinalCookie(CurlRequest curlRequest) {
        // 优先使用 -b 参数中的 Cookie
        if (curlRequest.cookie != null && !curlRequest.cookie.isEmpty()) {
            return curlRequest.cookie;
        }

        // 从 headers 中查找 Cookie（不区分大小写）
        for (Map.Entry<String, String> entry : curlRequest.headers.entrySet()) {
            if (HttpConstants.HEADER_COOKIE.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * 添加基本认证请求头
     *
     * @param builder
     *               请求构建器
     * @param curlRequest
     *                curl 请求对象
     */
    private static void addBasicAuth(Request.Builder builder, CurlRequest curlRequest) {
        if (curlRequest.username != null && curlRequest.password != null) {
            String credential = Credentials.basic(curlRequest.username, curlRequest.password);
            builder.addHeader(HttpConstants.HEADER_AUTHORIZATION, credential);
        }
    }

    /**
     * 设置请求体和请求方法
     *
     * @param builder
     *               请求构建器
     * @param curlRequest
     *                curl 请求对象
     */
    private static void setRequestBody(Request.Builder builder, CurlRequest curlRequest) {
        if (curlRequest.body != null) {
            String contentType = curlRequest.headers.getOrDefault(
                    HttpConstants.HEADER_CONTENT_TYPE,
                    HttpConstants.DEFAULT_CONTENT_TYPE
            );
            RequestBody body = RequestBody.create(curlRequest.body, MediaType.parse(contentType));
            builder.method(curlRequest.method, body);
        } else {
            builder.method(curlRequest.method, null);
        }
    }

    // 内部类存储解析结果
    static class CurlRequest {
        String method;
        String url;
        String body;
        String username;
        String password;
        String cookie;
        boolean insecure;
        Map<String, String> headers = new HashMap<>();
    }
}