package com.kation.mt.toolWindow.mws.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupVO {

    @JsonProperty("audit")
    private Boolean audit;
    @JsonProperty("bgId")
    private Integer bgId;
    @JsonProperty("bgName")
    private String bgName;
    @JsonProperty("blId")
    private Integer blId;
    @JsonProperty("blName")
    private String blName;
    @JsonProperty("clusterId")
    private Integer clusterId;
    @JsonProperty("commonPrefix")
    private String commonPrefix;
    @JsonProperty("createTime")
    private Long createTime;
    @JsonProperty("createUserName")
    private String createUserName;
    @JsonProperty("defaultDomain")
    private Integer defaultDomain;
    @JsonProperty("description")
    private String description;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("domainType")
    private Integer domainType;
    @JsonProperty("gray")
    private Boolean gray;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("notifyEnabled")
    private Boolean notifyEnabled;
    @JsonProperty("raptorAlertGroupId")
    private Integer raptorAlertGroupId;
    @JsonProperty("updateTime")
    private Long updateTime;
    @JsonProperty("updateUserName")
    private String updateUserName;


    public Boolean getAudit() {
        return audit;
    }

    public void setAudit(Boolean audit) {
        this.audit = audit;
    }

    public Integer getBgId() {
        return bgId;
    }

    public void setBgId(Integer bgId) {
        this.bgId = bgId;
    }

    public String getBgName() {
        return bgName;
    }

    public void setBgName(String bgName) {
        this.bgName = bgName;
    }

    public Integer getBlId() {
        return blId;
    }

    public void setBlId(Integer blId) {
        this.blId = blId;
    }

    public String getBlName() {
        return blName;
    }

    public void setBlName(String blName) {
        this.blName = blName;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public String getCommonPrefix() {
        return commonPrefix;
    }

    public void setCommonPrefix(String commonPrefix) {
        this.commonPrefix = commonPrefix;
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

    public Integer getDefaultDomain() {
        return defaultDomain;
    }

    public void setDefaultDomain(Integer defaultDomain) {
        this.defaultDomain = defaultDomain;
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

    public Integer getDomainType() {
        return domainType;
    }

    public void setDomainType(Integer domainType) {
        this.domainType = domainType;
    }

    public Boolean getGray() {
        return gray;
    }

    public void setGray(Boolean gray) {
        this.gray = gray;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNotifyEnabled() {
        return notifyEnabled;
    }

    public void setNotifyEnabled(Boolean notifyEnabled) {
        this.notifyEnabled = notifyEnabled;
    }

    public Integer getRaptorAlertGroupId() {
        return raptorAlertGroupId;
    }

    public void setRaptorAlertGroupId(Integer raptorAlertGroupId) {
        this.raptorAlertGroupId = raptorAlertGroupId;
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
}
