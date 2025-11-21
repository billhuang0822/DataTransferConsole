package com.tsb.console.model;

public class ServiceInfo {
    private ServiceType type;
    private ServiceStatus status;

    public ServiceInfo(ServiceType type, ServiceStatus status) {
        this.type = type;
        this.status = status;
    }
    public ServiceType getType() { return type; }
    public ServiceStatus getStatus() { return status; }
    public void setStatus(ServiceStatus status) { this.status = status; }
}