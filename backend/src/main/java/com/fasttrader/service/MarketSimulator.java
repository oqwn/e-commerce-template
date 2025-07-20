package com.fasttrader.service;

import com.fasttrader.model.Order;
import com.fasttrader.model.OrderBook;
import com.fasttrader.model.enums.OrderSide;
import com.fasttrader.model.enums.OrderType;
import com.fasttrader.model.enums.MarketState;
import com.fasttrader.engine.MatchingEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketSimulator {
    
    private final OrderService orderService;
    private final AccountService accountService;
    private final MatchingEngine matchingEngine;
    
    @Value("${app.simulation.enabled:false}")
    private boolean simulationEnabledConfig;
    
    private final Random random = new Random();
    private final AtomicBoolean simulationEnabled = new AtomicBoolean(false);
    
    private static final List<String> SYMBOLS = Arrays.asList("AAPL", "GOOGL", "MSFT", "AMZN", "TSLA");
    private static final List<String> TEST_ACCOUNTS = Arrays.asList("MM001", "MM002", "TRADER001", "TRADER002");
    
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initializeSimulation() {
        if (!simulationEnabledConfig) {
            log.info("Market simulation is disabled in configuration");
            return;
        }
        
        log.info("Initializing market simulation...");
        
        try {
            // Create test accounts
            for (String accountId : TEST_ACCOUNTS) {
                try {
                    accountService.createAccount(accountId, "Test Account " + accountId, 
                        new BigDecimal("1000000.00"));
                    log.info("Created test account: {}", accountId);
                } catch (Exception e) {
                    log.debug("Account {} may already exist: {}", accountId, e.getMessage());
                }
            }
            
            // Wait a bit for accounts to be ready
            Thread.sleep(1000);
            
            // Open market for all symbols
            openMarketForAllSymbols();
            
            placeInitialOrders();
            
            simulationEnabled.set(true);
            log.info("Market simulation initialized successfully");
            
        } catch (Exception e) {
            log.error("Failed to initialize market simulation", e);
        }
    }
    
    private void placeInitialOrders() {
        log.info("Placing initial orders for all symbols");
        
        for (String symbol : SYMBOLS) {
            BigDecimal basePrice = getInitialPrice(symbol);
            
            // Place more aggressive initial orders to ensure bid/ask presence
            // Start with tight spreads
            BigDecimal bidStart = basePrice.subtract(BigDecimal.valueOf(0.05));
            BigDecimal askStart = basePrice.add(BigDecimal.valueOf(0.05));
            
            // Place 10 levels on each side for better depth
            for (int i = 0; i < 10; i++) {
                BigDecimal bidPrice = bidStart.subtract(
                    BigDecimal.valueOf(i * 0.05)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal askPrice = askStart.add(
                    BigDecimal.valueOf(i * 0.05)).setScale(2, RoundingMode.HALF_UP);
                
                // Larger quantities for better liquidity
                Long bidQuantity = 500L + random.nextInt(1500);
                Long askQuantity = 500L + random.nextInt(1500);
                
                Order buyOrder = Order.builder()
                    .accountId("MM001")
                    .symbol(symbol)
                    .side(OrderSide.BUY)
                    .type(OrderType.LIMIT)
                    .price(bidPrice)
                    .quantity(bidQuantity)
                    .build();
                
                Order sellOrder = Order.builder()
                    .accountId("MM002")
                    .symbol(symbol)
                    .side(OrderSide.SELL)
                    .type(OrderType.LIMIT)
                    .price(askPrice)
                    .quantity(askQuantity)
                    .build();
                
                try {
                    orderService.placeOrder(buyOrder);
                    orderService.placeOrder(sellOrder);
                    log.debug("Placed initial orders for {} - Bid: {} @ {}, Ask: {} @ {}", 
                        symbol, bidQuantity, bidPrice, askQuantity, askPrice);
                } catch (Exception e) {
                    log.error("Failed to place initial order for {}", symbol, e);
                }
            }
        }
        
        log.info("Initial order placement completed");
    }
    
    public boolean isSimulationEnabled() {
        return simulationEnabled.get();
    }
    
    @Scheduled(fixedDelay = 2000)
    public void simulateMarketActivity() {
        if (!simulationEnabled.get()) {
            return;
        }
        
        try {
            // First ensure all symbols have bid/ask
            ensureBidAskForAllSymbols();
            
            String symbol = SYMBOLS.get(random.nextInt(SYMBOLS.size()));
            String accountId = TEST_ACCOUNTS.get(random.nextInt(TEST_ACCOUNTS.size()));
            
            if (random.nextDouble() < 0.7) {
                placeRandomOrder(symbol, accountId);
            } else if (random.nextDouble() < 0.9) {
                modifyRandomOrder(accountId);
            } else {
                cancelRandomOrder(accountId);
            }
            
        } catch (Exception e) {
            log.error("Error in market simulation", e);
        }
    }
    
    private void placeRandomOrder(String symbol, String accountId) {
        try {
            OrderSide side = random.nextBoolean() ? OrderSide.BUY : OrderSide.SELL;
            OrderType type = random.nextDouble() < 0.8 ? OrderType.LIMIT : OrderType.MARKET;
            
            BigDecimal currentPrice = getCurrentPrice(symbol);
            BigDecimal orderPrice = null;
            
            if (type == OrderType.LIMIT) {
                double priceVariation = (random.nextDouble() - 0.5) * 0.02;
                orderPrice = currentPrice.multiply(BigDecimal.valueOf(1 + priceVariation));
                orderPrice = roundToTickSize(orderPrice);
            }
            
            Long quantity = (long) ((random.nextInt(50) + 1) * 100);
            
            Order order = Order.builder()
                .accountId(accountId)
                .symbol(symbol)
                .side(side)
                .type(type)
                .price(orderPrice)
                .quantity(quantity)
                .build();
            
            orderService.placeOrder(order);
            log.debug("Placed simulated order: {} {} {} @ {}", 
                side, quantity, symbol, orderPrice);
            
        } catch (Exception e) {
            log.debug("Failed to place simulated order: {}", e.getMessage());
        }
    }
    
    private void modifyRandomOrder(String accountId) {
        try {
            List<Order> activeOrders = orderService.getActiveOrdersByAccount(accountId);
            if (!activeOrders.isEmpty()) {
                Order order = activeOrders.get(random.nextInt(activeOrders.size()));
                
                BigDecimal newPrice = order.getPrice().multiply(
                    BigDecimal.valueOf(1 + (random.nextDouble() - 0.5) * 0.01));
                newPrice = roundToTickSize(newPrice);
                
                orderService.modifyOrder(order.getOrderId(), newPrice, null);
                log.debug("Modified simulated order: {} to price {}", 
                    order.getOrderId(), newPrice);
            }
        } catch (Exception e) {
            log.debug("Failed to modify simulated order: {}", e.getMessage());
        }
    }
    
    private void cancelRandomOrder(String accountId) {
        try {
            List<Order> activeOrders = orderService.getActiveOrdersByAccount(accountId);
            if (!activeOrders.isEmpty()) {
                Order order = activeOrders.get(random.nextInt(activeOrders.size()));
                orderService.cancelOrder(order.getOrderId());
                log.debug("Cancelled simulated order: {}", order.getOrderId());
            }
        } catch (Exception e) {
            log.debug("Failed to cancel simulated order: {}", e.getMessage());
        }
    }
    
    private void openMarketForAllSymbols() {
        log.info("Opening market for all symbols");
        for (String symbol : SYMBOLS) {
            OrderBook orderBook = matchingEngine.getOrderBook(symbol);
            orderBook.updateMarketState(MarketState.CONTINUOUS_TRADING);
            log.info("Market opened for {}", symbol);
        }
    }
    
    private void ensureBidAskForAllSymbols() {
        for (String symbol : SYMBOLS) {
            OrderBook orderBook = matchingEngine.getOrderBook(symbol);
            
            // Check if bid is missing
            if (orderBook.getBestBid() == null || orderBook.getBidLevels().isEmpty()) {
                BigDecimal basePrice = getInitialPrice(symbol);
                BigDecimal bidPrice = basePrice.subtract(BigDecimal.valueOf(0.10))
                    .setScale(2, RoundingMode.HALF_UP);
                
                Order buyOrder = Order.builder()
                    .accountId("MM001")
                    .symbol(symbol)
                    .side(OrderSide.BUY)
                    .type(OrderType.LIMIT)
                    .price(bidPrice)
                    .quantity(1000L)
                    .build();
                
                try {
                    orderService.placeOrder(buyOrder);
                    log.debug("Added missing bid for {} at {}", symbol, bidPrice);
                } catch (Exception e) {
                    log.error("Failed to add bid for {}", symbol, e);
                }
            }
            
            // Check if ask is missing
            if (orderBook.getBestAsk() == null || orderBook.getAskLevels().isEmpty()) {
                BigDecimal basePrice = getInitialPrice(symbol);
                BigDecimal askPrice = basePrice.add(BigDecimal.valueOf(0.10))
                    .setScale(2, RoundingMode.HALF_UP);
                
                Order sellOrder = Order.builder()
                    .accountId("MM002")
                    .symbol(symbol)
                    .side(OrderSide.SELL)
                    .type(OrderType.LIMIT)
                    .price(askPrice)
                    .quantity(1000L)
                    .build();
                
                try {
                    orderService.placeOrder(sellOrder);
                    log.debug("Added missing ask for {} at {}", symbol, askPrice);
                } catch (Exception e) {
                    log.error("Failed to add ask for {}", symbol, e);
                }
            }
        }
    }
    
    private BigDecimal getInitialPrice(String symbol) {
        switch (symbol) {
            case "AAPL":
                return new BigDecimal("150.00");
            case "GOOGL":
                return new BigDecimal("2800.00");
            case "MSFT":
                return new BigDecimal("300.00");
            case "AMZN":
                return new BigDecimal("3300.00");
            case "TSLA":
                return new BigDecimal("900.00");
            default:
                return new BigDecimal("100.00");
        }
    }
    
    private BigDecimal getCurrentPrice(String symbol) {
        OrderBook orderBook = orderService.getOrderBook(symbol);
        
        if (orderBook.getLastPrice() != null) {
            return orderBook.getLastPrice();
        } else if (orderBook.getMidPrice() != null) {
            return orderBook.getMidPrice();
        } else {
            return getInitialPrice(symbol);
        }
    }
    
    public void enableSimulation(boolean enable) {
        simulationEnabled.set(enable);
        log.info("Market simulation {}", enable ? "enabled" : "disabled");
    }
    
    /**
     * Round price to valid tick size based on price ranges
     */
    private BigDecimal roundToTickSize(BigDecimal price) {
        BigDecimal tickSize;
        
        if (price.compareTo(new BigDecimal("0.01")) < 0) {
            tickSize = new BigDecimal("0.0001");
        } else if (price.compareTo(new BigDecimal("0.10")) < 0) {
            tickSize = new BigDecimal("0.001");
        } else if (price.compareTo(new BigDecimal("1.00")) < 0) {
            tickSize = new BigDecimal("0.01");
        } else if (price.compareTo(new BigDecimal("10.00")) < 0) {
            tickSize = new BigDecimal("0.01");
        } else if (price.compareTo(new BigDecimal("100.00")) < 0) {
            tickSize = new BigDecimal("0.05");
        } else if (price.compareTo(new BigDecimal("1000.00")) < 0) {
            tickSize = new BigDecimal("0.10");
        } else {
            tickSize = new BigDecimal("1.00");
        }
        
        // Round to nearest tick size
        return price.divide(tickSize, 0, RoundingMode.HALF_UP).multiply(tickSize);
    }
}