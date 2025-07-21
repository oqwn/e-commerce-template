# Cart Testing Guide

## Prerequisites
1. Make sure both backend and frontend are running:
   ```bash
   # Terminal 1 - Backend
   cd backend
   mvn spring-boot:run
   
   # Terminal 2 - Frontend  
   cd frontend
   pnpm run dev
   ```

2. Open browser at http://localhost:5173

## Test Scenarios

### 1. Guest User Cart (No Login)
- [ ] Navigate to buyer products page (/buyer/products)
- [ ] Click "Add to Cart" on any product
- [ ] Check if cart icon shows item count
- [ ] Click cart icon to view cart
- [ ] Try changing quantity with +/- buttons
- [ ] Remove an item from cart
- [ ] Add multiple items
- [ ] Click "Proceed to Checkout" (should redirect to login)

### 2. Authenticated User Cart
- [ ] Register/Login as a buyer
- [ ] Add items to cart
- [ ] View cart
- [ ] Change quantities
- [ ] Remove items
- [ ] Clear entire cart
- [ ] Proceed to checkout

### 3. Checkout Flow Test
- [ ] From cart, click "Proceed to Checkout"
- [ ] Add a new address
- [ ] Select shipping method
- [ ] Review order summary
- [ ] Complete order (payment UI only)
- [ ] View order confirmation

## API Endpoints to Test

### Using curl or Postman:

1. **Get Cart (Guest)**
   ```bash
   curl -X GET http://localhost:8080/api/cart \
     -H "Content-Type: application/json" \
     --cookie-jar cookies.txt
   ```

2. **Add to Cart (Guest)**
   ```bash
   curl -X POST http://localhost:8080/api/cart/items \
     -H "Content-Type: application/json" \
     -d '{"productId": 1, "quantity": 2}' \
     --cookie cookies.txt
   ```

3. **Update Cart Item**
   ```bash
   curl -X PUT http://localhost:8080/api/cart/items/1 \
     -H "Content-Type: application/json" \
     -d '{"quantity": 3}' \
     --cookie cookies.txt
   ```

4. **Remove from Cart**
   ```bash
   curl -X DELETE http://localhost:8080/api/cart/items/1 \
     --cookie cookies.txt
   ```

## Expected Results
- No 403 errors
- Cart persists for guest users via session
- Cart transfers when guest user logs in
- All CRUD operations work smoothly
- Error messages display properly

## Console Checks
Open browser DevTools (F12) and check:
- Network tab: No 403 errors on cart endpoints
- Console: No JSON parsing errors
- Application > Local Storage: Check for accessToken after login