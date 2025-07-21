#!/bin/bash

# Cart API Testing Script
echo "🧪 Testing Cart API Endpoints"
echo "================================"

BASE_URL="http://localhost:8080/api"
COOKIE_JAR="test-cookies.txt"

# Test 1: Get Cart (should work without auth)
echo "1️⃣ Testing GET /cart (guest user)"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/cart" \
  -H "Content-Type: application/json" \
  --cookie-jar "$COOKIE_JAR" \
  -o response1.json)

if [ "$response" = "200" ]; then
    echo "✅ GET /cart: Success (200)"
    cat response1.json | jq '.' 2>/dev/null || cat response1.json
else
    echo "❌ GET /cart: Failed ($response)"
    cat response1.json
fi

echo ""

# Test 2: Add to Cart (should work without auth)
echo "2️⃣ Testing POST /cart/items (guest user)"
response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/cart/items" \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}' \
  --cookie "$COOKIE_JAR" \
  -o response2.json)

if [ "$response" = "200" ]; then
    echo "✅ POST /cart/items: Success (200)"
    cat response2.json | jq '.' 2>/dev/null || cat response2.json
else
    echo "❌ POST /cart/items: Failed ($response)"
    cat response2.json
fi

echo ""

# Test 3: Update Cart Item (should work without auth)
echo "3️⃣ Testing PUT /cart/items/1 (guest user)"
response=$(curl -s -w "%{http_code}" -X PUT "$BASE_URL/cart/items/1" \
  -H "Content-Type: application/json" \
  -d '{"quantity": 3}' \
  --cookie "$COOKIE_JAR" \
  -o response3.json)

if [ "$response" = "200" ]; then
    echo "✅ PUT /cart/items/1: Success (200)"
    cat response3.json | jq '.' 2>/dev/null || cat response3.json
else
    echo "❌ PUT /cart/items/1: Failed ($response)"
    cat response3.json
fi

echo ""

# Test 4: Get Cart Count
echo "4️⃣ Testing GET /cart/count"
response=$(curl -s -w "%{http_code}" -X GET "$BASE_URL/cart/count" \
  --cookie "$COOKIE_JAR" \
  -o response4.json)

if [ "$response" = "200" ]; then
    echo "✅ GET /cart/count: Success (200)"
    cat response4.json | jq '.' 2>/dev/null || cat response4.json
else
    echo "❌ GET /cart/count: Failed ($response)"
    cat response4.json
fi

echo ""

# Test 5: Remove from Cart
echo "5️⃣ Testing DELETE /cart/items/1"
response=$(curl -s -w "%{http_code}" -X DELETE "$BASE_URL/cart/items/1" \
  --cookie "$COOKIE_JAR" \
  -o response5.json)

if [ "$response" = "200" ]; then
    echo "✅ DELETE /cart/items/1: Success (200)"
    cat response5.json | jq '.' 2>/dev/null || cat response5.json
else
    echo "❌ DELETE /cart/items/1: Failed ($response)"
    cat response5.json
fi

echo ""
echo "🧹 Cleaning up test files..."
rm -f response*.json test-cookies.txt

echo "✅ Cart API testing complete!"
echo ""
echo "Next steps:"
echo "1. Start the backend: cd backend && mvn spring-boot:run"
echo "2. Start the frontend: cd frontend && pnpm run dev"
echo "3. Run this script: ./test-api.sh"