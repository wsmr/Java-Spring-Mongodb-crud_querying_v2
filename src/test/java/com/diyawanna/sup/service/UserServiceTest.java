package com.diyawanna.sup.service;

import com.diyawanna.sup.entity.User;
import com.diyawanna.sup.repository.UserRepository;
import com.diyawanna.sup.exception.UserNotFoundException;
import com.diyawanna.sup.exception.UserAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * 
 * @author Diyawanna Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user123");
        testUser.setName("John Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setAge(25);
        testUser.setUniversity("University of Colombo");
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllActiveUsers_ShouldReturnActiveUsers() {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findByActiveTrue()).thenReturn(activeUsers);

        // When
        List<User> result = userService.getAllActiveUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository).findByActiveTrue();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserById("user123");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).findById("user123");
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findById("invalid")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("invalid"));
        verify(userRepository).findById("invalid");
    }

    @Test
    void getUserByUsername_WithValidUsername_ShouldReturnUser() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserByUsername("johndoe");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).findByUsername("johndoe");
    }

    @Test
    void getUserByUsername_WithInvalidUsername_ShouldThrowException() {
        // Given
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("invalid"));
        verify(userRepository).findByUsername("invalid");
    }

    @Test
    void createUser_WithValidUser_ShouldCreateUser() {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setUsername("janedoe");
        newUser.setEmail("jane@example.com");
        newUser.setPassword("plainPassword");
        
        when(userRepository.existsByUsername("janedoe")).thenReturn(false);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertTrue(result.isActive());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(userRepository).existsByUsername("janedoe");
        verify(userRepository).existsByEmail("jane@example.com");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Given
        User newUser = new User();
        newUser.setUsername("johndoe");
        newUser.setEmail("new@example.com");
        
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(newUser));
        verify(userRepository).existsByUsername("johndoe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("john@example.com");
        
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(newUser));
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        // Given
        User updateData = new User();
        updateData.setName("John Updated");
        updateData.setAge(26);
        
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser("user123", updateData);

        // Then
        assertNotNull(result);
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldSoftDeleteUser() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser("user123");

        // Then
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void searchUsersByName_ShouldReturnMatchingUsers() {
        // Given
        List<User> matchingUsers = Arrays.asList(testUser);
        when(userRepository.findByNameContainingIgnoreCase("John")).thenReturn(matchingUsers);

        // When
        List<User> result = userService.searchUsersByName("John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByNameContainingIgnoreCase("John");
    }

    @Test
    void getUsersByUniversity_ShouldReturnUsersFromUniversity() {
        // Given
        List<User> universityUsers = Arrays.asList(testUser);
        when(userRepository.findByUniversityAndActiveTrue("University of Colombo")).thenReturn(universityUsers);

        // When
        List<User> result = userService.getUsersByUniversity("University of Colombo");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByUniversityAndActiveTrue("University of Colombo");
    }

    @Test
    void getUsersByAgeRange_ShouldReturnUsersInRange() {
        // Given
        List<User> ageRangeUsers = Arrays.asList(testUser);
        when(userRepository.findByAgeBetweenAndActiveTrue(20, 30)).thenReturn(ageRangeUsers);

        // When
        List<User> result = userService.getUsersByAgeRange(20, 30);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findByAgeBetweenAndActiveTrue(20, 30);
    }

    @Test
    void countActiveUsers_ShouldReturnCount() {
        // Given
        when(userRepository.countByActiveTrue()).thenReturn(5L);

        // When
        long result = userService.countActiveUsers();

        // Then
        assertEquals(5L, result);
        verify(userRepository).countByActiveTrue();
    }

    @Test
    void usernameExists_WithExistingUsername_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // When
        boolean result = userService.usernameExists("johndoe");

        // Then
        assertTrue(result);
        verify(userRepository).existsByUsername("johndoe");
    }

    @Test
    void usernameExists_WithNonExistingUsername_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // When
        boolean result = userService.usernameExists("nonexistent");

        // Then
        assertFalse(result);
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    void emailExists_WithExistingEmail_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When
        boolean result = userService.emailExists("john@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("john@example.com");
    }

    @Test
    void activateUser_ShouldActivateUser() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.activateUser("user123");

        // Then
        assertNotNull(result);
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deactivateUser_ShouldDeactivateUser() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.deactivateUser("user123");

        // Then
        assertNotNull(result);
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }
}

