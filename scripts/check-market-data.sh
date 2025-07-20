#!/bin/bash

# Simple script to check if market data is being generated

API_BASE="http://localhost:20010/api"

echo "🔍 Checking Market Data Status"
echo "=============================="

# Check if API is available
echo -n "API Status: "
if curl -s -f "$API_BASE/market-data/symbols" > /dev/null 2>&1; then
    echo "✓ Running"
else
    echo "✗ Not Available"
    echo "Please ensure the backend is running with: docker-compose up"
    exit 1
fi

# Check market quotes
echo -e "\n📊 Market Quotes:"
echo "----------------"

response=$(curl -s "$API_BASE/market-data/quotes")

if command -v jq &> /dev/null; then
    # Pretty print with jq if available
    echo "$response" | jq -r '.[] | "
Symbol: \(.symbol)
  Bid: $\(.bidPrice // "null") × \(.bidSize // 0)
  Ask: $\(.askPrice // "null") × \(.askSize // 0)
  Last: $\(.lastPrice // "null")
  Volume: \(.volume // 0)
"'
else
    # Basic output without jq
    echo "$response" | grep -E "(symbol|bidPrice|askPrice)" || echo "No data"
fi

# Check if prices are null
if echo "$response" | grep -q '"bidPrice":null'; then
    echo -e "\n⚠️  WARNING: Bid/Ask prices are null!"
    echo "The market simulator may not be running properly."
    echo ""
    echo "To fix this, try:"
    echo "1. Restart the backend: docker-compose restart backend"
    echo "2. Run the market simulation script: ./scripts/simulate-market.sh"
    echo "3. Check backend logs: docker-compose logs backend | grep -i simulation"
else
    echo -e "\n✅ Market data is being generated successfully!"
fi