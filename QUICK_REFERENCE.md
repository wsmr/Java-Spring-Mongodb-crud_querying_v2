# Quick Reference Guide - Enhanced Features

## 🚀 New Features Overview

### 1. Authentication Rate Limiting
- **Purpose**: Prevent brute force attacks
- **Configuration**: `application.properties`
- **Admin Control**: `/api/admin/auth/*` endpoints

### 2. Environment-Based Error Responses
- **Development**: Full stack traces included
- **Production**: Clean, sanitized error messages
- **Control**: Spring profiles (`dev` vs `prod`)

### 3. Fixed User Management
- **Issue**: User deletion and retrieval inconsistencies
- **Solution**: Comprehensive cache management
- **New**: Pagination support for better performance

### 4. Enhanced Health Monitoring
- **Basic**: `/api/health` (for load balancers)
- **Detailed**: `/api/health/detailed` (comprehensive metrics)
- **Metrics**: RAM, CPU, Disk, Database, JVM stats

### 5. Admin Management Interface
- **Configuration**: View system settings
- **Rate Limiting**: Manage authentication attempts
- **Monitoring**: Real-time system status

## 🔧 Quick Configuration

### Enable/Disable Rate Limiting
```properties
# In application.properties
auth.rate-limiting.enabled=true
auth.rate-limiting.max-attempts=5
auth.rate-limiting.lockout-duration-minutes=15
```

### Switch Environment Mode
```bash
# Development (with stack traces)
export SPRING_PROFILES_ACTIVE=dev

# Production (clean errors)
export SPRING_PROFILES_ACTIVE=prod
```

## 📊 Key Endpoints

### Health Monitoring
```bash
GET /api/health              # Basic health
GET /api/health/detailed     # Full system metrics
GET /api/status              # Lightweight status
GET /api/info                # Application info
GET /api/ping                # Minimal check
```

### Admin Management
```bash
GET  /api/admin/auth/config           # View rate limiting config
POST /api/admin/auth/clear-all        # Clear all attempts
POST /api/admin/auth/clear/{user}     # Clear user attempts
GET  /api/admin/auth/attempts/{user}  # Check user attempts
GET  /api/admin/config                # System configuration
```

### Authentication (Enhanced)
```bash
POST /api/auth/login         # Login with rate limiting
POST /api/auth/register      # Register new user
POST /api/auth/refresh       # Refresh JWT token
GET  /api/auth/validate      # Validate token
```

## 🛠️ Troubleshooting

### Rate Limiting Issues
1. Check configuration: `GET /api/admin/auth/config`
2. Clear attempts: `POST /api/admin/auth/clear-all`
3. Verify settings in `application.properties`

### User Management Issues
1. Check cache status via health endpoint
2. Verify database connectivity
3. Use pagination for large datasets

### Health Check Issues
1. Verify application startup
2. Check MongoDB connectivity
3. Monitor system resources

## 📈 Monitoring Commands

### Check System Health
```bash
# Basic health check
curl http://localhost:8080/api/health

# Detailed system metrics
curl http://localhost:8080/api/health/detailed

# Check rate limiting status
curl http://localhost:8080/api/admin/auth/config
```

### Monitor Authentication
```bash
# Check specific user attempts
curl http://localhost:8080/api/admin/auth/attempts/username

# Clear all authentication attempts
curl -X POST http://localhost:8080/api/admin/auth/clear-all
```

## 🔒 Security Features

### Rate Limiting Protection
- ✅ Configurable attempt limits
- ✅ Automatic lockout periods
- ✅ Per-user tracking
- ✅ Admin override capabilities

### Error Information Security
- ✅ Environment-based disclosure
- ✅ No sensitive data leakage
- ✅ Configurable verbosity
- ✅ Production-safe defaults

## 🎯 Performance Optimizations

### Cache Management
- ✅ Fixed user deletion cache issues
- ✅ Comprehensive cache eviction
- ✅ Consistent data across endpoints
- ✅ Automatic cleanup processes

### System Monitoring
- ✅ Real-time resource tracking
- ✅ Database health monitoring
- ✅ JVM performance metrics
- ✅ Memory leak detection

## 📋 Configuration Checklist

### Production Deployment
- [ ] Set `SPRING_PROFILES_ACTIVE=prod`
- [ ] Configure `auth.rate-limiting.enabled=true`
- [ ] Set appropriate `auth.rate-limiting.max-attempts`
- [ ] Configure MongoDB connection string
- [ ] Set JWT secret and expiration
- [ ] Verify health endpoints accessibility
- [ ] Test rate limiting functionality
- [ ] Confirm error responses are sanitized

### Development Setup
- [ ] Set `SPRING_PROFILES_ACTIVE=dev`
- [ ] Enable debug logging
- [ ] Configure development database
- [ ] Set lenient rate limiting (optional)
- [ ] Verify stack traces in error responses
- [ ] Test all admin endpoints

## 🚨 Emergency Procedures

### Clear All Authentication Attempts
```bash
curl -X POST http://localhost:8080/api/admin/auth/clear-all
```

### Check System Status
```bash
curl http://localhost:8080/api/health/detailed
```

### Restart Application
```bash
# Stop current instance
pkill -f "diyawanna-sup-backend"

# Start new instance
java -jar target/diyawanna-sup-backend-1.0.0.jar
```

---

**Quick Reference Version**: 1.0.0
**Last Updated**: June 24, 2025

