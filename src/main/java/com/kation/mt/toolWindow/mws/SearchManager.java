package com.kation.mt.toolWindow.mws;


import com.kation.mt.toolWindow.mws.dto.ClassIndexDTO;
import com.kation.mt.toolWindow.mws.enums.GroupEnum;
import com.kation.mt.toolWindow.mws.po.ApiDetailPO;
import com.kation.mt.toolWindow.mws.vo.ApiDetailVO;
import com.kation.mt.toolWindow.mws.vo.ApiVO;

import java.util.*;

public class SearchManager {

    private final Map<Integer, ApiManager> API_MANAGER_MAP = new HashMap<>();
    private final Map<ApiDetailPO, ApiDetailManager> API_DETAIL_MANAGER_MAP = new HashMap<>();



    public ClassIndexDTO getClassIndex(String path) {
        List<GroupEnum> groupEnums = GroupEnum.getByCommonPrefix(path);

        Optional<ApiVO> optional = groupEnums.stream()
                .map(GroupEnum::getId)
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
