#!/bin/bash

# MySQL Setup Script for E-Commerce Backend
# This script safely creates the database if it doesn't exist

echo "==================================="
echo "MySQL Database Setup for E-Commerce"
echo "==================================="

# Default values
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="ecommerce"
DB_USER="root"
DB_PASS=""

# Function to check if MySQL is running
check_mysql_running() {
    if command -v mysql &> /dev/null; then
        mysql -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1" &> /dev/null
        return $?
    else
        echo "Error: MySQL client not found. Please install MySQL first."
        exit 1
    fi
}

# Function to create database if it doesn't exist
create_database_if_not_exists() {
    echo "Checking if database '$DB_NAME' exists..."
    
    # Check if database exists
    DB_EXISTS=$(mysql -u"$DB_USER" -p"$DB_PASS" -e "SHOW DATABASES LIKE '$DB_NAME';" 2>/dev/null | grep "$DB_NAME")
    
    if [ -z "$DB_EXISTS" ]; then
        echo "Database '$DB_NAME' does not exist. Creating it now..."
        mysql -u"$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
        
        if [ $? -eq 0 ]; then
            echo "✓ Database '$DB_NAME' created successfully!"
        else
            echo "✗ Failed to create database. Please check your MySQL credentials."
            exit 1
        fi
    else
        echo "✓ Database '$DB_NAME' already exists."
    fi
}

# Main execution
echo ""
echo "Attempting to connect to MySQL..."
echo "(Using user: $DB_USER, password: [empty])"
echo ""

# Check if MySQL is running
if ! check_mysql_running; then
    echo ""
    echo "Could not connect to MySQL. Possible reasons:"
    echo "1. MySQL is not running. Start it with:"
    echo "   - macOS: brew services start mysql"
    echo "   - Linux: sudo systemctl start mysql"
    echo "   - Windows: net start MySQL"
    echo ""
    echo "2. The root user has a password. If so, update the password in:"
    echo "   - backend/src/main/resources/application.yml"
    echo "   - backend/pom.xml (Flyway configuration)"
    echo ""
    exit 1
fi

echo "✓ Successfully connected to MySQL"
echo ""

# Create database if it doesn't exist
create_database_if_not_exists

echo ""
echo "==================================="
echo "Setup completed successfully!"
echo "==================================="
echo ""
echo "You can now run the application with:"
echo "  cd backend"
echo "  mvn spring-boot:run"
echo ""
echo "The application will automatically:"
echo "- Create all necessary tables"
echo "- Insert sample data (users, addresses)"
echo ""
echo "Default test users will be created:"
echo "- admin@ecommerce.com / password"
echo "- seller@ecommerce.com / password"
echo "- buyer@ecommerce.com / password"
echo ""