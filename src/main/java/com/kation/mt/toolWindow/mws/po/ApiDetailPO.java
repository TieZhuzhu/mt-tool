package com.kation.mt.toolWindow.mws.po;

import java.util.Objects;

public class ApiDetailPO {

    private String groupName;

    private String apiName;

    public ApiDetailPO(String groupName, String apiName) {
        this.groupName = groupName;
        this.apiName = apiName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiDetailPO that = (ApiDetailPO) o;
        return Objects.equals(groupName, that.groupName) && Objects.equals(apiName, that.apiName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, apiName);
    }
}
