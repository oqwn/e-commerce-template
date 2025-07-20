#!/bin/bash

# Script to manually trigger market simulation via API calls

API_BASE="http://localhost:20010/api"

echo "🚀 Starting Market Simulation..."
echo "================================"

# Function to create test accounts
create_accounts() {
    echo "📁 Creating test accounts..."
    
    accounts=("MM001" "MM002" "TRADER001" "TRADER002")
    
    for account in "${accounts[@]}"; do
        echo -n "Creating account $account... "
        response=$(curl -s -X POST "$API_BASE/accounts" \
            -H "Content-Type: application/json" \
            -d "{
                \"accountId\": \"$account\",
                \"accountName\": \"Test Account $account\",
                \"initialBalance\": 1000000.00
            }" 2>&1)
        
        if [[ $? -eq 0 ]]; then
            echo "✓"
        else
            echo "✗ (may already exist)"
        fi
    done
}

# Function to place initial orders for a symbol
place_orders_for_symbol() {
    local symbol=$1
    local base_price=$2
    
    echo -n "  Placing orders for $symbol (base: \$$base_price)... "
    
    # Place bid orders (5 levels)
    for i in {0..4}; do
        price=$(echo "$base_price - 0.1 * $i" | bc)
        quantity=$((100 + RANDOM % 400))
        
        curl -s -X POST "$API_BASE/orders" \
            -H "Content-Type: application/json" \
            -d "{
                \"accountId\": \"MM001\",
                \"symbol\": \"$symbol\",
                \"side\": \"BUY\",
                \"type\": \"LIMIT\",
                \"price\": $price,
                \"quantity\": $quantity
            }" > /dev/null 2>&1
    done
    
    # Place ask orders (5 levels)
    for i in {0..4}; do
        price=$(echo "$base_price + 0.1 + 0.1 * $i" | bc)
        quantity=$((100 + RANDOM % 400))
        
        curl -s -X POST "$API_BASE/orders" \
            -H "Content-Type: application/json" \
            -d "{
                \"accountId\": \"MM002\",
                \"symbol\": \"$symbol\",
                \"side\": \"SELL\",
                \"type\": \"LIMIT\",
                \"price\": $price,
                \"quantity\": $quantity
            }" > /dev/null 2>&1
    done
    
    echo "✓"
}

# Function to place initial orders
place_initial_orders() {
    echo "📊 Placing initial orders..."
    
    # Symbol base prices
    declare -A prices=(
        ["AAPL"]=182.50
        ["GOOGL"]=142.80
        ["MSFT"]=378.90
        ["AMZN"]=178.35
        ["TSLA"]=251.20
    )
    
    for symbol in "${!prices[@]}"; do
        place_orders_for_symbol "$symbol" "${prices[$symbol]}"
    done
}

# Function to check market data
check_market_data() {
    echo -e "\n📈 Checking market data..."
    echo "================================"
    
    response=$(curl -s "$API_BASE/market-data/quotes")
    
    if command -v jq &> /dev/null; then
        echo "$response" | jq -r '.[] | "\(.symbol): Bid=$\(.bidPrice // "null") Ask=$\(.askPrice // "null")"'
    else
        echo "$response"
    fi
}

# Function to continuously place random orders
simulate_trading() {
    echo -e "\n🔄 Starting continuous trading simulation..."
    echo "Press Ctrl+C to stop"
    echo "================================"
    
    symbols=("AAPL" "GOOGL" "MSFT" "AMZN" "TSLA")
    accounts=("MM001" "MM002" "TRADER001" "TRADER002")
    
    while true; do
        # Random symbol
        symbol=${symbols[$RANDOM % ${#symbols[@]}]}
        
        # Random account
        account=${accounts[$RANDOM % ${#accounts[@]}]}
        
        # Get current quote
        quote=$(curl -s "$API_BASE/market-data/quote/$symbol")
        
        if command -v jq &> /dev/null; then
            bid_price=$(echo "$quote" | jq -r '.bidPrice // 0')
            ask_price=$(echo "$quote" | jq -r '.askPrice // 0')
        else
            bid_price=100
            ask_price=101
        fi
        
        # Random action (70% limit orders, 20% market orders, 10% cancel)
        action=$((RANDOM % 100))
        
        if [[ $action -lt 70 ]]; then
            # Place limit order
            side=$([[ $((RANDOM % 2)) -eq 0 ]] && echo "BUY" || echo "SELL")
            
            if [[ "$side" == "BUY" ]]; then
                price=$(echo "$bid_price - 0.01" | bc)
            else
                price=$(echo "$ask_price + 0.01" | bc)
            fi
            
            quantity=$((50 + RANDOM % 200))
            
            echo -n "$(date +%H:%M:%S) - $account placing $side $quantity $symbol @ \$$price... "
            
            curl -s -X POST "$API_BASE/orders" \
                -H "Content-Type: application/json" \
                -d "{
                    \"accountId\": \"$account\",
                    \"symbol\": \"$symbol\",
                    \"side\": \"$side\",
                    \"type\": \"LIMIT\",
                    \"price\": $price,
                    \"quantity\": $quantity
                }" > /dev/null 2>&1
            
            echo "✓"
            
        elif [[ $action -lt 90 ]]; then
            # Place market order
            side=$([[ $((RANDOM % 2)) -eq 0 ]] && echo "BUY" || echo "SELL")
            quantity=$((10 + RANDOM % 50))
            
            echo -n "$(date +%H:%M:%S) - $account placing MARKET $side $quantity $symbol... "
            
            curl -s -X POST "$API_BASE/orders" \
                -H "Content-Type: application/json" \
                -d "{
                    \"accountId\": \"$account\",
                    \"symbol\": \"$symbol\",
                    \"side\": \"$side\",
                    \"type\": \"MARKET\",
                    \"quantity\": $quantity
                }" > /dev/null 2>&1
            
            echo "✓"
        fi
        
        # Sleep 1-3 seconds
        sleep $((1 + RANDOM % 3))
    done
}

# Function to manually trigger simulation start
trigger_simulation() {
    echo -e "\n🔧 Manually triggering simulation start..."
    response=$(curl -s -X POST "$API_BASE/simulation/start")
    
    if [[ $? -eq 0 ]]; then
        echo "✓ Simulation start triggered"
        echo "$response"
    else
        echo "✗ Failed to trigger simulation"
    fi
}

# Function to open market
open_market() {
    echo -e "\n🔔 Opening market for all symbols..."
    response=$(curl -s -X POST "$API_BASE/market/open")
    
    if [[ $? -eq 0 ]]; then
        echo "✓ Market opened"
        echo "$response"
    else
        echo "✗ Failed to open market"
    fi
}

# Main execution
main() {
    # Check if API is available
    echo -n "Checking API availability... "
    if curl -s -f "$API_BASE/market-data/symbols" > /dev/null 2>&1; then
        echo "✓"
    else
        echo "✗"
        echo "ERROR: Backend API is not available at $API_BASE"
        echo "Please ensure the backend is running with: docker-compose up"
        exit 1
    fi
    
    # Check simulation status
    echo -n "Checking simulation status... "
    status=$(curl -s "$API_BASE/simulation/status" 2>/dev/null || echo '{"enabled":false}')
    if echo "$status" | grep -q '"enabled":true'; then
        echo "✓ Already running"
    else
        echo "✗ Not running"
        trigger_simulation
        sleep 3  # Wait for simulation to initialize
    fi
    
    # Open market
    open_market
    sleep 2  # Wait for market to open
    
    # Create accounts
    create_accounts
    
    # Place initial orders
    place_initial_orders
    
    # Check market data
    check_market_data
    
    # Start continuous simulation
    simulate_trading
}

# Run main function
main