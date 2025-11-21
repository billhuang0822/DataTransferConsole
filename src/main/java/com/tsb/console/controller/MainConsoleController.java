package com.tsb.console.controller;

import com.tsb.console.model.*;
import com.tsb.console.service.ServiceStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.*;

@Controller
public class MainConsoleController {
    @Autowired
    private ServiceStatusManager statusManager;

    @GetMapping("/")
    public String mainPage(Model model) {
        List<ServiceInfo> serviceInfos = statusManager.getAllServiceStatus();
        Map<ServiceType, Boolean> serviceEnabled = new LinkedHashMap<>();
        for (ServiceInfo service : serviceInfos) {
            serviceEnabled.put(service.getType(), statusManager.canStart(service.getType()));
        }
        model.addAttribute("services", serviceInfos);
        model.addAttribute("serviceEnabled", serviceEnabled);
        return "console";
    }

    @PostMapping("/service/{type}/start")
    @ResponseBody
    public String startService(@PathVariable("type") String type) {
        ServiceType serviceType = ServiceType.valueOf(type);
        if (!statusManager.canStart(serviceType)) {
            return "依賴未成立，無法執行";
        }
        statusManager.setStatus(serviceType, ServiceStatus.START);
        return "STARTED";
    }
}