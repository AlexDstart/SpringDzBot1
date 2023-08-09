package ru.skypro.lessons.springboot.weblibrary.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appInfo")
public class InfoController {

    @Value("${app.env}")
    private String appEnv;

    @GetMapping
    public String getAppInfo() {
        return "Application Environment: " + appEnv;
    }
}
