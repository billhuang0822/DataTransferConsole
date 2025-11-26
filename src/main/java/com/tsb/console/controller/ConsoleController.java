package com.tsb.console.controller;

import com.tsb.console.model.*;
import com.tsb.console.service.ServiceStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ConsoleController {
    @Autowired
    private ServiceStatusManager statusManager;

    @GetMapping("/")
    public String mainPage(Model model) {
        List<ServiceInfo> serviceInfos = statusManager.getAllServiceStatus();
        Map<ServiceType, Boolean> enabledMap = new LinkedHashMap<>();
        for (ServiceType t : ServiceType.values()) {
            enabledMap.put(t, statusManager.canStart(t));
        }
        model.addAttribute("services", serviceInfos);
        model.addAttribute("serviceEnabled", enabledMap);
        return "console";
    }

    @PostMapping("/service/{type}/start")
    @ResponseBody
    public String startService(@PathVariable("type") String type) {
        ServiceType serviceType = ServiceType.valueOf(type);
        if (!statusManager.canStart(serviceType)) {
            return "FAIL";
        }
        statusManager.setStatus(serviceType, ServiceStatus.START);
        return "OK";
    }

    @GetMapping("/status")
    @ResponseBody
    public List<Map<String, String>> getStatus() {
        List<Map<String, String>> statuses = new ArrayList<>();
        for (ServiceInfo svc : statusManager.getAllServiceStatus()) {
            statuses.add(Map.of(
                "type", svc.getType().name(),
                "displayName", svc.getDisplayName(), // 新增這行
                "status", svc.getStatus().name(),
                "enabled", String.valueOf(statusManager.canStart(svc.getType()))
            ));
        }
        return statuses;
    }
}