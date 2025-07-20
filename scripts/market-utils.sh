#!/bin/bash

# Shared utility functions for market simulation scripts

API_BASE="http://localhost:20010/api"

# Symbol base prices
declare -A BASE_PRICES=(
    ["AAPL"]=182.50
    ["GOOGL"]=142.80
    ["MSFT"]=378.90
    ["AMZN"]=178.35
    ["TSLA"]=251.20
)

SYMBOLS=("AAPL" "GOOGL" "MSFT" "AMZN" "TSLA")
ACCOUNTS=("MM001" "MM002" "TRADER001" "TRADER002")

# Check if API is available
check_api() {
    echo -n "Checking API availability... "
    if curl -s -f "$API_BASE/market-data/symbols" > /dev/null 2>&1; then
        echo "✓"
        return 0
    else
        echo "✗"
        echo "ERROR: Backend API is not available at $API_BASE"
        echo "Please ensure the backend is running with: docker-compose up"
        return 1
    fi
}

# Place a single order
place_order() {
    local account_id=$1
    local symbol=$2
    local side=$3
    local price=$4
    local quantity=$5
    
    curl -s -X POST "$API_BASE/orders" \
        -H "Content-Type: application/json" \
        -d "{
            \"accountId\": \"$account_id\",
            \"symbol\": \"$symbol\",
            \"side\": \"$side\",
            \"type\": \"LIMIT\",
            \"price\": $price,
            \"quantity\": $quantity
        }" > /dev/null 2>&1
}

# Create test accounts
create_accounts() {
    echo "📁 Creating test accounts..."
    
    for account in "${ACCOUNTS[@]}"; do
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

# Check simulation status and start if needed
ensure_simulation() {
    echo -n "Checking simulation status... "
    status=$(curl -s "$API_BASE/simulation/status" 2>/dev/null || echo '{"enabled":false}')
    if echo "$status" | grep -q '"enabled":true'; then
        echo "✓ Already running"
    else
        echo "✗ Not running"
        echo -e "\n🔧 Starting simulation..."
        curl -s -X POST "$API_BASE/simulation/start" > /dev/null 2>&1
        sleep 3
    fi
}

# Open market for all symbols
open_market() {
    echo -e "\n🔔 Opening market..."
    curl -s -X POST "$API_BASE/market/open" > /dev/null 2>&1
    sleep 2
}

# Ensure bid/ask exists for a symbol
ensure_bid_ask_for_symbol() {
    local symbol=$1
    local base_price=${BASE_PRICES[$symbol]}
    
    # Get current quote
    quote=$(curl -s "$API_BASE/market-data/quote/$symbol")
    
    if command -v jq &> /dev/null; then
        bid_price=$(echo "$quote" | jq -r '.bidPrice // "null"')
        ask_price=$(echo "$quote" | jq -r '.askPrice // "null"')
        
        # If bid is missing, place bid orders
        if [[ "$bid_price" == "null" ]]; then
            for i in {0..2}; do
                price=$(echo "$base_price - 0.10 - 0.05 * $i" | bc)
                quantity=$((1000 + RANDOM % 1000))
                place_order "MM001" "$symbol" "BUY" "$price" "$quantity"
            done
            echo "  Added bids for $symbol"
        fi
        
        # If ask is missing, place ask orders
        if [[ "$ask_price" == "null" ]]; then
            for i in {0..2}; do
                price=$(echo "$base_price + 0.10 + 0.05 * $i" | bc)
                quantity=$((1000 + RANDOM % 1000))
                place_order "MM002" "$symbol" "SELL" "$price" "$quantity"
            done
            echo "  Added asks for $symbol"
        fi
    fi
}

# Ensure all symbols have bid/ask
ensure_all_bid_ask() {
    echo "🔍 Ensuring bid/ask presence..."
    for symbol in "${SYMBOLS[@]}"; do
        ensure_bid_ask_for_symbol "$symbol"
    done
}

# Place comprehensive orders for a symbol
place_symbol_orders() {
    local symbol=$1
    local base_price=${BASE_PRICES[$symbol]}
    
    echo -n "  $symbol (base: \$$base_price)... "
    
    # Place bid orders (5 levels)
    for i in {0..4}; do
        price=$(echo "$base_price - 0.05 - 0.05 * $i" | bc)
        quantity=$((500 + RANDOM % 1500))
        place_order "MM001" "$symbol" "BUY" "$price" "$quantity"
    done
    
    # Place ask orders (5 levels)
    for i in {0..4}; do
        price=$(echo "$base_price + 0.05 + 0.05 * $i" | bc)
        quantity=$((500 + RANDOM % 1500))
        place_order "MM002" "$symbol" "SELL" "$price" "$quantity"
    done
    
    echo "✓"
}

# Place initial orders for all symbols
place_initial_orders() {
    echo "📊 Placing initial orders..."
    
    for symbol in "${SYMBOLS[@]}"; do
        place_symbol_orders "$symbol"
    done
    
    sleep 2
    ensure_all_bid_ask
}

# Display market data
show_market_data() {
    echo -e "\n📈 Market Data:"
    echo "==============="
    
    response=$(curl -s "$API_BASE/market-data/quotes")
    
    if command -v jq &> /dev/null; then
        echo "$response" | jq -r '.[] | select(.symbol != "") | "
\(.symbol):
  Bid: $\(.bidPrice // "null") × \(.bidSize // 0)
  Ask: $\(.askPrice // "null") × \(.askSize // 0)
  Last: $\(.lastPrice // "null")
  Volume: \(.volume // 0)
"'
        
        # Check for missing prices
        if echo "$response" | grep -q '"bidPrice":null\|"askPrice":null'; then
            echo -e "\n⚠️  Some bid/ask prices are missing!"
            echo "Run: ./scripts/market-simulator.sh --fix"
        else
            echo -e "\n✅ All symbols have bid/ask prices!"
        fi
    else
        echo "$response"
    fi
}