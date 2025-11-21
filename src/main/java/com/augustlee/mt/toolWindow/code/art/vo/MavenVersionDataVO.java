package com.augustlee.mt.toolWindow.code.art.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Maven版本列表数据VO
 *
 * @see MavenVersionDataVO
 * @author August Lee
 * @since 2025/11/20 17:14
 */
public class MavenVersionDataVO {

    @JsonProperty("tn")
    private Integer tn;

    @JsonProperty("cn")
    private Integer cn;

    @JsonProperty("pn")
    private Integer pn;

    @JsonProperty("sn")
    private Integer sn;

    @JsonProperty("items")
    private List<MavenVersionItemVO> items;

    public Integer getTn() {
        return tn;
    }

    public void setTn(Integer tn) {
        this.tn = tn;
    }

    public Integer getCn() {
        return cn;
    }

    public void setCn(Integer cn) {
        this.cn = cn;
    }

    public Integer getPn() {
        return pn;
    }

    public void setPn(Integer pn) {
        this.pn = pn;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public List<MavenVersionItemVO> getItems() {
        return items;
    }

    public void setItems(List<MavenVersionItemVO> items) {
        this.items = items;
    }
}

