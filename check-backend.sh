#!/bin/bash

echo "🔍 Checking Backend Status"
echo "========================="

# Check if backend is running
echo "1️⃣ Checking if backend is running on port 8080..."
if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Backend is running!"
    
    echo ""
    echo "2️⃣ Testing health endpoint:"
    curl -s http://localhost:8080/api/health | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/health
    
    echo ""
    echo "3️⃣ Testing cart endpoint (should not return 403):"
    response=$(curl -s -w "HTTP Status: %{http_code}" http://localhost:8080/api/cart)
    echo "$response"
    
else
    echo "❌ Backend is not running on http://localhost:8080"
    echo ""
    echo "To start the backend:"
    echo "  cd backend"
    echo "  mvn spring-boot:run"
    echo ""
    echo "Or check if it's running on a different port:"
    echo "  lsof -i :8080"
fi

echo ""
echo "📝 If backend is running but cart returns 403:"
echo "   1. Check SecurityConfig.java cart endpoint permissions"
echo "   2. Look at backend logs for error details"
echo "   3. Ensure MySQL/H2 database is accessible"