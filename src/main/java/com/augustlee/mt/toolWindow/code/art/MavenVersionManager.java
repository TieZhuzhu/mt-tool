package com.augustlee.mt.toolWindow.code.art;

import com.augustlee.mt.toolWindow.code.art.vo.MavenVersionVO;
import com.augustlee.mt.toolWindow.common.command.AbsCatchCurlCommand;
import com.augustlee.mt.toolWindow.common.util.CurlToOkHttp;

import java.io.IOException;

/**
 * Maven版本列表管理器
 *
 * @see MavenVersionManager
 * @author August Lee
 * @since 2025/11/21 17:26
 */
public class MavenVersionManager extends AbsCatchCurlCommand<MavenVersionVO> {

    private static final String CURL_TEMP = "curl \"https://dev.sankuai.com/v1/component/mtart/version/list?pageNum={{PAGE_NUM}}&pageSize={{PAGE_SIZE}}&componentName={{COMPONENT_NAME}}&componentTool=maven\" \\\n" +
            "  -H \"Accept: application/json, text/plain, */*\" \\\n" +
            "  -H \"Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7\" \\\n" +
            "  -H \"Connection: keep-alive\" \\\n" +
            "  -b \"{{COOKIE}}\" \\\n" +
            "  -H \"Referer: https://dev.sankuai.com/art/repo/Maven/detail/{{COMPONENT_NAME}}/version\" \\\n" +
            "  -H \"Sec-Fetch-Dest: empty\" \\\n" +
            "  -H \"Sec-Fetch-Mode: cors\" \\\n" +
            "  -H \"Sec-Fetch-Site: same-origin\" \\\n" +
            "  -H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36\" \\\n" +
            "  -H \"X-Requested-With: XMLHttpRequest\" \\\n" +
            "  -H \"sec-ch-ua: \\\"Not(A:Brand\\\";v=\\\"8\\\", \\\"Chromium\\\";v=\\\"144\\\", \\\"Microsoft Edge\\\";v=\\\"144\\\"\" \\\n" +
            "  -H \"sec-ch-ua-mobile: ?0\" \\\n" +
            "  -H \"sec-ch-ua-platform: \\\"Windows\\\"\"";

    private Integer pageNum;
    private Integer pageSize;
    private String componentName;
    private String cookie;

    public MavenVersionManager(Integer pageNum, Integer pageSize, String componentName, String cookie) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.componentName = componentName;
        // 清理 Cookie：去除换行符和多余空格，确保是单行字符串
        this.cookie = cookie != null ? cookie.trim().replaceAll("\\s+", " ") : "";
    }

    @Override
    public MavenVersionVO convertResp(String responseBody) {
        // 检查响应是否是 HTML 错误页面
        if (responseBody != null && (responseBody.trim().startsWith("<html") || responseBody.trim().startsWith("<!DOCTYPE"))) {
            // 提取错误信息
            String errorMessage = extractErrorMessageFromHtml(responseBody);
            throw new RuntimeException("服务器返回错误: " + errorMessage);
        }
        
        // 直接解析整个响应对象，因为需要包含 code、data、message
        try {
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseBody);
            Integer code = jsonObject.getInteger("code");
            if (code != null && code != 0) {
                throw new RuntimeException("code not 0, resp: " + responseBody);
            }
            return jsonObject.toJavaObject(MavenVersionVO.class);
        } catch (com.alibaba.fastjson.JSONException e) {
            // 如果解析失败，可能是返回了 HTML 或其他格式
            if (responseBody != null && responseBody.contains("400") && responseBody.contains("Cookie Too Large")) {
                throw new RuntimeException("Cookie 太大，超过了服务器限制。请检查 Cookie 内容，可能需要清除一些不必要的 Cookie。");
            }
            throw new RuntimeException("响应解析失败，服务器可能返回了错误页面: " + e.getMessage());
        }
    }

    /**
     * 从 HTML 错误页面中提取错误信息
     */
    private String extractErrorMessageFromHtml(String html) {
        if (html == null) {
            return "未知错误";
        }
        
        // 提取 title 标签中的错误信息
        if (html.contains("<title>")) {
            int titleStart = html.indexOf("<title>") + 7;
            int titleEnd = html.indexOf("</title>", titleStart);
            if (titleEnd > titleStart) {
                String title = html.substring(titleStart, titleEnd).trim();
                if (!title.isEmpty()) {
                    return title;
                }
            }
        }
        
        // 提取 h1 标签中的错误信息
        if (html.contains("<h1>")) {
            int h1Start = html.indexOf("<h1>") + 4;
            int h1End = html.indexOf("</h1>", h1Start);
            if (h1End > h1Start) {
                String h1 = html.substring(h1Start, h1End).trim();
                if (!h1.isEmpty()) {
                    return h1;
                }
            }
        }
        
        // 提取 center 标签中的错误信息
        if (html.contains("<center>")) {
            int centerStart = html.indexOf("<center>") + 8;
            int centerEnd = html.indexOf("</center>", centerStart);
            if (centerEnd > centerStart) {
                String center = html.substring(centerStart, centerEnd).trim();
                if (!center.isEmpty() && !center.equals("openresty")) {
                    return center;
                }
            }
        }
        
        return "服务器返回了错误页面";
    }

    @Override
    public String getCurlCommand() {
        String curl = CURL_TEMP
                .replace("{{PAGE_NUM}}", String.valueOf(pageNum))
                .replace("{{PAGE_SIZE}}", String.valueOf(pageSize))
                .replace("{{COMPONENT_NAME}}", componentName);
        // 替换 Cookie 占位符
        curl = curl.replace("{{COOKIE}}", cookie);
        return curl;
    }

    /**
     * 获取原始响应字符串（用于调试和查看）
     */
    public String getRawResponse() throws IOException {
        String curl = this.getCurlCommand();
        return CurlToOkHttp.executeCurl(curl);
    }

    @Override
    public MavenVersionVO execute() {
        // 重写 execute 方法，使用 -b 参数而不是父类的 -H 'Cookie:' 方式
        String curl = this.getCurlCommand();
        try {
            return this.convertResp(CurlToOkHttp.executeCurl(curl));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int getCacheMS() {
        return 1000 * 60 * 60; // 1小时缓存
    }
}

