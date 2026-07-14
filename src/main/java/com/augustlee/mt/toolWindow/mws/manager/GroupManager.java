package com.augustlee.mt.toolWindow.mws.manager;

import com.alibaba.fastjson.JSONArray;
import com.augustlee.mt.toolWindow.common.command.AbsCatchCurlCommand;
import com.augustlee.mt.toolWindow.common.log.ConsoleLogger;
import com.augustlee.mt.toolWindow.mws.enums.GroupEnum;
import com.augustlee.mt.toolWindow.mws.vo.GroupVO;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 分组管理器
 * 负责管理 API 分组信息的获取
 *
 * @see GroupManager
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class GroupManager extends AbsCatchCurlCommand<List<GroupVO>> {

    private static final ConsoleLogger LOG = ConsoleLogger.getInstance(GroupManager.class);

    private static final String CURL_TEMP = "curl 'https://shepherd.mws-test.sankuai.com/spapi/v1/groups/list' \\\n" +
            "  -H 'accept: application/json, text/plain, */*' \\\n" +
            "  -H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8' \\\n" +
            "  -H 'cache-control: no-cache' \\\n" +
            "  -H 'm-appkey: fe_mws-shepherd-fe' \\\n" +
            "  -H 'm-traceid: {{TRACE_ID}}' \\\n" +
            "  -H 'pragma: no-cache' \\\n" +
            "  -H 'priority: u=1, i' \\\n" +
            "  -H 'referer: https://shepherd.mws-test.sankuai.com/api-group-manage' \\\n" +
            "  -H 'sec-ch-ua: \"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Google Chrome\";v=\"134\"' \\\n" +
            "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
            "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
            "  -H 'sec-fetch-dest: empty' \\\n" +
            "  -H 'sec-fetch-mode: cors' \\\n" +
            "  -H 'sec-fetch-site: same-origin' \\\n" +
            "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36' \\\n" +
            "  -H 'x-requested-with: XMLHttpRequest'";

    /**
     * 获取可用的 API 分组，在线接口不可用时回退到本地枚举。
     *
     * @return 可用的 API 分组列表
     */
    public List<GroupVO> getAvailableGroups() {
        try {
            List<GroupVO> onlineGroups = this.execute();
            if (onlineGroups != null) {
                List<GroupVO> validGroups = onlineGroups.stream()
                        .filter(this::isValidGroup)
                        .toList();
                if (!validGroups.isEmpty()) {
                    return validGroups;
                }
            }
            LOG.warn("在线 API 分组为空，回退到本地 GroupEnum");
        } catch (RuntimeException exception) {
            LOG.warn("获取在线 API 分组失败，回退到本地 GroupEnum", exception);
        }
        return this.getFallbackGroups();
    }

    /**
     * 根据 API 路径获取匹配的可用分组。
     *
     * @param path API 路径
     * @return commonPrefix 与路径匹配的分组列表
     */
    public List<GroupVO> getByCommonPrefix(String path) {
        if (path == null || path.isEmpty()) {
            return List.of();
        }
        return this.getAvailableGroups().stream()
                .filter(group -> path.startsWith(group.getCommonPrefix()))
                .toList();
    }

    @Override
    public List<GroupVO> convertResp(String responseBody) {
        JSONArray jsonArray = (JSONArray)super.getJsonArrayResp(responseBody);
        return jsonArray.toJavaList(GroupVO.class);
    }

    @Override
    public String getCurlCommand() {
        return CURL_TEMP.replace("{{TRACE_ID}}", String.valueOf(new Random().nextLong()));
    }

    @Override
    public int getCacheMS() {
        return 1000 * 60 * 60;
    }

    /**
     * 校验在线分组是否包含搜索所需的关键字段。
     *
     * @param group API 分组
     * @return id 和 commonPrefix 均有效时返回 true
     */
    private boolean isValidGroup(GroupVO group) {
        return group != null
                && group.getId() != null
                && group.getCommonPrefix() != null
                && !group.getCommonPrefix().isEmpty();
    }

    /**
     * 将本地枚举转换为统一的分组对象。
     *
     * @return 本地兜底分组列表
     */
    private List<GroupVO> getFallbackGroups() {
        return Arrays.stream(GroupEnum.values())
                .map(this::convertGroup)
                .toList();
    }

    /**
     * 将本地枚举项转换为分组对象。
     *
     * @param groupEnum 本地分组枚举
     * @return 分组对象
     */
    private GroupVO convertGroup(GroupEnum groupEnum) {
        GroupVO group = new GroupVO();
        group.setId(groupEnum.getId());
        group.setName(groupEnum.getName());
        group.setCommonPrefix(groupEnum.getCommonPrefix());
        group.setDescription(groupEnum.getDescription());
        return group;
    }
}

