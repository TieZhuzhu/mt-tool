package com.augustlee.mt.toolWindow.common.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augustlee.mt.toolWindow.common.util.Config;
import com.augustlee.mt.toolWindow.common.util.CurlToOkHttp;

import java.io.IOException;

/**
 * Curl 命令执行抽象基类
 * 提供执行 curl 命令的通用功能，包括响应解析
 *
 * @see AbsCurlCommand
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public abstract class AbsCurlCommand<RESP> {


    public RESP execute() {
        String curl = this.getCurlCommand();
        curl += "  -H 'Cookie: "+ Config.COOKIE_NAME +"'";
        try {
            return this.convertResp(CurlToOkHttp.executeCurl(curl));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JSON getJsonArrayResp(String responseBody) {
        JSONObject jsonObject = JSONObject.parseObject(responseBody);
        Integer code = jsonObject.getInteger("code");
        if(code != 0){
            throw new RuntimeException("code not 0, resp: " + responseBody);
        }
        Object data = jsonObject.get("data");
        if (data instanceof JSONArray) {
            return jsonObject.getJSONArray("data");
        } else if (data instanceof JSONObject) {
            return jsonObject.getJSONObject("data");
        }
        return null;
    }

    public abstract RESP convertResp(String responseBody);

    public abstract String getCurlCommand();



}

