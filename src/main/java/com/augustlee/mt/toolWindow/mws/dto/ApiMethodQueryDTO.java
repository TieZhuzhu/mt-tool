package com.augustlee.mt.toolWindow.mws.dto;

import java.util.Collections;
import java.util.List;

/**
 * 方法查询参数。
 *
 * @author August Lee
 * @since 2026/05/08
 */
public class ApiMethodQueryDTO extends ClassIndexDTO {

    /**
     * 参数类型列表。
     */
    private final List<String> parameterTypeList;

    /**
     * 是否显式指定了参数签名。
     */
    private final boolean parameterSpecified;

    public ApiMethodQueryDTO(String serviceName, String methodName, List<String> parameterTypeList, boolean parameterSpecified) {
        super(serviceName, methodName);
        this.parameterTypeList = parameterTypeList == null ? Collections.emptyList() : parameterTypeList;
        this.parameterSpecified = parameterSpecified;
    }

    public List<String> getParameterTypeList() {
        return parameterTypeList;
    }

    public boolean isParameterSpecified() {
        return parameterSpecified;
    }
}
