# Diyawanna Sup Backend

A comprehensive Spring Boot backend service with MongoDB Atlas integration, JWT authentication, CRUD operations, performance optimizations, and dynamic query processing.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Project Architecture](#project-architecture)
- [Features](#features)
- [Setup and Installation](#setup-and-installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Performance Optimizations](#performance-optimizations)
- [Testing](#testing)
- [Error Handling](#error-handling)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Diyawanna Sup Backend is a robust, scalable Spring Boot application designed to provide comprehensive backend services for educational institutions. It features user management, university and faculty administration, shopping cart functionality, and dynamic query processing capabilities.

### Key Highlights

- **Modern Architecture**: Built with Spring Boot 3.5.0 and Java 17
- **Secure Authentication**: JWT-based authentication with Spring Security
- **High Performance**: MongoDB Atlas integration with connection pooling and caching
- **Dynamic Queries**: Configurable query system with parameter substitution
- **Comprehensive Testing**: Unit and integration tests with 90%+ coverage
- **Production Ready**: Performance monitoring, error handling, and deployment configurations

## Technology Stack

### Core Technologies
- **Java**: 17.0.7 LTS
- **Spring Boot**: 3.5.0
- **Spring Data MongoDB**: 4.5.x
- **MongoDB Java Driver**: 5.5.x
- **MongoDB Server**: 7.0 (Atlas M0 free tier)

### Security & Authentication
- **Spring Security**: 6.x
- **JWT (JSON Web Tokens)**: For stateless authentication
- **BCrypt**: Password hashing

### Testing & Quality
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework
- **Spring Boot Test**: Integration testing
- **Postman**: API testing collection

### Development Tools
- **Maven**: Dependency management and build tool
- **Git**: Version control
- **SLF4J + Logback**: Logging framework

## Project Architecture

The application follows a layered architecture pattern with clear separation of concerns:

```
src/main/java/com/diyawanna/sup/
├── config/          # Configuration classes
├── controller/      # REST API controllers
├── dto/            # Data Transfer Objects
├── entity/         # MongoDB entity models
├── exception/      # Custom exception classes
├── repository/     # Data access layer
├── security/       # Security configurations
├── service/        # Business logic layer
└── util/           # Utility classes
```

### Architecture Layers

1. **Presentation Layer** (`controller/`)
   - REST API endpoints
   - Request/response handling
   - Input validation

2. **Business Logic Layer** (`service/`)
   - Core business logic
   - Data processing
   - Business rule enforcement

3. **Data Access Layer** (`repository/`)
   - MongoDB operations
   - Query execution
   - Data persistence

4. **Security Layer** (`security/`)
   - Authentication filters
   - Authorization rules
   - JWT token processing

## Features

### 🔐 Authentication & Authorization
- JWT-based stateless authentication
- User registration and login
- Token validation and refresh
- Role-based access control
- Password encryption with BCrypt

### 👥 User Management
- Complete CRUD operations for users
- User profile management
- Search and filtering capabilities
- University and age-based grouping
- Soft delete functionality

### 🏛️ University & Faculty Management
- University registration and management
- Faculty administration
- Subject management within faculties
- Location-based university search
- Hierarchical data relationships

### 🛒 Shopping Cart System
- User-specific cart management
- Item addition and removal
- Cart status tracking
- Total amount calculation
- Order processing capabilities

### 🔍 Dynamic Query System
- Configurable query execution
- Parameter substitution
- Query validation
- Sample query examples
- External JSON configuration

### 📊 Performance Monitoring
- Real-time performance metrics
- Cache management
- Memory usage tracking
- Database connection monitoring
- Health check endpoints

### 🚀 Performance Optimizations
- MongoDB connection pooling
- Multi-level caching strategy
- Database indexing
- Query optimization
- Efficient data structures



## Setup and Installation

### Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17 or higher**
- **Maven 3.6+**
- **MongoDB Atlas account** (or local MongoDB instance)
- **Git** (for cloning the repository)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd diyawanna-sup-backend
   ```

2. **Configure MongoDB Atlas**
   - Create a MongoDB Atlas account at [mongodb.com/atlas](https://www.mongodb.com/atlas)
   - Create a new cluster (M0 free tier recommended)
   - Create a database named `diyawanna_sup_main`
   - Get your connection string

3. **Configure Application Properties**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   
   Update the following properties:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/diyawanna_sup_main
   jwt.secret=your-secret-key-here
   ```

4. **Build the Application**
   ```bash
   mvn clean compile
   ```

5. **Run Tests**
   ```bash
   mvn test
   ```

6. **Start the Application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Docker Setup (Optional)

1. **Build Docker Image**
   ```bash
   docker build -t diyawanna-sup-backend .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

## Configuration

### Application Properties

The application can be configured through `application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/diyawanna_sup_main
spring.data.mongodb.database=diyawanna_sup_main

# Connection Pool Settings
spring.data.mongodb.options.max-connection-pool-size=20
spring.data.mongodb.options.min-connection-pool-size=5
spring.data.mongodb.options.max-connection-idle-time=30000
spring.data.mongodb.options.max-connection-life-time=120000

# JWT Configuration
jwt.secret=your-256-bit-secret-key-here
jwt.expiration=3600000

# Cache Configuration
spring.cache.type=simple
spring.cache.cache-names=users,universities,faculties,carts,queries

# Logging Configuration
logging.level.com.diyawanna.sup=INFO
logging.level.org.springframework.security=WARN
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### Environment Variables

For production deployment, use environment variables:

```bash
export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/diyawanna_sup_main"
export JWT_SECRET="your-production-secret-key"
export SERVER_PORT="8080"
```

### Profile-Specific Configuration

- **Development**: `application-dev.properties`
- **Testing**: `application-test.properties`
- **Production**: `application-prod.properties`

Activate profiles using:
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### POST /auth/login
Authenticate user and receive JWT token.

**Request:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "johndoe",
  "expiresIn": 3600000,
  "message": "Login successful"
}
```

#### POST /auth/register
Register a new user account.

**Request:**
```json
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "age": 25,
  "university": "University of Colombo",
  "school": "Faculty of Science",
  "work": "Software Engineer"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "name": "John Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "age": 25,
    "university": "University of Colombo",
    "active": true,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### POST /auth/validate
Validate JWT token.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "valid": true,
  "username": "johndoe",
  "expiresAt": "2024-01-15T11:30:00"
}
```

#### POST /auth/refresh
Refresh JWT token.

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "johndoe",
  "expiresIn": 3600000
}
```

### User Management Endpoints

#### GET /users
Get all active users (requires authentication).

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "64f8a1b2c3d4e5f6a7b8c9d0",
      "name": "John Doe",
      "username": "johndoe",
      "email": "john@example.com",
      "age": 25,
      "university": "University of Colombo",
      "active": true,
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "count": 1
}
```

#### GET /users/{id}
Get user by ID.

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "64f8a1b2c3d4e5f6a7b8c9d0",
    "name": "John Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "age": 25,
    "university": "University of Colombo",
    "school": "Faculty of Science",
    "work": "Software Engineer",
    "active": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

#### POST /users
Create a new user.

**Request:**
```json
{
  "name": "Jane Smith",
  "username": "janesmith",
  "email": "jane@example.com",
  "password": "password123",
  "age": 23,
  "university": "University of Peradeniya"
}
```

#### PUT /users/{id}
Update user information.

**Request:**
```json
{
  "name": "John Doe Updated",
  "age": 26
}
```

#### DELETE /users/{id}
Soft delete user (sets active = false).

**Response:**
```json
{
  "success": true,
  "message": "User deleted successfully"
}
```

#### GET /users/search/name?name={name}
Search users by name.

#### GET /users/university/{university}
Get users by university.

#### GET /users/age-range?minAge={min}&maxAge={max}
Get users within age range.

#### GET /users/stats
Get user statistics.

**Response:**
```json
{
  "totalUsers": 150,
  "activeUsers": 142,
  "inactiveUsers": 8,
  "averageAge": 24.5,
  "universitiesRepresented": 12,
  "recentRegistrations": 5
}
```

### University Management Endpoints

#### GET /universities
Get all active universities.

#### POST /universities
Create a new university.

**Request:**
```json
{
  "name": "University of Colombo",
  "description": "Premier university in Sri Lanka",
  "location": "Colombo, Sri Lanka",
  "website": "https://cmb.ac.lk",
  "contactEmail": "info@cmb.ac.lk",
  "contactPhone": "+94112581835",
  "faculties": []
}
```

#### GET /universities/{id}
Get university by ID.

#### PUT /universities/{id}
Update university information.

#### DELETE /universities/{id}
Soft delete university.

#### GET /universities/location/{location}
Get universities by location.

#### GET /universities/search?name={name}
Search universities by name.

### Faculty Management Endpoints

#### GET /faculties
Get all active faculties.

#### POST /faculties
Create a new faculty.

**Request:**
```json
{
  "name": "Faculty of Science",
  "description": "Science and technology education",
  "universityId": "64f8a1b2c3d4e5f6a7b8c9d0",
  "dean": "Prof. John Smith",
  "contactEmail": "science@university.edu",
  "subjects": ["Mathematics", "Physics", "Chemistry", "Biology"]
}
```

#### GET /faculties/{id}
Get faculty by ID.

#### PUT /faculties/{id}
Update faculty information.

#### DELETE /faculties/{id}
Soft delete faculty.

#### GET /faculties/university/{universityId}
Get faculties by university.

#### GET /faculties/search?name={name}
Search faculties by name.

### Cart Management Endpoints

#### GET /carts/user/{userId}
Get user's carts.

#### POST /carts
Create a new cart.

**Request:**
```json
{
  "name": "My Shopping Cart",
  "userId": "64f8a1b2c3d4e5f6a7b8c9d0",
  "items": [
    {
      "productId": "product123",
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    }
  ]
}
```

#### PUT /carts/{id}
Update cart.

#### DELETE /carts/{id}
Delete cart.

#### POST /carts/{id}/items
Add item to cart.

#### DELETE /carts/{id}/items/{itemId}
Remove item from cart.

### Dynamic Query Endpoints

#### POST /dynamic-query/execute
Execute a dynamic query.

**Request:**
```json
{
  "queryName": "user.findByUniversity",
  "parameters": {
    "university": "University of Colombo"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Query executed successfully",
  "data": [...],
  "queryName": "user.findByUniversity",
  "executionTime": "2024-01-15T10:30:00",
  "resultCount": 25
}
```

#### GET /dynamic-query/mappings
Get available query mappings.

#### GET /dynamic-query/samples
Get sample queries with examples.

#### POST /dynamic-query/validate
Validate query parameters.

### Performance Monitoring Endpoints

#### GET /performance/metrics
Get comprehensive performance metrics (Admin only).

#### GET /performance/health
Get system health status.

#### DELETE /performance/cache/all
Clear all caches (Admin only).

#### DELETE /performance/cache/{cacheName}
Clear specific cache (Admin only).

### Health Check Endpoints

#### GET /health
Application health check.

**Response:**
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00",
  "version": "1.0.0",
  "environment": "development"
}
```

#### GET /dynamic-query/health
Dynamic query service health check.


## Database Schema

### Collections Overview

The application uses MongoDB with the following collections:

#### Users Collection
```javascript
{
  "_id": ObjectId,
  "name": String,
  "username": String (unique),
  "email": String (unique),
  "password": String (hashed),
  "age": Number,
  "university": String,
  "school": String,
  "work": String,
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

**Indexes:**
- `username` (unique)
- `email` (unique)
- `active`
- `university`
- `age`
- `createdAt`
- Compound: `{active: 1, university: 1}`

#### Universities Collection
```javascript
{
  "_id": ObjectId,
  "name": String (unique),
  "description": String,
  "location": String,
  "website": String,
  "contactEmail": String,
  "contactPhone": String,
  "faculties": [String],
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

**Indexes:**
- `name` (unique)
- `location`
- `active`
- `faculties`

#### Faculties Collection
```javascript
{
  "_id": ObjectId,
  "name": String,
  "description": String,
  "universityId": String,
  "dean": String,
  "contactEmail": String,
  "subjects": [String],
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

**Indexes:**
- `name`
- `universityId`
- `active`
- `subjects`
- Compound: `{universityId: 1, active: 1}`

#### Carts Collection
```javascript
{
  "_id": ObjectId,
  "name": String,
  "userId": String,
  "items": [
    {
      "productId": String,
      "productName": String,
      "quantity": Number,
      "price": Number
    }
  ],
  "totalAmount": Number,
  "status": String, // "ACTIVE", "COMPLETED", "CANCELLED"
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

**Indexes:**
- `userId`
- `status`
- `active`
- Compound: `{userId: 1, status: 1, active: 1}`

#### Queries Collection
```javascript
{
  "_id": ObjectId,
  "name": String (unique),
  "description": String,
  "category": String,
  "queryType": String, // "FIND", "AGGREGATE", "COUNT"
  "collection": String,
  "query": String,
  "parameters": [String],
  "cacheable": Boolean,
  "cacheTimeoutSeconds": Number,
  "createdBy": String,
  "active": Boolean,
  "createdAt": Date,
  "updatedAt": Date
}
```

**Indexes:**
- `name` (unique)
- `category`
- `queryType`
- `collection`
- `cacheable`
- `active`

## Security

### Authentication Flow

1. **User Registration/Login**
   - User provides credentials
   - Password is hashed using BCrypt
   - JWT token is generated upon successful authentication

2. **Token Validation**
   - Each protected request includes JWT token in Authorization header
   - JwtAuthenticationFilter validates token
   - User context is set in SecurityContext

3. **Authorization**
   - Role-based access control (RBAC)
   - Method-level security with @PreAuthorize
   - Admin-only endpoints for sensitive operations

### Security Features

- **Password Encryption**: BCrypt with salt rounds
- **JWT Tokens**: Stateless authentication with configurable expiration
- **CORS Configuration**: Cross-origin request handling
- **Input Validation**: Request validation with Bean Validation
- **SQL Injection Prevention**: MongoDB parameterized queries
- **XSS Protection**: Input sanitization and output encoding

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // JWT authentication filter
    // Password encoder configuration
    // CORS configuration
    // Security filter chain
}
```

### JWT Token Structure

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "username",
    "iat": 1642234567,
    "exp": 1642238167
  },
  "signature": "..."
}
```

## Performance Optimizations

### Database Optimizations

1. **Connection Pooling**
   - Maximum pool size: 20 connections
   - Minimum pool size: 5 connections
   - Connection timeout: 10 seconds
   - Idle timeout: 10 minutes

2. **Indexing Strategy**
   - Primary indexes on frequently queried fields
   - Compound indexes for multi-field queries
   - Unique indexes for data integrity
   - Text indexes for search functionality

3. **Query Optimization**
   - Efficient query patterns
   - Projection to limit returned fields
   - Pagination for large result sets
   - Aggregation pipelines for complex operations

### Caching Strategy

1. **Application-Level Caching**
   - Spring Cache abstraction
   - ConcurrentMapCacheManager
   - Method-level caching with @Cacheable
   - Cache eviction strategies

2. **Cache Configuration**
   ```java
   @Cacheable(value = "users", key = "#id")
   public User getUserById(String id) { ... }
   
   @CacheEvict(value = "users", key = "#id")
   public void deleteUser(String id) { ... }
   ```

3. **Cache Regions**
   - `users`: User data caching
   - `universities`: University information
   - `faculties`: Faculty data
   - `queries`: Dynamic query results
   - `statistics`: Computed statistics

### Memory Management

1. **JVM Tuning**
   - Heap size optimization
   - Garbage collection tuning
   - Memory leak prevention

2. **Object Lifecycle Management**
   - Proper resource cleanup
   - Connection management
   - Thread pool optimization

### Performance Monitoring

1. **Metrics Collection**
   - Response time monitoring
   - Memory usage tracking
   - Database connection metrics
   - Cache hit/miss ratios

2. **Health Checks**
   - Application health endpoints
   - Database connectivity checks
   - Memory usage alerts
   - Performance degradation detection

## Testing

### Testing Strategy

The application includes comprehensive testing at multiple levels:

1. **Unit Tests**
   - Service layer testing with Mockito
   - Repository layer testing
   - Utility class testing
   - 90%+ code coverage target

2. **Integration Tests**
   - Controller endpoint testing
   - Database integration testing
   - Security integration testing
   - End-to-end workflow testing

3. **API Testing**
   - Postman collection for manual testing
   - Automated API testing scripts
   - Performance testing scenarios

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage report
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest=**/*IntegrationTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

### Test Configuration

- **Test Database**: Separate test database configuration
- **Test Profiles**: Isolated test environment
- **Mock Objects**: Mockito for external dependencies
- **Test Data**: Predefined test datasets

### Coverage Reports

Test coverage reports are generated in:
```
target/site/jacoco/index.html
```

## Error Handling

### Global Exception Handling

The application uses a centralized error handling approach:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        // Standardized error response
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidation(ValidationException ex) {
        // Validation error response
    }
}
```

### Error Response Format

All error responses follow a consistent format:

```json
{
  "success": false,
  "error": "USER_NOT_FOUND",
  "message": "User with ID 12345 not found",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/users/12345",
  "details": {
    "errorCode": "USR_001",
    "field": "userId",
    "rejectedValue": "12345"
  }
}
```

### Exception Types

1. **Business Logic Exceptions**
   - `UserNotFoundException`
   - `UserAlreadyExistsException`
   - `UniversityNotFoundException`
   - `FacultyNotFoundException`
   - `CartNotFoundException`
   - `QueryNotFoundException`

2. **Security Exceptions**
   - `AuthenticationException`
   - `InvalidTokenException`
   - `AccessDeniedException`

3. **Validation Exceptions**
   - `MethodArgumentNotValidException`
   - `ConstraintViolationException`
   - `DataIntegrityViolationException`

4. **System Exceptions**
   - `DatabaseConnectionException`
   - `CacheException`
   - `ConfigurationException`

### Logging Strategy

```java
// Structured logging with correlation IDs
log.info("User created successfully", 
    kv("userId", user.getId()),
    kv("username", user.getUsername()),
    kv("correlationId", correlationId));

log.error("Database connection failed", 
    kv("error", ex.getMessage()),
    kv("retryAttempt", retryCount));
```

## Deployment

### Production Deployment

1. **Environment Setup**
   ```bash
   # Set production environment variables
   export SPRING_PROFILES_ACTIVE=prod
   export MONGODB_URI="mongodb+srv://prod-user:password@prod-cluster.mongodb.net/diyawanna_sup_main"
   export JWT_SECRET="production-secret-key-256-bits"
   export SERVER_PORT=8080
   ```

2. **Build Production JAR**
   ```bash
   mvn clean package -Pprod
   ```

3. **Run Application**
   ```bash
   java -jar target/diyawanna-sup-backend-1.0.0.jar
   ```

### Docker Deployment

1. **Dockerfile**
   ```dockerfile
   FROM openjdk:17-jre-slim
   COPY target/diyawanna-sup-backend-1.0.0.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```

2. **Docker Compose**
   ```yaml
   version: '3.8'
   services:
     app:
       build: .
       ports:
         - "8080:8080"
       environment:
         - SPRING_PROFILES_ACTIVE=prod
         - MONGODB_URI=${MONGODB_URI}
         - JWT_SECRET=${JWT_SECRET}
   ```

### Cloud Deployment

#### AWS Deployment
- **Elastic Beanstalk**: Easy application deployment
- **ECS**: Containerized deployment
- **Lambda**: Serverless deployment option

#### Heroku Deployment
```bash
# Create Heroku app
heroku create diyawanna-sup-backend

# Set environment variables
heroku config:set MONGODB_URI="mongodb+srv://..."
heroku config:set JWT_SECRET="your-secret"

# Deploy
git push heroku main
```

### Monitoring and Maintenance

1. **Application Monitoring**
   - Health check endpoints
   - Performance metrics
   - Error rate monitoring
   - Resource utilization

2. **Database Monitoring**
   - Connection pool metrics
   - Query performance
   - Index usage statistics
   - Storage utilization

3. **Security Monitoring**
   - Authentication failures
   - Suspicious activity detection
   - Token expiration tracking
   - Access pattern analysis

## Contributing

### Development Guidelines

1. **Code Style**
   - Follow Java coding conventions
   - Use meaningful variable and method names
   - Add comprehensive JavaDoc comments
   - Maintain consistent formatting

2. **Git Workflow**
   ```bash
   # Create feature branch
   git checkout -b feature/user-management-enhancement
   
   # Make changes and commit
   git add .
   git commit -m "feat: add user profile picture upload functionality"
   
   # Push and create pull request
   git push origin feature/user-management-enhancement
   ```

3. **Commit Message Format**
   ```
   type(scope): description
   
   feat(auth): add JWT token refresh functionality
   fix(user): resolve duplicate email validation issue
   docs(api): update authentication endpoint documentation
   test(service): add unit tests for UserService
   ```

4. **Pull Request Process**
   - Create descriptive pull request title
   - Include detailed description of changes
   - Add relevant tests
   - Ensure all tests pass
   - Request code review

### Testing Requirements

- All new features must include unit tests
- Integration tests for API endpoints
- Minimum 80% code coverage
- Performance tests for critical paths

### Documentation Requirements

- Update API documentation for new endpoints
- Add JavaDoc comments for public methods
- Update README for configuration changes
- Include example requests/responses

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:

- **Email**: support@diyawanna.sup
- **Documentation**: [API Documentation](docs/api.md)
- **Issues**: [GitHub Issues](https://github.com/diyawanna/sup-backend/issues)

## Changelog

### Version 1.0.0 (2024-01-15)
- Initial release
- JWT authentication system
- Complete CRUD operations for all entities
- Dynamic query processing
- Performance optimizations
- Comprehensive testing suite
- Production-ready deployment configuration

---

**Built with ❤️ by the Diyawanna Team**

