package com.vlosco.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("<html><body style='display: flex; justify-content: center; align-items: center; height: 100vh;'><h1>Service is up and running</h1></body></html>");
    }
} 