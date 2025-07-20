# Database Guide

## Overview

This project uses H2 database with Flyway migrations for version control. The database files are automatically generated and should **never** be committed to git.

## Database Files (In .gitignore)

### ❌ **Never Commit These Files:**
- `backend/data/ecommerce.mv.db` - Main H2 database file  
- `backend/data/ecommerce.trace.db` - H2 trace/debug file
- `backend/data/ecommerce.lock.db` - H2 lock file
- `backend/data/` - Entire data directory

### ✅ **Always Commit These:**
- `backend/src/main/resources/db/migration/V*.sql` - Flyway migration scripts
- `backend/src/main/resources/application.yml` - Database configuration
- `backend/DATABASE_SETUP.md` - Migration setup guide

## Why Database Files Are Ignored

1. **Runtime Data** - Contains actual user data, passwords, etc.
2. **Environment-Specific** - Each developer needs their own database
3. **Security** - May contain sensitive test data
4. **Large Size** - Can slow down git operations
5. **Binary Format** - Creates merge conflicts

## Development Workflow

### First Time Setup
```bash
cd backend
./db-migrate.sh migrate
```

### Adding New Migrations
1. Create new file: `V{next_number}__{description}.sql`
2. Add your SQL changes
3. Run: `./db-migrate.sh migrate`
4. Commit only the migration file

### Resetting Database
```bash
# WARNING: This deletes all data!
./db-migrate.sh clean
./db-migrate.sh migrate
```

## Sample Data

The database comes with test accounts (see `V2__Add_sample_data.sql`):
- `admin@ecommerce.com` / `password` (ADMIN)
- `seller@ecommerce.com` / `password` (SELLER)  
- `buyer@ecommerce.com` / `password` (BUYER)

## Production Deployment

For production, update `application.yml` to use PostgreSQL:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

The same Flyway migrations will work with PostgreSQL since we use PostgreSQL-compatible H2 mode.