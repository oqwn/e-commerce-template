# Database Setup and Migration Guide

## Overview

This e-commerce backend uses Flyway for database version control and migrations. This ensures consistent database schema across all environments and makes it easy to track and apply database changes.

## Quick Start

### 1. Initial Setup

For first-time setup or after cloning the repository:

```bash
# Navigate to backend directory
cd backend

# Run initial migration
./db-migrate.sh migrate
```

This will:
- Create the database file in `./data/ecommerce.mv.db`
- Apply all migration scripts in order
- Insert sample data for development

### 2. Check Migration Status

```bash
./db-migrate.sh info
```

This shows all migrations and their current status.

## Database Configuration

### Development (H2 Database)

The default configuration uses H2 database with PostgreSQL compatibility mode:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/ecommerce;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password: 
```

Access H2 Console: http://localhost:8080/api/h2-console

### Production (PostgreSQL)

For production, update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

## Migration Management

### Creating New Migrations

1. Create a new file in `src/main/resources/db/migration/`
2. Name it following the pattern: `V{version}__{description}.sql`
   - Example: `V3__Add_products_table.sql`

### Migration Commands

```bash
# Run pending migrations
./db-migrate.sh migrate

# Validate migrations
./db-migrate.sh validate

# Show migration history
./db-migrate.sh info

# Clean database (WARNING: Deletes all data!)
./db-migrate.sh clean

# Repair checksums after manual changes
./db-migrate.sh repair
```

## Migration Best Practices

1. **Never modify existing migrations** - Create new migrations for changes
2. **Test migrations locally** before committing
3. **Use descriptive names** for migration files
4. **Keep migrations small and focused** on a single change
5. **Always provide rollback strategy** in comments

## Current Schema

### Users Table
- Authentication and user profile information
- Roles: BUYER, SELLER, ADMIN
- Email verification and password reset tokens

### Addresses Table
- Multiple addresses per user
- Types: HOME, WORK, OTHER
- One default address per user

## Sample Data

The following test accounts are created by migration V2:

| Email | Password | Role | Verified |
|-------|----------|------|----------|
| admin@ecommerce.com | password | ADMIN | Yes |
| seller@ecommerce.com | password | SELLER | Yes |
| buyer@ecommerce.com | password | BUYER | Yes |

## Troubleshooting

### Migration Failed

1. Check the error message in console
2. Fix the SQL syntax in the migration file
3. If migration was partially applied:
   ```bash
   ./db-migrate.sh repair
   ./db-migrate.sh migrate
   ```

### Reset Database

To completely reset the database:

```bash
# WARNING: This deletes all data!
./db-migrate.sh clean
./db-migrate.sh migrate
```

### Manual Database Access

```bash
# Start the application
mvn spring-boot:run

# Access H2 Console
# URL: http://localhost:8080/api/h2-console
# JDBC URL: jdbc:h2:file:./data/ecommerce
# Username: sa
# Password: (leave empty)
```

## CI/CD Integration

For automated deployments:

```bash
# In your CI/CD pipeline
mvn flyway:migrate -Dflyway.url=$DB_URL -Dflyway.user=$DB_USER -Dflyway.password=$DB_PASSWORD
```

## Backup and Restore

### Backup
```bash
# For H2
cp ./data/ecommerce.mv.db ./backups/ecommerce-$(date +%Y%m%d).mv.db

# For PostgreSQL
pg_dump -U username -d ecommerce > backup.sql
```

### Restore
```bash
# For H2
cp ./backups/ecommerce-20240120.mv.db ./data/ecommerce.mv.db

# For PostgreSQL
psql -U username -d ecommerce < backup.sql
```