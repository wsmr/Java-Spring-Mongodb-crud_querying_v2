# Project Structure Documentation

## Directory Structure

```
diyawanna-sup-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── diyawanna/
│   │   │           └── sup/
│   │   │               ├── config/
│   │   │               │   ├── CacheConfig.java
│   │   │               │   ├── MongoConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── CartController.java
│   │   │               │   ├── DynamicQueryController.java
│   │   │               │   ├── FacultyController.java
│   │   │               │   ├── HealthController.java
│   │   │               │   ├── PerformanceController.java
│   │   │               │   ├── QueryController.java
│   │   │               │   ├── UniversityController.java
│   │   │               │   └── UserController.java
│   │   │               ├── dto/
│   │   │               │   ├── LoginRequest.java
│   │   │               │   ├── LoginResponse.java
│   │   │               │   ├── QueryExecutionRequest.java
│   │   │               │   ├── QueryExecutionResponse.java
│   │   │               │   └── RegisterRequest.java
│   │   │               ├── entity/
│   │   │               │   ├── Cart.java
│   │   │               │   ├── Faculty.java
│   │   │               │   ├── Query.java
│   │   │               │   ├── University.java
│   │   │               │   └── User.java
│   │   │               ├── exception/
│   │   │               │   ├── AuthenticationException.java
│   │   │               │   ├── CartNotFoundException.java
│   │   │               │   ├── FacultyNotFoundException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── QueryAlreadyExistsException.java
│   │   │               │   ├── QueryNotFoundException.java
│   │   │               │   ├── UniversityAlreadyExistsException.java
│   │   │               │   ├── UniversityNotFoundException.java
│   │   │               │   ├── UserAlreadyExistsException.java
│   │   │               │   └── UserNotFoundException.java
│   │   │               ├── repository/
│   │   │               │   ├── CartRepository.java
│   │   │               │   ├── FacultyRepository.java
│   │   │               │   ├── QueryRepository.java
│   │   │               │   ├── UniversityRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               ├── security/
│   │   │               │   └── JwtAuthenticationFilter.java
│   │   │               ├── service/
│   │   │               │   ├── AuthenticationService.java
│   │   │               │   ├── CartService.java
│   │   │               │   ├── DynamicQueryService.java
│   │   │               │   ├── FacultyService.java
│   │   │               │   ├── PerformanceMonitoringService.java
│   │   │               │   ├── QueryService.java
│   │   │               │   ├── UniversityService.java
│   │   │               │   └── UserService.java
│   │   │               ├── util/
│   │   │               │   └── JwtUtil.java
│   │   │               └── DiyawannaSupBackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── query-config.json
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── diyawanna/
│       │           └── sup/
│       │               ├── controller/
│       │               │   └── AuthControllerIntegrationTest.java
│       │               └── service/
│       │                   └── UserServiceTest.java
│       └── resources/
│           └── application-test.properties
├── target/ (generated)
├── .gitignore
├── pom.xml
├── README.md
├── postman-collection.json
└── PROJECT_STRUCTURE.md
```

## Component Overview

### Configuration Layer (`config/`)
- **CacheConfig.java**: Cache management configuration
- **MongoConfig.java**: MongoDB connection and indexing setup
- **SecurityConfig.java**: Spring Security and JWT configuration

### Controller Layer (`controller/`)
- **AuthController.java**: Authentication endpoints (login, register, validate, refresh)
- **UserController.java**: User management CRUD operations
- **UniversityController.java**: University management operations
- **FacultyController.java**: Faculty management operations
- **CartController.java**: Shopping cart operations
- **QueryController.java**: Query management operations
- **DynamicQueryController.java**: Dynamic query execution
- **PerformanceController.java**: Performance monitoring endpoints
- **HealthController.java**: Health check endpoints

### Data Transfer Objects (`dto/`)
- **LoginRequest.java**: Login request payload
- **LoginResponse.java**: Login response with JWT token
- **RegisterRequest.java**: User registration payload
- **QueryExecutionRequest.java**: Dynamic query execution request
- **QueryExecutionResponse.java**: Dynamic query execution response

### Entity Layer (`entity/`)
- **User.java**: User entity with authentication fields
- **University.java**: University entity with faculties
- **Faculty.java**: Faculty entity with subjects
- **Cart.java**: Shopping cart entity with items
- **Query.java**: Dynamic query entity

### Exception Handling (`exception/`)
- **GlobalExceptionHandler.java**: Centralized exception handling
- **AuthenticationException.java**: Authentication-related exceptions
- **UserNotFoundException.java**: User not found exception
- **UserAlreadyExistsException.java**: Duplicate user exception
- **UniversityNotFoundException.java**: University not found exception
- **UniversityAlreadyExistsException.java**: Duplicate university exception
- **FacultyNotFoundException.java**: Faculty not found exception
- **CartNotFoundException.java**: Cart not found exception
- **QueryNotFoundException.java**: Query not found exception
- **QueryAlreadyExistsException.java**: Duplicate query exception

### Repository Layer (`repository/`)
- **UserRepository.java**: User data access with custom queries
- **UniversityRepository.java**: University data access
- **FacultyRepository.java**: Faculty data access
- **CartRepository.java**: Cart data access
- **QueryRepository.java**: Query data access

### Security Layer (`security/`)
- **JwtAuthenticationFilter.java**: JWT token validation filter

### Service Layer (`service/`)
- **AuthenticationService.java**: Authentication business logic
- **UserService.java**: User management business logic
- **UniversityService.java**: University management business logic
- **FacultyService.java**: Faculty management business logic
- **CartService.java**: Cart management business logic
- **QueryService.java**: Query management business logic
- **DynamicQueryService.java**: Dynamic query execution logic
- **PerformanceMonitoringService.java**: Performance monitoring logic

### Utility Layer (`util/`)
- **JwtUtil.java**: JWT token generation and validation utilities

### Test Layer (`test/`)
- **UserServiceTest.java**: Unit tests for UserService
- **AuthControllerIntegrationTest.java**: Integration tests for AuthController

### Configuration Files
- **application.properties**: Main application configuration
- **application-dev.properties**: Development environment configuration
- **application-test.properties**: Test environment configuration
- **query-config.json**: Dynamic query configuration

### Build and Documentation
- **pom.xml**: Maven build configuration
- **README.md**: Comprehensive project documentation
- **postman-collection.json**: API testing collection
- **.gitignore**: Git ignore rules

## Key Features by Layer

### Security Features
- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control
- CORS configuration
- Request validation

### Performance Features
- MongoDB connection pooling
- Multi-level caching
- Database indexing
- Query optimization
- Performance monitoring

### Business Features
- User management with soft delete
- University and faculty administration
- Shopping cart functionality
- Dynamic query processing
- Search and filtering capabilities

### Quality Assurance
- Comprehensive unit testing
- Integration testing
- Global exception handling
- Structured logging
- API documentation

## Development Workflow

1. **Entity Design**: Define MongoDB entities with proper validation
2. **Repository Layer**: Create data access interfaces with custom queries
3. **Service Layer**: Implement business logic with caching and validation
4. **Controller Layer**: Create REST endpoints with proper error handling
5. **Security Integration**: Add authentication and authorization
6. **Testing**: Write unit and integration tests
7. **Documentation**: Update API documentation and README

## Deployment Structure

### Development
- Local MongoDB or MongoDB Atlas
- Development profile configuration
- Debug logging enabled
- Hot reload for rapid development

### Testing
- Separate test database
- Mock external dependencies
- Test-specific configuration
- Coverage reporting

### Production
- MongoDB Atlas production cluster
- Production profile configuration
- Optimized logging levels
- Performance monitoring enabled
- Security hardening applied

