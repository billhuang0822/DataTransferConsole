package com.tsb.console.model;

public class ServiceInfo {
    private ServiceType type;
    private ServiceStatus status;
    private String displayName;

    public ServiceInfo(ServiceType type, ServiceStatus status) {
        this.type = type;
        this.status = status;
        this.displayName = type.getDisplayName(); // 這行直接取 enum 的中文名稱
    }
    public ServiceType getType() { return type; }
    public ServiceStatus getStatus() { return status; }
    public void setStatus(ServiceStatus status) { this.status = status; }
    public String getDisplayName() { return displayName; }
}