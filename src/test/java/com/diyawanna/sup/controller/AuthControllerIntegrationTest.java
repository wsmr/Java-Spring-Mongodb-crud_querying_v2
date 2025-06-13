package com.diyawanna.sup.controller;

import com.diyawanna.sup.dto.LoginRequest;
import com.diyawanna.sup.dto.RegisterRequest;
import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.repository.UserRepository;
import com.diyawanna.sup.util.JwtUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        testUser = new User();
        testUser.setId("user123");
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setAge(25);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("johndoe", "password123");
        
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("johndoe")).thenReturn("jwt-token");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("johndoe"));

        verify(userRepository).findByUsername("johndoe");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(jwtUtil).generateToken("johndoe");
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnError() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("johndoe", "wrongpassword");
        
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        verify(userRepository).findByUsername("johndoe");
        verify(passwordEncoder).matches("wrongpassword", "hashedPassword");
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void login_WithNonExistentUser_ShouldReturnError() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistent", "password123");
        
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void register_WithValidData_ShouldCreateUser() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Jane Doe");
        registerRequest.setUsername("janedoe");
        registerRequest.setEmail("jane@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setAge(24);
        registerRequest.setUniversity("University of Colombo");
        
        User newUser = new User();
        newUser.setId("user456");
        newUser.setName("Jane Doe");
        newUser.setUsername("janedoe");
        newUser.setEmail("jane@example.com");
        newUser.setPassword("hashedPassword");
        newUser.setAge(24);
        newUser.setUniversity("University of Colombo");
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        
        when(userRepository.existsByUsername("janedoe")).thenReturn(false);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.user.username").value("janedoe"))
                .andExpect(jsonPath("$.user.password").doesNotExist()); // Password should not be returned

        verify(userRepository).existsByUsername("janedoe");
        verify(userRepository).existsByEmail("jane@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithExistingUsername_ShouldReturnError() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Jane Doe");
        registerRequest.setUsername("johndoe"); // Existing username
        registerRequest.setEmail("jane@example.com");
        registerRequest.setPassword("password123");
        
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        verify(userRepository).existsByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WithInvalidData_ShouldReturnValidationError() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(""); // Invalid: empty name
        registerRequest.setUsername(""); // Invalid: empty username
        registerRequest.setEmail("invalid-email"); // Invalid: bad email format
        registerRequest.setPassword("123"); // Invalid: too short

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnValid() throws Exception {
        // Given
        String validToken = "valid-jwt-token";
        
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn("johndoe");

        // When & Then
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("johndoe"));

        verify(jwtUtil).validateToken(validToken);
        verify(jwtUtil).extractUsername(validToken);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnInvalid() throws Exception {
        // Given
        String invalidToken = "invalid-jwt-token";
        
        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));

        verify(jwtUtil).validateToken(invalidToken);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void validateToken_WithoutToken_ShouldReturnError() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/validate"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        verify(jwtUtil, never()).validateToken(anyString());
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewToken() throws Exception {
        // Given
        String oldToken = "old-jwt-token";
        String newToken = "new-jwt-token";
        
        when(jwtUtil.validateToken(oldToken)).thenReturn(true);
        when(jwtUtil.extractUsername(oldToken)).thenReturn("johndoe");
        when(jwtUtil.generateToken("johndoe")).thenReturn(newToken);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value(newToken))
                .andExpect(jsonPath("$.username").value("johndoe"));

        verify(jwtUtil).validateToken(oldToken);
        verify(jwtUtil).extractUsername(oldToken);
        verify(jwtUtil).generateToken("johndoe");
    }
}

