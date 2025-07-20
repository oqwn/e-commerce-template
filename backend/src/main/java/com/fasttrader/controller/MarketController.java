package com.fasttrader.controller;

import com.fasttrader.engine.MatchingEngine;
import com.fasttrader.model.OrderBook;
import com.fasttrader.model.enums.MarketState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {
    
    private final MatchingEngine matchingEngine;
    
    @PostMapping("/open")
    public ResponseEntity<Map<String, String>> openMarket() {
        log.info("Opening market for all symbols");
        
        try {
            Map<String, OrderBook> orderBooks = matchingEngine.getAllOrderBooks();
            for (OrderBook orderBook : orderBooks.values()) {
                orderBook.updateMarketState(MarketState.CONTINUOUS_TRADING);
                log.info("Opened market for symbol: {}", orderBook.getSymbol());
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Market opened for all symbols"
            ));
        } catch (Exception e) {
            log.error("Failed to open market", e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to open market: " + e.getMessage()
                ));
        }
    }
    
    @PostMapping("/close")
    public ResponseEntity<Map<String, String>> closeMarket() {
        log.info("Closing market for all symbols");
        
        try {
            Map<String, OrderBook> orderBooks = matchingEngine.getAllOrderBooks();
            for (OrderBook orderBook : orderBooks.values()) {
                orderBook.updateMarketState(MarketState.CLOSED);
                log.info("Closed market for symbol: {}", orderBook.getSymbol());
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Market closed for all symbols"
            ));
        } catch (Exception e) {
            log.error("Failed to close market", e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to close market: " + e.getMessage()
                ));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getMarketStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            Map<String, OrderBook> orderBooks = matchingEngine.getAllOrderBooks();
            Map<String, String> symbolStates = new HashMap<>();
            
            for (Map.Entry<String, OrderBook> entry : orderBooks.entrySet()) {
                symbolStates.put(entry.getKey(), entry.getValue().getMarketState().toString());
            }
            
            status.put("symbols", symbolStates);
            status.put("message", "Market status retrieved successfully");
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Failed to get market status", e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to get market status: " + e.getMessage()
                ));
        }
    }
}