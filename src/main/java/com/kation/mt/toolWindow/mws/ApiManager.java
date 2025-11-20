package com.kation.mt.toolWindow.mws;

import com.alibaba.fastjson.JSONArray;
import com.kation.mt.toolWindow.mws.vo.ApiVO;

import java.util.List;
import java.util.Random;

public class ApiManager extends AbsCatchCurlCommand<List<ApiVO>> {

    private static final String CURL_TEMP = "curl 'https://shepherd.mws-test.sankuai.com/spapi/v1/apis/{{GROUP_ID}}' \\\n" +
            "  -H 'accept: application/json, text/plain, */*' \\\n" +
            "  -H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8' \\\n" +
            "  -H 'cache-control: no-cache' \\\n" +
            "  -H 'm-appkey: fe_mws-shepherd-fe' \\\n" +
            "  -H 'm-traceid: {{TRACE_ID}}' \\\n" +
            "  -H 'pragma: no-cache' \\\n" +
            "  -H 'priority: u=1, i' \\\n" +
            "  -H 'referer: https://shepherd.mws-test.sankuai.com/api-group-detail?api_group_name=sgb2b_seashop_mall_api_2&api_group_id=50256&group_tab=api-manage' \\\n" +
            "  -H 'sec-ch-ua: \"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Google Chrome\";v=\"134\"' \\\n" +
            "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
            "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
            "  -H 'sec-fetch-dest: empty' \\\n" +
            "  -H 'sec-fetch-mode: cors' \\\n" +
            "  -H 'sec-fetch-site: same-origin' \\\n" +
            "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36' \\\n" +
            "  -H 'x-requested-with: XMLHttpRequest'";

    private Integer id;

    public ApiManager(Integer id){
        this.id = id;
    }


    @Override
    public List<ApiVO> convertResp(String responseBody) {
        JSONArray jsonArray = (JSONArray)super.getJsonArrayResp(responseBody);
        return jsonArray.toJavaList(ApiVO.class);
    }

    @Override
    public String getCurlCommand() {
        return CURL_TEMP.replace("{{TRACE_ID}}", String.valueOf(new Random().nextLong()))
                .replace("{{GROUP_ID}}", String.valueOf(id));
    }

    @Override
    public int getCacheMS() {
        return 1000 * 60 * 60;
    }
}
