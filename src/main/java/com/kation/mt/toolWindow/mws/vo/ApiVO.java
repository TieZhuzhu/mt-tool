package com.kation.mt.toolWindow.mws.vo;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApiVO {


    @JsonProperty("allowMultiHttpParamValues")
    private Boolean allowMultiHttpParamValues;
    @JsonProperty("apiGroupId")
    private Integer apiGroupId;
    @JsonProperty("apiGroupName")
    private String apiGroupName;
    @JsonProperty("apiVersion")
    private Integer apiVersion;
    @JsonProperty("contentType")
    private Integer contentType;
    @JsonProperty("createTime")
    private Long createTime;
    @JsonProperty("createUserName")
    private String createUserName;
    @JsonProperty("degradation")
    private String degradation;
    @JsonProperty("description")
    private String description;
    @JsonProperty("errorMatchType")
    private String errorMatchType;
    @JsonProperty("expressType")
    private Integer expressType;
    @JsonProperty("failure")
    private String failure;
    @JsonProperty("grayRule")
    private String grayRule;
    @JsonProperty("headerMap")
    private String headerMap;
    @JsonProperty("httpType")
    private Integer httpType;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("importFromMapi")
    private Boolean importFromMapi;
    @JsonProperty("importFromPapi")
    private Boolean importFromPapi;
    @JsonProperty("invokerScheduleType")
    private Integer invokerScheduleType;
    @JsonProperty("invokerViews")
    private List<InvokerViewsDTO> invokerViews;
    @JsonProperty("mapiBodyDecodeEnabled")
    private Boolean mapiBodyDecodeEnabled;
    @JsonProperty("methodName")
    private String methodName;
    @JsonProperty("methodTypes")
    private String methodTypes;
    @JsonProperty("name")
    private String name;
    @JsonProperty("path")
    private String path;
    @JsonProperty("prestatus")
    private Integer prestatus;
    @JsonProperty("redirect")
    private String redirect;
    @JsonProperty("regexPaths")
    private List<?> regexPaths;
    @JsonProperty("response")
    private String response;
    @JsonProperty("responseCase")
    private String responseCase;
    @JsonProperty("rhinoConfigSyncStatus")
    private Integer rhinoConfigSyncStatus;
    @JsonProperty("serviceName")
    private String serviceName;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("timeout")
    private Integer timeout;
    @JsonProperty("transparent")
    private Boolean transparent;
    @JsonProperty("updateTime")
    private Long updateTime;
    @JsonProperty("updateUserName")
    private String updateUserName;

    public Boolean getAllowMultiHttpParamValues() {
        return allowMultiHttpParamValues;
    }

    public void setAllowMultiHttpParamValues(Boolean allowMultiHttpParamValues) {
        this.allowMultiHttpParamValues = allowMultiHttpParamValues;
    }

    public Integer getApiGroupId() {
        return apiGroupId;
    }

    public void setApiGroupId(Integer apiGroupId) {
        this.apiGroupId = apiGroupId;
    }

    public String getApiGroupName() {
        return apiGroupName;
    }

    public void setApiGroupName(String apiGroupName) {
        this.apiGroupName = apiGroupName;
    }

    public Integer getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(Integer apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getDegradation() {
        return degradation;
    }

    public void setDegradation(String degradation) {
        this.degradation = degradation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorMatchType() {
        return errorMatchType;
    }

    public void setErrorMatchType(String errorMatchType) {
        this.errorMatchType = errorMatchType;
    }

    public Integer getExpressType() {
        return expressType;
    }

    public void setExpressType(Integer expressType) {
        this.expressType = expressType;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public String getGrayRule() {
        return grayRule;
    }

    public void setGrayRule(String grayRule) {
        this.grayRule = grayRule;
    }

    public String getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(String headerMap) {
        this.headerMap = headerMap;
    }

    public Integer getHttpType() {
        return httpType;
    }

    public void setHttpType(Integer httpType) {
        this.httpType = httpType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getImportFromMapi() {
        return importFromMapi;
    }

    public void setImportFromMapi(Boolean importFromMapi) {
        this.importFromMapi = importFromMapi;
    }

    public Boolean getImportFromPapi() {
        return importFromPapi;
    }

    public void setImportFromPapi(Boolean importFromPapi) {
        this.importFromPapi = importFromPapi;
    }

    public Integer getInvokerScheduleType() {
        return invokerScheduleType;
    }

    public void setInvokerScheduleType(Integer invokerScheduleType) {
        this.invokerScheduleType = invokerScheduleType;
    }

    public List<InvokerViewsDTO> getInvokerViews() {
        return invokerViews;
    }

    public void setInvokerViews(List<InvokerViewsDTO> invokerViews) {
        this.invokerViews = invokerViews;
    }

    public Boolean getMapiBodyDecodeEnabled() {
        return mapiBodyDecodeEnabled;
    }

    public void setMapiBodyDecodeEnabled(Boolean mapiBodyDecodeEnabled) {
        this.mapiBodyDecodeEnabled = mapiBodyDecodeEnabled;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodTypes() {
        return methodTypes;
    }

    public void setMethodTypes(String methodTypes) {
        this.methodTypes = methodTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPrestatus() {
        return prestatus;
    }

    public void setPrestatus(Integer prestatus) {
        this.prestatus = prestatus;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public List<?> getRegexPaths() {
        return regexPaths;
    }

    public void setRegexPaths(List<?> regexPaths) {
        this.regexPaths = regexPaths;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponseCase() {
        return responseCase;
    }

    public void setResponseCase(String responseCase) {
        this.responseCase = responseCase;
    }

    public Integer getRhinoConfigSyncStatus() {
        return rhinoConfigSyncStatus;
    }

    public void setRhinoConfigSyncStatus(Integer rhinoConfigSyncStatus) {
        this.rhinoConfigSyncStatus = rhinoConfigSyncStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean getTransparent() {
        return transparent;
    }

    public void setTransparent(Boolean transparent) {
        this.transparent = transparent;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public static class InvokerViewsDTO {
        @JsonProperty("alias")
        private String alias;
        @JsonProperty("appkey")
        private String appkey;
        @JsonProperty("ignoreException")
        private Boolean ignoreException;
        @JsonProperty("loadBalance")
        private String loadBalance;
        @JsonProperty("type")
        private String type;
        @JsonProperty("useConditionalRoute")
        private Boolean useConditionalRoute;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAppkey() {
            return appkey;
        }

        public void setAppkey(String appkey) {
            this.appkey = appkey;
        }

        public Boolean getIgnoreException() {
            return ignoreException;
        }

        public void setIgnoreException(Boolean ignoreException) {
            this.ignoreException = ignoreException;
        }

        public String getLoadBalance() {
            return loadBalance;
        }

        public void setLoadBalance(String loadBalance) {
            this.loadBalance = loadBalance;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getUseConditionalRoute() {
            return useConditionalRoute;
        }

        public void setUseConditionalRoute(Boolean useConditionalRoute) {
            this.useConditionalRoute = useConditionalRoute;
        }
    }
}
