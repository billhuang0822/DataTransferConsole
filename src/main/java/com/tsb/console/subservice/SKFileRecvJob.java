package com.tsb.console.subservice;

import com.tsb.console.model.ServiceType;
import com.tsb.console.model.ServiceStatus;
import com.tsb.console.service.ServiceStatusManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SKFileRecvJob {
    private final ServiceStatusManager statusManager;
    public SKFileRecvJob(ServiceStatusManager statusManager) {
        this.statusManager = statusManager;
    }

    @Scheduled(fixedDelay = 10000)
    public void monitor() {
        if (statusManager.getStatus(ServiceType.SK_FileRecv) == ServiceStatus.START) {
            statusManager.setStatus(ServiceType.SK_FileRecv, ServiceStatus.RUNNING);
            // 實際作業流程，如失敗:
            // statusManager.setStatus(ServiceType.SK_FileRecv, ServiceStatus.FAIL);
            // 成功:
            statusManager.setStatus(ServiceType.SK_FileRecv, ServiceStatus.DONE);
        }
    }
}