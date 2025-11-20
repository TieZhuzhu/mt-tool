package com.augustlee.mt.toolWindow.mws.dto;

public class ClassIndexDTO {

    private String serviceName;

    private String methodName;

    public ClassIndexDTO(String serviceName, String methodName) {
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
