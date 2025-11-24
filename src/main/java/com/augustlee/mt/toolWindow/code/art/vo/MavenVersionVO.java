package com.augustlee.mt.toolWindow.code.art.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maven版本列表响应VO
 *
 * @see MavenVersionVO
 * @author August Lee
 * @since 2025/11/20 17:14
 */
public class MavenVersionVO {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("data")
    private MavenVersionDataVO data;

    @JsonProperty("message")
    private String message;

    @JsonProperty("details")
    private String details;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public MavenVersionDataVO getData() {
        return data;
    }

    public void setData(MavenVersionDataVO data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
