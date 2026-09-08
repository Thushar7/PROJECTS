@echo off
echo ============================================
echo USER SERVICE API TESTING ON PORT 8081
echo ============================================
echo.

echo 1. Testing /register endpoint...
curl -X POST http://localhost:8081/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser1\",\"email\":\"testuser1@example.com\",\"password\":\"password123\",\"role\":\"USER\"}" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -s
echo.

echo 2. Testing /login endpoint...
curl -X POST http://localhost:8081/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser1\",\"password\":\"password123\"}" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -s
echo.

echo 3. Testing /profile GET endpoint (should fail without token)...
curl -X GET http://localhost:8081/profile ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -s
echo.

echo 4. Testing /profile PUT endpoint (should fail without token)...
curl -X PUT http://localhost:8081/profile ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"updateduser\",\"email\":\"updated@example.com\"}" ^
  -w "\nStatus Code: %%{http_code}\n" ^
  -s
echo.

echo ============================================
echo API TESTING COMPLETED
echo ============================================
