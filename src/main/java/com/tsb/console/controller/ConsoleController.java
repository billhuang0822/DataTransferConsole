package com.tsb.console.controller;

import com.tsb.console.model.*;
import com.tsb.console.service.ServiceStatusManager;
import com.tsb.console.service.ConsoleControlTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ConsoleController {
    @Autowired
    private ServiceStatusManager statusManager;

     @Autowired
    private ConsoleControlTokenService tokenService;

    @GetMapping("/")
    public String mainPage(Model model) {
       public String mainPage(Model model, HttpSession session) {
        String sid = session.getId();
        boolean isController = tokenService.tryAcquireOrUpdateToken(sid);

        List<ServiceInfo> serviceInfos = statusManager.getAllServiceStatus();
        Map<ServiceType, Boolean> enabledMap = new LinkedHashMap<>();
        for (ServiceType t : ServiceType.values()) {
            enabledMap.put(t, statusManager.canStart(t));
        }
        model.addAttribute("services", serviceInfos);
        model.addAttribute("serviceEnabled", enabledMap);

        if (isController) {
            return "console";
        } else {
            return "view";
        }
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

     // 狀態查詢與令牌刷新（console.jsp用於 AJAX 心跳）
    @GetMapping("/controller/status")
    @ResponseBody
    public Map<String, Object> controllerStatus(HttpSession session) {
        String sid = session.getId();
        boolean isController = tokenService.tryAcquireOrUpdateToken(sid);
        Map<String, Object> result = new HashMap<>();
        result.put("isController", isController);
        return result;
    }

     // 景況token釋放(離開或登出可調用)
    @PostMapping("/controller/release")
    @ResponseBody
    public void releaseController(HttpSession session) {
        tokenService.releaseTokenIfMatch(session.getId());
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
