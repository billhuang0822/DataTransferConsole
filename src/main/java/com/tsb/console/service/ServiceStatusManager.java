package com.tsb.console.service;

import com.tsb.console.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ServiceStatusManager {
    private static final Map<ServiceType, List<ServiceType>> DEPENDENCIES = Map.of(
        ServiceType.SK_FileRecv, Collections.emptyList(),
        ServiceType.SK_CIF_Import, List.of(ServiceType.SK_FileRecv),
        ServiceType.SK_DesigAcctImport, List.of(ServiceType.SK_FileRecv),
        ServiceType.SK_FreqAcctImport, List.of(ServiceType.SK_FileRecv),
        ServiceType.SKDesigAcct2TSB, List.of(ServiceType.SK_DesigAcctImport),
        ServiceType.SKFreqAcct2TSB, List.of(ServiceType.SK_FreqAcctImport)
    );

//    @Autowired
//    private StringRedisTemplate redisTemplate;

    public ServiceStatus getStatus(ServiceType type) {
//        String value = redisTemplate.opsForValue().get(type.name());
    	String value = null;
        if (value == null) return ServiceStatus.NONE;
        try {
            return ServiceStatus.valueOf(value);
        } catch (Exception e) {
            return ServiceStatus.NONE;
        }
    }

    public void setStatus(ServiceType type, ServiceStatus status) {
//        redisTemplate.opsForValue().set(type.name(), status.name());
    }

    public List<ServiceInfo> getAllServiceStatus() {
        List<ServiceInfo> list = new ArrayList<>();
        for (ServiceType type : ServiceType.values()) {
            list.add(new ServiceInfo(type, getStatus(type)));
        }
        return list;
    }

    // 判斷此服務可否 start（只有全部依賴 Done 且自身不是 Running/Done/Start才可執行）
    public boolean canStart(ServiceType type) {
        List<ServiceType> deps = DEPENDENCIES.get(type);
        if (deps != null) {
            for (ServiceType dep : deps) {
                if (getStatus(dep) != ServiceStatus.DONE)
                    return false;
            }
        }
        ServiceStatus myStatus = getStatus(type);
        return myStatus == ServiceStatus.NONE || myStatus == ServiceStatus.FAIL;
    }
}