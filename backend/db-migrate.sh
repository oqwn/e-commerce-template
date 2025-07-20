#!/bin/bash

# Database Migration Script for E-Commerce Backend
# This script helps manage database migrations using Flyway

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven first."
    exit 1
fi

# Function to show usage
show_usage() {
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  info      - Show current migration status"
    echo "  migrate   - Run pending migrations"
    echo "  validate  - Validate migrations"
    echo "  clean     - Clean database (WARNING: This will delete all data!)"
    echo "  repair    - Repair migration checksums"
    echo "  baseline  - Baseline existing database"
    echo ""
}

# Parse command
COMMAND=$1

case $COMMAND in
    "info")
        print_info "Checking migration status..."
        mvn flyway:info
        ;;
    "migrate")
        print_info "Running database migrations..."
        mvn flyway:migrate
        if [ $? -eq 0 ]; then
            print_info "Migrations completed successfully!"
        else
            print_error "Migration failed!"
            exit 1
        fi
        ;;
    "validate")
        print_info "Validating migrations..."
        mvn flyway:validate
        ;;
    "clean")
        print_warning "This will DELETE ALL DATA in the database!"
        read -p "Are you sure? (yes/no): " confirm
        if [ "$confirm" == "yes" ]; then
            print_info "Cleaning database..."
            mvn flyway:clean
            print_info "Database cleaned. Run 'migrate' to recreate schema."
        else
            print_info "Clean cancelled."
        fi
        ;;
    "repair")
        print_info "Repairing migration checksums..."
        mvn flyway:repair
        ;;
    "baseline")
        print_info "Creating baseline for existing database..."
        mvn flyway:baseline
        ;;
    *)
        show_usage
        exit 1
        ;;
esac