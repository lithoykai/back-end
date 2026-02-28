package com.joaopenascimento.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/healthz")
public class Healthz {

    @GetMapping
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
