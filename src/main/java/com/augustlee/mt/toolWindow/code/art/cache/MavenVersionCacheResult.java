package com.augustlee.mt.toolWindow.code.art.cache;

import com.augustlee.mt.toolWindow.code.art.vo.MavenVersionVO;

/**
 * Maven版本缓存结果
 *
 * @see MavenVersionCacheResult
 * @author August Lee
 * @since 2025/11/21 17:26
 */
public class MavenVersionCacheResult {
    private final MavenVersionVO data;
    private final long timestamp;

    public MavenVersionCacheResult(MavenVersionVO data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public MavenVersionVO getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

