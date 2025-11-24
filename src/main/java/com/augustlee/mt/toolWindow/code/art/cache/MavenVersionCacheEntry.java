package com.augustlee.mt.toolWindow.code.art.cache;

/**
 * Maven版本缓存条目
 *
 * @see MavenVersionCacheEntry
 * @author August Lee
 * @since 2025/11/21 17:26
 */
public class MavenVersionCacheEntry {
    private String jsonData;
    private long timestamp;
    private String componentName;
    private int pageNum;
    private int pageSize;

    public MavenVersionCacheEntry() {
    }

    public MavenVersionCacheEntry(String jsonData, long timestamp, String componentName, int pageNum, int pageSize) {
        this.jsonData = jsonData;
        this.timestamp = timestamp;
        this.componentName = componentName;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}

