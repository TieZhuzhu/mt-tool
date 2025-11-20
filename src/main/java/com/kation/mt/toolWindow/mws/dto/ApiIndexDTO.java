package com.kation.mt.toolWindow.mws.dto;

public class ApiIndexDTO extends ClassIndexDTO{

    private String path;


    public ApiIndexDTO(String path, String serviceName, String methodName) {
        super(serviceName, methodName);
        this.path = path;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
