package com.augustlee.mt.toolWindow.mws.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApiDetailVO {


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
    @JsonProperty("customErrors")
    private List<?> customErrors;
    @JsonProperty("degradation")
    private String degradation;
    @JsonProperty("description")
    private String description;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("expressType")
    private Integer expressType;
    @JsonProperty("failure")
    private String failure;
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
    @JsonProperty("port")
    private Integer port;
    @JsonProperty("preParameters")
    private List<?> preParameters;
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
    @JsonProperty("responseCookies")
    private List<?> responseCookies;
    @JsonProperty("responseHeaders")
    private List<?> responseHeaders;
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

    public List<?> getCustomErrors() {
        return customErrors;
    }

    public void setCustomErrors(List<?> customErrors) {
        this.customErrors = customErrors;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<?> getPreParameters() {
        return preParameters;
    }

    public void setPreParameters(List<?> preParameters) {
        this.preParameters = preParameters;
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

    public List<?> getResponseCookies() {
        return responseCookies;
    }

    public void setResponseCookies(List<?> responseCookies) {
        this.responseCookies = responseCookies;
    }

    public List<?> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(List<?> responseHeaders) {
        this.responseHeaders = responseHeaders;
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
        @JsonProperty("forExpression")
        private String forExpression;
        @JsonProperty("framework")
        private String framework;
        @JsonProperty("ignoreException")
        private Boolean ignoreException;
        @JsonProperty("inputs")
        private List<InputsDTO> inputs;
        @JsonProperty("loadBalance")
        private String loadBalance;
        @JsonProperty("methodName")
        private String methodName;
        @JsonProperty("pirateExtendParam")
        private Object pirateExtendParam;
        @JsonProperty("serviceName")
        private String serviceName;
        @JsonProperty("switchExpression")
        private String switchExpression;
        @JsonProperty("switchExpressionType")
        private String switchExpressionType;
        @JsonProperty("timeout")
        private Integer timeout;
        @JsonProperty("transparent")
        private Boolean transparent;
        @JsonProperty("transparentMethod")
        private Boolean transparentMethod;
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

        public String getForExpression() {
            return forExpression;
        }

        public void setForExpression(String forExpression) {
            this.forExpression = forExpression;
        }

        public String getFramework() {
            return framework;
        }

        public void setFramework(String framework) {
            this.framework = framework;
        }

        public Boolean getIgnoreException() {
            return ignoreException;
        }

        public void setIgnoreException(Boolean ignoreException) {
            this.ignoreException = ignoreException;
        }

        public List<InputsDTO> getInputs() {
            return inputs;
        }

        public void setInputs(List<InputsDTO> inputs) {
            this.inputs = inputs;
        }

        public String getLoadBalance() {
            return loadBalance;
        }

        public void setLoadBalance(String loadBalance) {
            this.loadBalance = loadBalance;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Object getPirateExtendParam() {
            return pirateExtendParam;
        }

        public void setPirateExtendParam(Object pirateExtendParam) {
            this.pirateExtendParam = pirateExtendParam;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getSwitchExpression() {
            return switchExpression;
        }

        public void setSwitchExpression(String switchExpression) {
            this.switchExpression = switchExpression;
        }

        public String getSwitchExpressionType() {
            return switchExpressionType;
        }

        public void setSwitchExpressionType(String switchExpressionType) {
            this.switchExpressionType = switchExpressionType;
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

        public Boolean getTransparentMethod() {
            return transparentMethod;
        }

        public void setTransparentMethod(Boolean transparentMethod) {
            this.transparentMethod = transparentMethod;
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

        public static class InputsDTO {
            @JsonProperty("defaultValue")
            private String defaultValue;
            @JsonProperty("expressType")
            private Integer expressType;
            @JsonProperty("mode")
            private Integer mode;
            @JsonProperty("paramKey")
            private String paramKey;
            @JsonProperty("type")
            private String type;
            @JsonProperty("value")
            private String value;

            public String getDefaultValue() {
                return defaultValue;
            }

            public void setDefaultValue(String defaultValue) {
                this.defaultValue = defaultValue;
            }

            public Integer getExpressType() {
                return expressType;
            }

            public void setExpressType(Integer expressType) {
                this.expressType = expressType;
            }

            public Integer getMode() {
                return mode;
            }

            public void setMode(Integer mode) {
                this.mode = mode;
            }

            public String getParamKey() {
                return paramKey;
            }

            public void setParamKey(String paramKey) {
                this.paramKey = paramKey;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
