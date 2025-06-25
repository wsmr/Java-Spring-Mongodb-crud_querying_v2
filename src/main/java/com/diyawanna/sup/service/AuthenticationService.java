package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.repository.UserRepository;
import com.diyawanna.sup.util.JwtUtil;
import com.diyawanna.sup.dto.LoginRequest;
import com.diyawanna.sup.dto.LoginResponse;
import com.diyawanna.sup.dto.RegisterRequest;
import com.diyawanna.sup.exception.AuthenticationException;
import com.diyawanna.sup.exception.UserAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication service for user login, registration, and JWT token management
 * 
 * This service provides:
 * - User authentication with username/password
 * - JWT token generation and validation
 * - User registration
 * - UserDetailsService implementation for Spring Security
 * - Authentication attempt rate limiting
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationAttemptService attemptService;

    /**
     * Authenticate user and generate JWT token
     */
    public LoginResponse authenticate(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        
        try {
            // Check if user is currently blocked due to too many failed attempts
            if (attemptService.isBlocked(username)) {
                long retryAfter = attemptService.getRemainingLockoutTime(username);
                throw new AuthenticationException("Too many authentication attempts, please try again later", retryAfter);
            }

            // Find user by username
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                attemptService.recordFailedAttempt(username);
                throw new AuthenticationException("Invalid username or password");
            }

            User user = userOptional.get();

            // Check if user is active
            if (!user.isActive()) {
                attemptService.recordFailedAttempt(username);
                throw new AuthenticationException("User account is deactivated");
            }

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                attemptService.recordFailedAttempt(username);
                throw new AuthenticationException("Invalid username or password");
            }

            // Authentication successful - clear any failed attempts
            attemptService.recordSuccessfulAttempt(username);

            // Create UserDetails for JWT generation
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(new ArrayList<>())
                    .build();

            // Generate JWT token with additional claims
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getId());
            extraClaims.put("name", user.getName());
            extraClaims.put("email", user.getEmail());

            String token = jwtUtil.generateToken(user.getUsername(), extraClaims);

            // Create response
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenType("Bearer");
            response.setExpiresIn(jwtUtil.getExpirationTime());
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setName(user.getName());
            response.setEmail(user.getEmail());

            return response;

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            attemptService.recordFailedAttempt(username);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Register new user
     */
    public User register(RegisterRequest registerRequest) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                throw new UserAlreadyExistsException("Username already exists");
            }

            // Check if email already exists
            if (registerRequest.getEmail() != null &&
                userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new UserAlreadyExistsException("Email already exists");
            }

            // Create new user
            User user = new User();
            user.setName(registerRequest.getName());
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEmail(registerRequest.getEmail());
            user.setAge(registerRequest.getAge());
            user.setUniversity(registerRequest.getUniversity());
            user.setSchool(registerRequest.getSchool());
            user.setWork(registerRequest.getWork());
            user.setPhoneNumber(registerRequest.getPhoneNumber());
            user.setAddress(registerRequest.getAddress());
            user.setActive(true);

            return userRepository.save(user);

        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Validate JWT token and return user details
     */
    public User validateTokenAndGetUser(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new AuthenticationException("Invalid or expired token");
            }

            String username = jwtUtil.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new AuthenticationException("User not found");
            }

            User user = userOptional.get();
            if (!user.isActive()) {
                throw new AuthenticationException("User account is deactivated");
            }

            return user;

        } catch (Exception e) {
            throw new AuthenticationException("Token validation failed: " + e.getMessage());
        }
    }

    /**
     * Refresh JWT token
     */
    public LoginResponse refreshToken(String token) {
        try {
            if (!jwtUtil.canTokenBeRefreshed(token)) {
                throw new AuthenticationException("Token cannot be refreshed");
            }

            String username = jwtUtil.extractUsername(token);
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new AuthenticationException("User not found");
            }

            User user = userOptional.get();
            if (!user.isActive()) {
                throw new AuthenticationException("User account is deactivated");
            }

            // Generate new token
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", user.getId());
            extraClaims.put("name", user.getName());
            extraClaims.put("email", user.getEmail());

            String newToken = jwtUtil.generateToken(user.getUsername(), extraClaims);

            // Create response
            LoginResponse response = new LoginResponse();
            response.setToken(newToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(jwtUtil.getExpirationTime());
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setName(user.getName());
            response.setEmail(user.getEmail());

            return response;

        } catch (Exception e) {
            throw new AuthenticationException("Token refresh failed: " + e.getMessage());
        }
    }

    /**
     * Implementation of UserDetailsService for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = userOptional.get();

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }

    /**
     * Change user password
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new AuthenticationException("User not found");
            }

            User user = userOptional.get();

            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new AuthenticationException("Invalid current password");
            }

            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

        } catch (Exception e) {
            throw new AuthenticationException("Password change failed: " + e.getMessage());
        }
    }

    /**
     * Deactivate user account
     */
    public void deactivateUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(false);
            userRepository.save(user);
        }
    }

    /**
     * Activate user account
     */
    public void activateUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(true);
            userRepository.save(user);
        }
    }
}

