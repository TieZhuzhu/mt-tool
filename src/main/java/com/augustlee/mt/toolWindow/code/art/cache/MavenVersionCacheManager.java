package com.augustlee.mt.toolWindow.code.art.cache;

import com.augustlee.mt.toolWindow.code.art.vo.MavenVersionVO;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Maven版本查询缓存管理器
 *
 * @see MavenVersionCacheManager
 * @author August Lee
 * @since 2025/11/21 17:26
 */
public class MavenVersionCacheManager {

    private final MavenVersionCacheState cacheState;

    public MavenVersionCacheManager() {
        this.cacheState = ApplicationManager.getApplication().getService(MavenVersionCacheState.class);
    }

    /**
     * 生成缓存 Key
     */
    private String generateCacheKey(String componentName, int pageNum, int pageSize) {
        return componentName + ":" + pageNum + ":" + pageSize;
    }

    /**
     * 保存缓存
     */
    public void saveCache(String componentName, int pageNum, int pageSize, MavenVersionVO data) {
        try {
            String cacheKey = generateCacheKey(componentName, pageNum, pageSize);
            String jsonData = com.alibaba.fastjson.JSON.toJSONString(data);
            long timestamp = System.currentTimeMillis();

            Map<String, MavenVersionCacheEntry> cacheData = cacheState.getCacheData();
            
            // 如果已存在，先移除（保证新数据在最后，方便LRU清理）
            cacheData.remove(cacheKey);
            
            // 添加新缓存
            cacheData.put(cacheKey, new MavenVersionCacheEntry(
                    jsonData, timestamp, componentName, pageNum, pageSize
            ));

            // 清理超量缓存（LRU策略：删除最旧的）
            cleanupExcessCache();

            // 保存状态
            cacheState.loadState(cacheState);
        } catch (Exception e) {
            // 缓存失败不影响主流程，静默处理
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存
     */
    @Nullable
    public MavenVersionCacheResult getCache(String componentName, int pageNum, int pageSize) {
        try {
            String cacheKey = generateCacheKey(componentName, pageNum, pageSize);
            Map<String, MavenVersionCacheEntry> cacheData = cacheState.getCacheData();
            MavenVersionCacheEntry entry = cacheData.get(cacheKey);

            if (entry != null && entry.getJsonData() != null) {
                MavenVersionVO data = com.alibaba.fastjson.JSON.parseObject(
                        entry.getJsonData(), MavenVersionVO.class);
                return new MavenVersionCacheResult(data, entry.getTimestamp());
            }
        } catch (Exception e) {
            // 缓存解析失败，返回null
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清理超量缓存（LRU策略）
     */
    private void cleanupExcessCache() {
        Map<String, MavenVersionCacheEntry> cacheData = cacheState.getCacheData();
        int maxSize = cacheState.getMaxCacheSize();

        if (cacheData.size() > maxSize) {
            // 按时间戳排序，删除最旧的
            List<Map.Entry<String, MavenVersionCacheEntry>> entries = 
                    new ArrayList<>(cacheData.entrySet());
            entries.sort(Comparator.comparingLong(e -> e.getValue().getTimestamp()));

            // 删除最旧的条目
            int toRemove = cacheData.size() - maxSize;
            for (int i = 0; i < toRemove; i++) {
                cacheData.remove(entries.get(i).getKey());
            }
        }
    }

    /**
     * 格式化缓存时间
     */
    public static String formatCacheTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    /**
     * 获取缓存配置的最大条目数
     */
    public int getMaxCacheSize() {
        return cacheState.getMaxCacheSize();
    }

    /**
     * 设置缓存配置的最大条目数
     */
    public void setMaxCacheSize(int maxSize) {
        cacheState.setMaxCacheSize(maxSize);
    }
}

