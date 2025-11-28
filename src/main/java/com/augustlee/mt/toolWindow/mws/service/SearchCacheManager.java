package com.augustlee.mt.toolWindow.mws.service;

import com.alibaba.fastjson.JSON;
import com.augustlee.mt.toolWindow.common.state.ApiPathState;
import com.augustlee.mt.toolWindow.mws.dto.ApiIndexDTO;
import com.augustlee.mt.toolWindow.mws.dto.ClassIndexDTO;
import com.augustlee.mt.toolWindow.mws.enums.GroupEnum;
import com.augustlee.mt.toolWindow.mws.manager.ApiDetailManager;
import com.augustlee.mt.toolWindow.mws.manager.ApiManager;
import com.augustlee.mt.toolWindow.mws.po.ApiDetailPO;
import com.augustlee.mt.toolWindow.mws.vo.ApiDetailVO;

import java.util.*;

/**
 * API 缓存搜索服务
 * 提供带缓存功能的 API 搜索服务
 *
 * @see SearchCacheManager
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class SearchCacheManager {

    private final Map<Integer, ApiManager> API_MANAGER_MAP = new HashMap<>();
    private final Map<ApiDetailPO, ApiDetailManager> API_DETAIL_MANAGER_MAP = new HashMap<>();

    private final static Map<String, ClassIndexDTO> CLASS_INDEX_MAP = new HashMap<>();


    private ApiPathState apiPathState;

    public SearchCacheManager(ApiPathState apiPathState) {
        this.apiPathState = apiPathState;
        init();
    }

    public ClassIndexDTO getClassIndex(String path) {
        return CLASS_INDEX_MAP.get(path);
    }

    public void refresh(){
        List<ApiIndexDTO> list = Arrays.stream(GroupEnum.values())
                .map(GroupEnum::getId)
                .map(this::getApiManager)
                .map(ApiManager::execute)
                .flatMap(Collection::stream)
                .map(apiVO -> new ApiDetailPO(apiVO.getApiGroupName(), apiVO.getName()))
                .map(this::getApiDetailManager)
                .map(ApiDetailManager::execute)
                .filter(Objects::nonNull)
                .parallel()
                .map(apiDetailVO -> {
                    List<ApiDetailVO.InvokerViewsDTO> invokerViewsDTOList = apiDetailVO.getInvokerViews();
                    if(invokerViewsDTOList == null || invokerViewsDTOList.isEmpty()){
                        return null;
                    }
                    ApiDetailVO.InvokerViewsDTO invokerViewsDTO = invokerViewsDTOList.get(0);
                    ApiIndexDTO apiIndexDTO = new ApiIndexDTO(apiDetailVO.getPath(), invokerViewsDTO.getServiceName(), invokerViewsDTO.getMethodName());
                    System.out.println("  ===> " + JSON.toJSONString(apiIndexDTO));
                    return apiIndexDTO;
                })
                .filter(Objects::nonNull)
                .toList();

        this.refreshClassIndex(list);
        apiPathState.setApiPathJson(JSON.toJSONString(list));
    }

    public int getApiCount(){
        return CLASS_INDEX_MAP.size();
    }





    private void init(){
        if (apiPathState == null) {
            return;
        }
        String apiPathJson = apiPathState.getApiPathJson();
        if (apiPathJson == null || apiPathJson.isEmpty()) {
            return;
        }
        List<ApiIndexDTO> list = JSON.parseArray(apiPathJson, ApiIndexDTO.class);
        this.refreshClassIndex(list);
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

    private void refreshClassIndex(List<ApiIndexDTO> list){
        CLASS_INDEX_MAP.clear();

        if(list == null || list.isEmpty()){
            return;
        }
        list.forEach(apiIndexDTO -> CLASS_INDEX_MAP.put(apiIndexDTO.getPath(), apiIndexDTO));
    }





}

