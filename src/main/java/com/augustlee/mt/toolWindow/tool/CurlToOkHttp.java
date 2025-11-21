package com.augustlee.mt.toolWindow.tool;

import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class CurlToOkHttp {



    public static String executeCurl(String curlCommand) throws IOException {
        // 解析 curl 命令
        CurlRequest curlRequest = parseCurl(curlCommand);

        // 构建 OkHttpClient（支持忽略 SSL 证书验证）
        OkHttpClient client = buildOkHttpClient(curlRequest.insecure);

        // 构建请求
        Request request = buildRequest(curlRequest);

        ResponseBody responseBody = client.newCall(request).execute().body();

        if (responseBody == null) {
            return null;
        }

        // 发送请求并返回响应
        return responseBody.string();
    }

    private static CurlRequest parseCurl(String curlCommand) {
        CurlRequest request = new CurlRequest();
        String[] tokens = tokenizeCurlCommand(curlCommand);

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            switch (token) {
                case "-X":
                case "--request":
                    request.method = tokens[++i];
                    break;
                case "-H":
                case "--header":
                    String[] header = tokens[++i].split(":", 2);
                    if (header.length == 2) {
                        request.headers.put(header[0].trim(), header[1].trim());
                    }
                    break;
                case "-d":
                case "--data":
                case "--data-ascii":
                case "--data-raw":
                    request.body = tokens[++i];
                    break;
                case "-u":
                case "--user":
                    String[] creds = tokens[++i].split(":", 2);
                    if (creds.length == 2) {
                        request.username = creds[0];
                        request.password = creds[1];
                    }
                    break;
                case "-b":
                case "--cookie":
                    // 支持 -b 参数传递 Cookie
                    if (i + 1 < tokens.length) {
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

    private static OkHttpClient buildOkHttpClient(boolean insecure) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        if (insecure) {
            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return builder.build();
    }

    private static Request buildRequest(CurlRequest curlRequest) {
        Request.Builder builder = new Request.Builder().url(curlRequest.url);

        // 添加请求头（但排除 Cookie，因为 Cookie 会通过 -b 参数单独处理）
        for (Map.Entry<String, String> entry : curlRequest.headers.entrySet()) {
            // 跳过 Cookie header，避免重复添加
            if (!"Cookie".equalsIgnoreCase(entry.getKey())) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 处理 Cookie（优先使用 -b 参数，如果没有则使用 Header 中的 Cookie）
        if (curlRequest.cookie != null && !curlRequest.cookie.isEmpty()) {
            builder.addHeader("Cookie", curlRequest.cookie);
        } else if (curlRequest.headers.containsKey("Cookie")) {
            // 如果 -b 参数没有 Cookie，但 Header 中有，则使用 Header 中的
            builder.addHeader("Cookie", curlRequest.headers.get("Cookie"));
        }

        // 基本认证
        if (curlRequest.username != null && curlRequest.password != null) {
            String credential = Credentials.basic(curlRequest.username, curlRequest.password);
            builder.addHeader("Authorization", credential);
        }

        // 请求体处理
        if (curlRequest.body != null) {
            String contentType = curlRequest.headers.getOrDefault("Content-Type", "application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(curlRequest.body, MediaType.parse(contentType));
            builder.method(curlRequest.method, body);
        } else {
            builder.method(curlRequest.method, null);
        }

        return builder.build();
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