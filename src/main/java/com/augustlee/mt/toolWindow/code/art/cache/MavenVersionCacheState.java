package com.augustlee.mt.toolWindow.code.art.cache;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maven版本查询缓存状态
 *
 * @see MavenVersionCacheState
 * @author August Lee
 * @since 2025/11/21 17:26
 */
@State(name = "MavenVersionCacheState", storages = @Storage("mavenVersionCacheState.xml"))
public class MavenVersionCacheState implements PersistentStateComponent<MavenVersionCacheState> {

    /**
     * 缓存数据：key = componentName:pageNum:pageSize, value = JSON字符串
     */
    private Map<String, MavenVersionCacheEntry> cacheData = new LinkedHashMap<>();

    /**
     * 缓存条目数量限制（可配置）
     */
    private int maxCacheSize = 100;

    @Nullable
    @Override
    public MavenVersionCacheState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MavenVersionCacheState state) {
        this.cacheData = state.cacheData != null ? state.cacheData : new LinkedHashMap<>();
        this.maxCacheSize = state.maxCacheSize > 0 ? state.maxCacheSize : 100;
    }

    public Map<String, MavenVersionCacheEntry> getCacheData() {
        return cacheData;
    }

    public void setCacheData(Map<String, MavenVersionCacheEntry> cacheData) {
        this.cacheData = cacheData;
    }

    public int getMaxCacheSize() {
        return maxCacheSize > 0 ? maxCacheSize : 100;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize > 0 ? maxCacheSize : 100;
    }
}

