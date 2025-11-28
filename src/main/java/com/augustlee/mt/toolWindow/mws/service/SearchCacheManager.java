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
import com.augustlee.mt.toolWindow.mws.vo.ApiVO;
import com.intellij.openapi.application.ApplicationManager;
import com.augustlee.mt.toolWindow.common.log.ConsoleLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API 缓存搜索服务
 * 提供带缓存功能的 API 搜索服务
 *
 * @see SearchCacheManager
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class SearchCacheManager {

    private static final ConsoleLogger LOG = ConsoleLogger.getInstance(SearchCacheManager.class);

    private final Map<Integer, ApiManager> API_MANAGER_MAP = new ConcurrentHashMap<>();
    private final Map<ApiDetailPO, ApiDetailManager> API_DETAIL_MANAGER_MAP = new ConcurrentHashMap<>();

    private final static Map<String, ClassIndexDTO> CLASS_INDEX_MAP = new ConcurrentHashMap<>();

    private final ApiPathState apiPathState;

    /**
     * 使用 IntelliJ Platform 的线程池作为 Executor
     * 这样可以确保在正确的类加载器上下文中执行
     */
    private final Executor intelliJExecutor = runnable ->
            ApplicationManager.getApplication().executeOnPooledThread(runnable);


    public SearchCacheManager(ApiPathState apiPathState) {
        this.apiPathState = apiPathState;
        init();
    }

    public ClassIndexDTO getClassIndex(String path) {
        if (path == null || path.isEmpty()) {
            LOG.warn("搜索路径为空");
            return null;
        }
        ClassIndexDTO result = CLASS_INDEX_MAP.get(path);
        if (result == null) {
            LOG.debug("未找到路径: " + path + "，当前缓存大小: " + CLASS_INDEX_MAP.size());
            // 输出前10个路径作为参考
            if (!CLASS_INDEX_MAP.isEmpty()) {
                LOG.debug("缓存中的前10个路径示例:");
                CLASS_INDEX_MAP.keySet().stream()
                        .limit(10)
                        .forEach(key -> LOG.debug("  - " + key));
            }
        }
        return result;
    }

    public void refresh(){
        LOG.info("========== SearchCacheManager.refresh() 被调用 ==========");
        long startTime = System.currentTimeMillis();

        // 第一步：并行获取所有 Group 的 API 列表
        List<ApiVO> allApis = fetchAllGroupApis(startTime);

        // 第二步：并行获取所有 API 的详情
        List<ApiIndexDTO> detailResults = fetchAllApiDetails(allApis, startTime);

        // 保存结果
        saveResults(detailResults, startTime);
    }

    /**
     * 并行获取所有 Group 的 API 列表
     * 使用 CompletableFuture 实现更优雅的并行处理
     */
    private List<ApiVO> fetchAllGroupApis(long startTime) {
        LOG.info("第一步：开始并行获取所有 Group 的 API 列表，共 " + GroupEnum.values().length + " 个 Group");

        GroupEnum[] groups = GroupEnum.values();

        // 为每个 Group 创建异步任务
        List<CompletableFuture<List<ApiVO>>> futures = new ArrayList<>();
        for (GroupEnum group : groups) {
            final int groupId = group.getId();
            CompletableFuture<List<ApiVO>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    ApiManager apiManager = getApiManager(groupId);
                    List<ApiVO> apis = apiManager.execute();
                    LOG.debug("Group " + groupId + " 获取到 " + (apis != null ? apis.size() : 0) + " 个 API");
                    return apis != null ? apis : new ArrayList<>();
                } catch (Exception e) {
                    LOG.warn("获取 Group " + groupId + " 的 API 列表失败", e);
                    return new ArrayList<>();
                }
            }, intelliJExecutor);
            futures.add(future);
        }

        // 等待所有任务完成并收集结果
        CompletableFuture<List<List<ApiVO>>> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .toList());

        List<ApiVO> allApis = allFutures.join().stream()
                .flatMap(Collection::stream)
                .toList();

        LOG.info("第一步完成：共获取到 " + allApis.size() + " 个 API");
        return allApis;
    }

    /**
     * 并行获取所有 API 的详情
     * 使用 CompletableFuture 实现更优雅的并行处理
     */
    private List<ApiIndexDTO> fetchAllApiDetails(List<ApiVO> allApis, long startTime) {
        LOG.info("第二步：开始并行获取所有 API 的详情，共 " + allApis.size() + " 个 API");

        AtomicInteger processedCount = new AtomicInteger(0);

        // 为每个 API 创建异步任务
        List<CompletableFuture<ApiIndexDTO>> futures = allApis.stream()
                .map(apiVO -> {
                    ApiDetailPO apiDetailPO = new ApiDetailPO(apiVO.getApiGroupName(), apiVO.getName());
                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            return processApiDetail(apiDetailPO, processedCount, allApis.size());
                        } catch (Exception e) {
                            LOG.warn("获取 API 详情失败: " + apiDetailPO.getGroupName() + "/" + apiDetailPO.getApiName(), e);
                            return null;
                        }
                    }, intelliJExecutor);
                })
                .toList();

        // 等待所有任务完成并收集结果
        CompletableFuture<List<ApiIndexDTO>> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList());

        List<ApiIndexDTO> detailResults = allFutures.join();

        long endTime = System.currentTimeMillis();
        LOG.info("第二步完成：共处理 " + detailResults.size() + " 个有效的 API 详情，耗时 " + (endTime - startTime) + " ms");
        return detailResults;
    }

    /**
     * 处理单个 API 详情
     */
    private ApiIndexDTO processApiDetail(ApiDetailPO apiDetailPO, AtomicInteger processedCount, int totalSize) {
        ApiDetailManager apiDetailManager = getApiDetailManager(apiDetailPO);
        ApiDetailVO apiDetailVO = apiDetailManager.execute();

        if (apiDetailVO == null) {
            return null;
        }

        List<ApiDetailVO.InvokerViewsDTO> invokerViewsDTOList = apiDetailVO.getInvokerViews();
        if (invokerViewsDTOList == null || invokerViewsDTOList.isEmpty()) {
            return null;
        }

        ApiDetailVO.InvokerViewsDTO invokerViewsDTO = invokerViewsDTOList.get(0);
        ApiIndexDTO apiIndexDTO = new ApiIndexDTO(
                apiDetailVO.getPath(),
                invokerViewsDTO.getServiceName(),
                invokerViewsDTO.getMethodName()
        );

        int count = processedCount.incrementAndGet();
        if (count % 10 == 0 || count == totalSize) {
            LOG.debug("已处理 " + count + "/" + totalSize + " 个 API");
        }

        LOG.debug("  ===> " + JSON.toJSONString(apiIndexDTO));
        return apiIndexDTO;
    }

    /**
     * 保存结果
     */
    private void saveResults(List<ApiIndexDTO> detailResults, long startTime) {
        this.refreshClassIndex(detailResults);
        if (apiPathState != null) {
            apiPathState.setApiPathJson(JSON.toJSONString(detailResults));
        }

        long endTime = System.currentTimeMillis();
        LOG.info("API 缓存刷新完成，共缓存 " + detailResults.size() + " 个 API，总耗时 " + (endTime - startTime) + " ms");
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

    private ApiDetailManager getApiDetailManager(ApiDetailPO apiDetailPO) {
        return API_DETAIL_MANAGER_MAP.computeIfAbsent(apiDetailPO, ApiDetailManager::new);
    }

    private ApiManager getApiManager(Integer groupId) {
        return API_MANAGER_MAP.computeIfAbsent(groupId, ApiManager::new);
    }

    private void refreshClassIndex(List<ApiIndexDTO> list){
        CLASS_INDEX_MAP.clear();

        if(list == null || list.isEmpty()){
            return;
        }
        list.forEach(apiIndexDTO -> CLASS_INDEX_MAP.put(apiIndexDTO.getPath(), apiIndexDTO));
    }

}

