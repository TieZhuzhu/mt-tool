package com.kation.mt.toolWindow.mws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kation.mt.toolWindow.tool.Config;
import com.kation.mt.toolWindow.tool.CurlToOkHttp;

import java.io.IOException;

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
