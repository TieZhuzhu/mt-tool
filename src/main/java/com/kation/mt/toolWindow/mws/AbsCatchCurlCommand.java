package com.kation.mt.toolWindow.mws;


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
