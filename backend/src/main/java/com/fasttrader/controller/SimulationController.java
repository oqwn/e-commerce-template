package com.fasttrader.controller;

import com.fasttrader.service.MarketSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/simulation")
@RequiredArgsConstructor
public class SimulationController {
    
    private final MarketSimulator marketSimulator;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startSimulation() {
        log.info("Manual simulation start requested");
        
        try {
            marketSimulator.initializeSimulation();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Market simulation started"
            ));
        } catch (Exception e) {
            log.error("Failed to start simulation", e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to start simulation: " + e.getMessage()
                ));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSimulationStatus() {
        return ResponseEntity.ok(Map.of(
            "enabled", marketSimulator.isSimulationEnabled(),
            "message", marketSimulator.isSimulationEnabled() 
                ? "Simulation is running" 
                : "Simulation is not running"
        ));
    }
}