@echo off
REM MySQL Setup Script for E-Commerce Backend (Windows)
REM This script safely creates the database if it doesn't exist

echo ===================================
echo MySQL Database Setup for E-Commerce
echo ===================================
echo.

REM Default values
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=ecommerce
set DB_USER=root
set DB_PASS=

echo Attempting to connect to MySQL...
echo (Using user: %DB_USER%, password: [empty])
echo.

REM Check if MySQL is accessible
mysql -u%DB_USER% -e "SELECT 1" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Could not connect to MySQL. Possible reasons:
    echo 1. MySQL is not running. Start it from Services or:
    echo    - Run: net start MySQL
    echo.
    echo 2. The root user has a password. If so, update the password in:
    echo    - backend\src\main\resources\application.yml
    echo    - backend\pom.xml ^(Flyway configuration^)
    echo.
    exit /b 1
)

echo [OK] Successfully connected to MySQL
echo.

echo Checking if database '%DB_NAME%' exists...

REM Check if database exists and create if not
mysql -u%DB_USER% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [OK] Database '%DB_NAME%' is ready!
) else (
    echo [ERROR] Failed to create database. Please check your MySQL installation.
    exit /b 1
)

echo.
echo ===================================
echo Setup completed successfully!
echo ===================================
echo.
echo You can now run the application with:
echo   cd backend
echo   mvn spring-boot:run
echo.
echo The application will automatically:
echo - Create all necessary tables
echo - Insert sample data (users, addresses)
echo.
echo Default test users will be created:
echo - admin@ecommerce.com / password
echo - seller@ecommerce.com / password  
echo - buyer@ecommerce.com / password
echo.
pause