#!/bin/bash

# Consolidated Market Simulator Script
# Usage: ./market-simulator.sh [OPTIONS]
#   --check    Check market data status only
#   --fix      Fix missing bid/ask prices only  
#   --setup    Setup accounts and initial orders only
#   --trade    Start continuous trading simulation
#   (no args) Complete setup + trading simulation

# Load utility functions
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/market-utils.sh"

show_usage() {
    cat << EOF
Market Simulator - Trading System Setup and Simulation

Usage: $0 [OPTION]

OPTIONS:
    --check     Check market data status and show current prices
    --fix       Fix missing bid/ask prices for all symbols
    --setup     Setup accounts, open market, and place initial orders
    --trade     Start continuous trading simulation
    --help      Show this help message

Examples:
    $0                 # Complete setup + trading simulation
    $0 --check         # Just check current market status
    $0 --fix           # Fix any missing bid/ask prices
    $0 --setup         # Setup market without continuous trading

EOF
}

# Continuous trading simulation
start_trading() {
    echo -e "\n🔄 Starting continuous trading simulation..."
    echo "Press Ctrl+C to stop"
    echo "================================"
    
    order_count=0
    
    while true; do
        # Random symbol and account
        symbol=${SYMBOLS[$RANDOM % ${#SYMBOLS[@]}]}
        account=${ACCOUNTS[$RANDOM % ${#ACCOUNTS[@]}]}
        base_price=${BASE_PRICES[$symbol]}
        
        # Get current quote
        quote=$(curl -s "$API_BASE/market-data/quote/$symbol")
        
        if command -v jq &> /dev/null; then
            bid_price=$(echo "$quote" | jq -r '.bidPrice // 0')
            ask_price=$(echo "$quote" | jq -r '.askPrice // 0')
        else
            bid_price=$base_price
            ask_price=$base_price
        fi
        
        # Use base price if missing
        if [[ "$bid_price" == "0" || "$bid_price" == "null" ]]; then
            bid_price=$(echo "$base_price - 0.10" | bc)
        fi
        if [[ "$ask_price" == "0" || "$ask_price" == "null" ]]; then
            ask_price=$(echo "$base_price + 0.10" | bc)
        fi
        
        # Random action: 80% limit orders, 20% market orders
        if [[ $((RANDOM % 100)) -lt 80 ]]; then
            # Place limit order
            side=$([[ $((RANDOM % 2)) -eq 0 ]] && echo "BUY" || echo "SELL")
            
            if [[ "$side" == "BUY" ]]; then
                offset=$(echo "0.01 * $((RANDOM % 3))" | bc)
                price=$(echo "$bid_price - $offset" | bc)
            else
                offset=$(echo "0.01 * $((RANDOM % 3))" | bc)
                price=$(echo "$ask_price + $offset" | bc)
            fi
            
            quantity=$((100 + RANDOM % 500))
            
            echo -n "$(date +%H:%M:%S) - $account $side $quantity $symbol @ \$$price... "
            place_order "$account" "$symbol" "$side" "$price" "$quantity"
            echo "✓"
        else
            # Place market order (simplified for demo)
            side=$([[ $((RANDOM % 2)) -eq 0 ]] && echo "BUY" || echo "SELL")
            quantity=$((50 + RANDOM % 200))
            
            echo -n "$(date +%H:%M:%S) - $account MARKET $side $quantity $symbol... "
            # For market orders, use current bid/ask as limit price
            price=$([[ "$side" == "BUY" ]] && echo "$ask_price" || echo "$bid_price")
            place_order "$account" "$symbol" "$side" "$price" "$quantity"
            echo "✓"
        fi
        
        # Every 20 orders, ensure bid/ask presence
        order_count=$((order_count + 1))
        if [[ $((order_count % 20)) -eq 0 ]]; then
            ensure_all_bid_ask
        fi
        
        # Sleep 1-3 seconds
        sleep $((1 + RANDOM % 3))
    done
}

# Main execution
main() {
    case "${1:-}" in
        --help|-h)
            show_usage
            exit 0
            ;;
        --check)
            echo "🔍 Checking Market Data"
            echo "======================"
            check_api || exit 1
            show_market_data
            ;;
        --fix)
            echo "🔧 Fixing Missing Bid/Ask Prices"
            echo "================================"
            check_api || exit 1
            ensure_all_bid_ask
            show_market_data
            ;;
        --setup)
            echo "⚡ Market Setup"
            echo "=============="
            check_api || exit 1
            ensure_simulation
            open_market
            create_accounts
            place_initial_orders
            show_market_data
            ;;
        --trade)
            echo "🚀 Starting Trading Simulation"
            echo "=============================="
            check_api || exit 1
            start_trading
            ;;
        "")
            echo "🚀 Complete Market Simulation Setup"
            echo "==================================="
            check_api || exit 1
            ensure_simulation
            open_market
            create_accounts
            place_initial_orders
            show_market_data
            start_trading
            ;;
        *)
            echo "Error: Unknown option '$1'"
            echo ""
            show_usage
            exit 1
            ;;
    esac
}

# Handle Ctrl+C gracefully
trap 'echo -e "\n\n👋 Simulation stopped. Market data preserved."; exit 0' INT

# Run main function
main "$@"