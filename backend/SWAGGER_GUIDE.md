# Swagger/OpenAPI Documentation Guide

## Overview

The E-Commerce Platform API is documented using OpenAPI 3.0 (Swagger). This provides interactive API documentation that allows you to explore and test the API endpoints directly from your browser.

## Accessing Swagger UI

Once the backend application is running, you can access the Swagger UI at:

```
http://localhost:8080/api/swagger-ui.html
```

## Features

### Interactive Documentation
- Browse all available API endpoints organized by tags
- View request/response schemas
- See example requests and responses
- Understand required parameters and authentication

### API Testing
- Execute API calls directly from the browser
- Authenticate using JWT tokens
- Test different scenarios with various parameters
- View actual responses from the server

## Authentication

Most endpoints require JWT authentication. To test authenticated endpoints:

1. First, use the `/auth/login` endpoint to obtain a JWT token
2. Click the "Authorize" button in Swagger UI
3. Enter the token in the format: `Bearer YOUR_JWT_TOKEN`
4. Now you can test protected endpoints

## API Documentation URLs

- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/api/v3/api-docs.yaml`

## Configuration

The OpenAPI documentation is configured in:
- `OpenApiConfig.java` - Main configuration class
- `application.yml` - SpringDoc properties

## Adding Documentation to New Endpoints

To document new endpoints, use these annotations:

```java
@Operation(summary = "Short description", description = "Detailed description")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Success",
        content = @Content(schema = @Schema(implementation = YourResponseClass.class))),
    @ApiResponse(responseCode = "400", description = "Bad request")
})
@SecurityRequirement(name = "bearerAuth")  // For protected endpoints
```

## Supported Endpoints

Currently documented endpoint groups:
- **Authentication** - User registration, login, token refresh
- **Products** - Product CRUD, search, and filtering
- **Categories** - Category management
- **Cart** - Shopping cart operations
- **Orders** - Order placement and management
- **Users** - User profile and address management
- **Stores** - Store management for sellers
- **Payments** - Payment processing with Stripe

## Development Tips

1. Always run `mvn compile` after adding new Swagger annotations
2. Use meaningful operation summaries and descriptions
3. Document all possible response codes
4. Include example values in your DTOs using `@Schema(example = "value")`
5. Group related endpoints using `@Tag` annotation