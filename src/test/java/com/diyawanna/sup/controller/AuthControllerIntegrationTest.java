package com.diyawanna.sup.controller;

import com.diyawanna.sup.dto.LoginRequest;
import com.diyawanna.sup.dto.RegisterRequest;
import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.repository.UserRepository;
import com.diyawanna.sup.util.JwtUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController
 *
 * @author Diyawanna Team
 * @version 1.0.0
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private UserDetails testUserDetails;

    // Constants for better maintainability
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String VALIDATE_ENDPOINT = "/api/auth/validate";
    private static final String REFRESH_ENDPOINT = "/api/auth/refresh";

    private static final String TEST_USERNAME = "johndoe";
    private static final String TEST_EMAIL = "john@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String HASHED_PASSWORD = "hashedPassword";
    private static final String JWT_TOKEN = "jwt-token";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        testUser = createTestUser();
        testUserDetails = createTestUserDetails();
    }

    private User createTestUser() {
        User user = new User();
        user.setId("user123");
        user.setName("John Doe");
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(HASHED_PASSWORD);
        user.setAge(25);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserDetails createTestUserDetails() {
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(TEST_USERNAME);
        return builder
                .password(HASHED_PASSWORD)
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    @DisplayName("Should return token when credentials are valid")
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, TEST_PASSWORD);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(JWT_TOKEN);

        // When & Then
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value(JWT_TOKEN))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME));

        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(passwordEncoder).matches(TEST_PASSWORD, HASHED_PASSWORD);
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should return error when password is incorrect")
    void login_WithInvalidCredentials_ShouldReturnError() throws Exception {
        // Given
        String wrongPassword = "wrongpassword";
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, wrongPassword);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(wrongPassword, HASHED_PASSWORD)).thenReturn(false);

        // When & Then
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(passwordEncoder).matches(wrongPassword, HASHED_PASSWORD);
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should return error when user does not exist")
    void login_WithNonExistentUser_ShouldReturnError() throws Exception {
        // Given
        String nonExistentUser = "nonexistent";
        LoginRequest loginRequest = new LoginRequest(nonExistentUser, TEST_PASSWORD);

        when(userRepository.findByUsername(nonExistentUser)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        verify(userRepository).findByUsername(nonExistentUser);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Should create user when registration data is valid")
    void register_WithValidData_ShouldCreateUser() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Jane Doe");
        registerRequest.setUsername("janedoe");
        registerRequest.setEmail("jane@example.com");
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setAge(24);
        registerRequest.setUniversity("University of Colombo");

        User newUser = new User();
        newUser.setId("user456");
        newUser.setName("Jane Doe");
        newUser.setUsername("janedoe");
        newUser.setEmail("jane@example.com");
        newUser.setPassword(HASHED_PASSWORD);
        newUser.setAge(24);
        newUser.setUniversity("University of Colombo");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.existsByUsername("janedoe")).thenReturn(false);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When & Then
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.user.username").value("janedoe"))
                .andExpect(jsonPath("$.user.password").doesNotExist()); // Password should not be returned

        verify(userRepository).existsByUsername("janedoe");
        verify(userRepository).existsByEmail("jane@example.com");
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should return error when username already exists")
    void register_WithExistingUsername_ShouldReturnError() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Jane Doe");
        registerRequest.setUsername(TEST_USERNAME); // Existing username
        registerRequest.setEmail("jane@example.com");
        registerRequest.setPassword(TEST_PASSWORD);

        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        // When & Then
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        verify(userRepository).existsByUsername(TEST_USERNAME);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return validation error when data is invalid")
    void register_WithInvalidData_ShouldReturnValidationError() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(""); // Invalid: empty name
        registerRequest.setUsername(""); // Invalid: empty username
        registerRequest.setEmail("invalid-email"); // Invalid: bad email format
        registerRequest.setPassword("123"); // Invalid: too short

        // When & Then
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return valid when token is valid")
    void validateToken_WithValidToken_ShouldReturnValid() throws Exception {
        // Given
        String validToken = "valid-jwt-token";

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn(TEST_USERNAME);

        // When & Then
        mockMvc.perform(post(VALIDATE_ENDPOINT)
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME));

        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).extractUsername(validToken);
    }

    @Test
    @DisplayName("Should return invalid when token is invalid")
    void validateToken_WithInvalidToken_ShouldReturnInvalid() throws Exception {
        // Given
        String invalidToken = "invalid-jwt-token";

        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        mockMvc.perform(post(VALIDATE_ENDPOINT)
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));

        verify(jwtUtil).validateToken(invalidToken);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("Should return error when no token is provided")
    void validateToken_WithoutToken_ShouldReturnError() throws Exception {
        // When & Then
        mockMvc.perform(post(VALIDATE_ENDPOINT))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("Should return new token when refresh token is valid")
    void refreshToken_WithValidToken_ShouldReturnNewToken() throws Exception {
        // Given
        String oldToken = "old-jwt-token";
        String newToken = "new-jwt-token";

        when(jwtUtil.validateToken(oldToken)).thenReturn(true);
        when(jwtUtil.extractUsername(oldToken)).thenReturn(TEST_USERNAME);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(newToken);

        // When & Then
        mockMvc.perform(post(REFRESH_ENDPOINT)
                .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value(newToken))
                .andExpect(jsonPath("$.username").value(TEST_USERNAME));

        verify(jwtUtil).validateToken(oldToken);
        verify(jwtUtil).extractUsername(oldToken);
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle malformed JSON in login request")
        void login_WithMalformedJson_ShouldReturnBadRequest() throws Exception {
            String malformedJson = "{\"username\":\"test\",\"password\":}";

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(malformedJson))
                    .andExpect(status().isBadRequest());

            verify(userRepository, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("Should handle empty request body")
        void login_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""))
                    .andExpect(status().isBadRequest());

            verify(userRepository, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("Should handle null username in login request")
        void login_WithNullUsername_ShouldReturnBadRequest() throws Exception {
            LoginRequest loginRequest = new LoginRequest(null, TEST_PASSWORD);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());

            verify(userRepository, never()).findByUsername(anyString());
        }

        @Test
        @DisplayName("Should handle null password in login request")
        void login_WithNullPassword_ShouldReturnBadRequest() throws Exception {
            LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, null);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());

            verify(userRepository, never()).findByUsername(anyString());
        }
    }

    @Nested
    @DisplayName("Security Tests")
    class SecurityTests {

        @Test
        @DisplayName("Should not expose sensitive information in error responses")
        void login_WithInvalidCredentials_ShouldNotExposeSensitiveInfo() throws Exception {
            LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, "wrongpassword");

            when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongpassword", HASHED_PASSWORD)).thenReturn(false);

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error").exists())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.hashedPassword").doesNotExist());
        }

        @Test
        @DisplayName("Should handle SQL injection attempts gracefully")
        void login_WithSqlInjectionAttempt_ShouldReturnError() throws Exception {
            String maliciousUsername = "admin'; DROP TABLE users; --";
            LoginRequest loginRequest = new LoginRequest(maliciousUsername, TEST_PASSWORD);

            when(userRepository.findByUsername(maliciousUsername)).thenReturn(Optional.empty());

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());

            verify(userRepository).findByUsername(maliciousUsername);
        }
    }
}

