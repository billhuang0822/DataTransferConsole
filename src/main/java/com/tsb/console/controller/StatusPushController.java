package com.tsb.console.controller;

import com.tsb.console.model.*;
import com.tsb.console.service.ServiceStatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
public class StatusPushController {

    @Autowired
    private ServiceStatusManager statusManager;

    @GetMapping("/status/push")
    public SseEmitter statusPush() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                // 每3秒推送一次所有status
                StringBuilder sb = new StringBuilder();
                for (ServiceInfo svc : statusManager.getAllServiceStatus()) {
                    sb.append(svc.getType().name())
                      .append(",")
                      .append(svc.getStatus().name())
                      .append(";");
                }
                emitter.send(sb.toString()); // 你可以用 JSON，也可以自己格式化
            } catch (IOException e) {
                emitter.complete();
            }
        }, 0, 3, TimeUnit.SECONDS);

        return emitter;
    }
}