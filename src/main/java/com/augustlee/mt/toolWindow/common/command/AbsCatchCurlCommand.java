package com.augustlee.mt.toolWindow.common.command;

/**
 * 带缓存的 Curl 命令执行抽象类
 * 继承自 AbsCurlCommand，提供响应缓存功能
 *
 * @see AbsCatchCurlCommand
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public abstract class AbsCatchCurlCommand<RESP> extends AbsCurlCommand<RESP>{

    protected RESP cache = null;
    public long requestTime = -1;

    @Override
    public RESP execute(){
        int cacheMs = this.getCacheMS();
        if(requestTime < 0 || System.currentTimeMillis() - requestTime > cacheMs || cache == null){
            requestTime = System.currentTimeMillis();
            this.cache = super.execute();
        }
        return this.cache;
    }

    public abstract int getCacheMS();


}

