package com.tsb.console.service;

import com.tsb.console.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceStatusManager {
    private static final Map<ServiceType, List<ServiceType>> DEPENDENCIES = Map.of(
        ServiceType.SK_FileRecv, List.of(),
        ServiceType.SK_CIF_Import, List.of(ServiceType.SK_FileRecv),
        ServiceType.SK_DesigAcctImport, List.of(ServiceType.SK_FileRecv),
        ServiceType.SK_FreqAcctImport, List.of(ServiceType.SK_FileRecv),
        ServiceType.SKDesigAcct2TSB, List.of(ServiceType.SK_DesigAcctImport),
        ServiceType.SKFreqAcct2TSB, List.of(ServiceType.SK_FreqAcctImport)
    );
    @Autowired
    private StringRedisTemplate redisTemplate;

    public ServiceStatus getStatus(ServiceType type) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get(type.name());
        if (value == null) return ServiceStatus.NONE;
        try {
            return ServiceStatus.valueOf(value);
        } catch (Exception e) {
            return ServiceStatus.NONE;
        }
    }

    public void setStatus(ServiceType type, ServiceStatus status) {
        redisTemplate.opsForValue().set(type.name(), status.name());
    }

    public List<ServiceInfo> getAllServiceStatus() {
        List<ServiceInfo> list = new ArrayList<>();
        for (ServiceType type : ServiceType.values()) {
            list.add(new ServiceInfo(type, getStatus(type)));
        }
        return list;
    }

    public boolean canStart(ServiceType type) {
        List<ServiceType> deps = DEPENDENCIES.get(type);
        if (deps == null || deps.isEmpty()) return true;
        for (ServiceType dep : deps) {
            ServiceStatus depStatus = getStatus(dep);
            if (depStatus != ServiceStatus.DONE) return false;
        }
        ServiceStatus myStatus = getStatus(type);
        return myStatus == ServiceStatus.NONE || myStatus == ServiceStatus.FAIL;
    }
}