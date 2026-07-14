package com.augustlee.mt.toolWindow.mws.service;

import com.augustlee.mt.toolWindow.mws.dto.ClassIndexDTO;
import com.augustlee.mt.toolWindow.mws.manager.ApiDetailManager;
import com.augustlee.mt.toolWindow.mws.manager.ApiManager;
import com.augustlee.mt.toolWindow.mws.manager.GroupManager;
import com.augustlee.mt.toolWindow.mws.po.ApiDetailPO;
import com.augustlee.mt.toolWindow.mws.vo.ApiDetailVO;
import com.augustlee.mt.toolWindow.mws.vo.ApiVO;
import com.augustlee.mt.toolWindow.mws.vo.GroupVO;

import java.util.*;

/**
 * API 搜索服务
 * 提供 API 搜索的高级业务功能
 *
 * @see SearchManager
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class SearchManager {

    private final Map<Integer, ApiManager> API_MANAGER_MAP = new HashMap<>();
    private final Map<ApiDetailPO, ApiDetailManager> API_DETAIL_MANAGER_MAP = new HashMap<>();
    private final GroupManager groupManager = new GroupManager();


    /**
     * 根据 API 路径查询对应的 Java 类方法索引。
     *
     * @param path API 路径
     * @return Java 类方法索引
     * @throws RuntimeException 未找到匹配路径或调用信息时抛出
     */
    public ClassIndexDTO getClassIndex(String path) {
        List<GroupVO> groups = this.groupManager.getByCommonPrefix(path);

        Optional<ApiVO> optional = groups.stream()
                .map(GroupVO::getId)
                .map(this::getApiManager)
                .map(ApiManager::execute)
                .flatMap(Collection::stream)
                .filter(api -> path.equals(api.getPath()))
                .findFirst();
        if(optional.isEmpty()){
            throw new RuntimeException("无法找到匹配的路径， path: " + path);
        }

        ApiVO api = optional.get();
        ApiDetailPO apiDetailPO = new ApiDetailPO(api.getApiGroupName(), api.getName());

        ApiDetailManager apiDetailManager = this.getApiDetailManager(apiDetailPO);
        ApiDetailVO apiDetailVO = apiDetailManager.execute();
        List<ApiDetailVO.InvokerViewsDTO> invokerViewsDTOList = apiDetailVO.getInvokerViews();
        if(invokerViewsDTOList == null || invokerViewsDTOList.isEmpty()){
            throw new RuntimeException("无法找到匹配的路径， path: " + path);
        }
        ApiDetailVO.InvokerViewsDTO invokerViewsDTO = invokerViewsDTOList.get(0);
        return new ClassIndexDTO(invokerViewsDTO.getServiceName(), invokerViewsDTO.getMethodName());
    }

    private synchronized ApiDetailManager getApiDetailManager(ApiDetailPO apiDetailPO) {
        ApiDetailManager apiDetailManager = API_DETAIL_MANAGER_MAP.get(apiDetailPO);
        if (apiDetailManager == null) {
            apiDetailManager = new ApiDetailManager(apiDetailPO);
            API_DETAIL_MANAGER_MAP.put(apiDetailPO, apiDetailManager);
            return apiDetailManager;
        }
        return apiDetailManager;
    }


    private synchronized ApiManager getApiManager(Integer groupId) {
        ApiManager apiManager = API_MANAGER_MAP.get(groupId);
        if (apiManager == null) {
            apiManager = new ApiManager(groupId);
            API_MANAGER_MAP.put(groupId, apiManager);
            return apiManager;
        }
        return API_MANAGER_MAP.get(groupId);
    }

}

