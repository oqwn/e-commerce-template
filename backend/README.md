# E-Commerce Backend

## Quick Start

### Development Mode (No Environment Variables Required)

```bash
# Option 1: Run with default dummy values
mvn spring-boot:run

# Option 2: Run with development profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Production Mode

Copy `.env.example` to `.env` and update with your actual values:

```bash
cp .env.example .env
# Edit .env with your values
```

## Environment Variables

See `.env.example` for all available environment variables.

For local development, the application provides safe default values, so you can start immediately without configuration.