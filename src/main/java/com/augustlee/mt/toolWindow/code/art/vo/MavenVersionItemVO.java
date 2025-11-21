package com.augustlee.mt.toolWindow.code.art.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maven版本项VO
 *
 * @see MavenVersionItemVO
 * @author August Lee
 * @since 2025/11/20 17:14
 */
public class MavenVersionItemVO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("version")
    private String version;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("artifactId")
    private String artifactId;

    @JsonProperty("componentName")
    private String componentName;

    @JsonProperty("forbiddenFlag")
    private Boolean forbiddenFlag;

    @JsonProperty("limitRules")
    private String limitRules;

    @JsonProperty("vulFlag")
    private Boolean vulFlag;

    @JsonProperty("componentArtVuls")
    private String componentArtVuls;

    @JsonProperty("usedNum")
    private Integer usedNum;

    @JsonProperty("publishTime")
    private String publishTime;

    @JsonProperty("dependencies")
    private String dependencies;

    @JsonProperty("repositories")
    private String repositories;

    @JsonProperty("author")
    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Boolean getForbiddenFlag() {
        return forbiddenFlag;
    }

    public void setForbiddenFlag(Boolean forbiddenFlag) {
        this.forbiddenFlag = forbiddenFlag;
    }

    public String getLimitRules() {
        return limitRules;
    }

    public void setLimitRules(String limitRules) {
        this.limitRules = limitRules;
    }

    public Boolean getVulFlag() {
        return vulFlag;
    }

    public void setVulFlag(Boolean vulFlag) {
        this.vulFlag = vulFlag;
    }

    public String getComponentArtVuls() {
        return componentArtVuls;
    }

    public void setComponentArtVuls(String componentArtVuls) {
        this.componentArtVuls = componentArtVuls;
    }

    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getRepositories() {
        return repositories;
    }

    public void setRepositories(String repositories) {
        this.repositories = repositories;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

