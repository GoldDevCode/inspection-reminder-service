package com.tribia.application.servicetests;

import com.tribia.application.entity.User;
import com.tribia.application.repository.UserRepository;
import com.tribia.application.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final String existingEmail = "existing@example.com";
    private final String newEmail = "new@example.com";
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User(existingEmail);
    }

    @Test
    void findOrCreateUser_withExistingUser_shouldReturnExistingUser() {
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));

        User result = userService.findOrCreateUser(existingEmail);

        assertEquals(existingUser, result);
        verify(userRepository).findByEmail(existingEmail);
    }

    @Test
    void findOrCreateUser_withNewUser_shouldReturnNewUser() {
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());

        User result = userService.findOrCreateUser(newEmail);

        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        verify(userRepository).findByEmail(newEmail);
    }

    @Test
    void findUserByEmail_withExistingUser_shouldReturnOptionalUser() {
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));

        Optional<User> result = userService.findUserByEmail(existingEmail);

        assertTrue(result.isPresent());
        assertEquals(existingUser, result.get());
        verify(userRepository).findByEmail(existingEmail);
    }

    @Test
    void findUserByEmail_withNonExistingUser_shouldReturnEmptyOptional() {
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserByEmail(newEmail);

        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail(newEmail);
    }

    @Test
    void saveUser_shouldSaveAndReturnUser() {
        User userToSave = new User(newEmail);
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User result = userService.saveUser(userToSave);

        assertEquals(userToSave, result);
        verify(userRepository).save(userToSave);
    }

    @Test
    void findOrCreateUser_withNullEmail_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.findOrCreateUser(null));
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void findUserByEmail_withNullEmail_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.findUserByEmail(null));
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void saveUser_withNullUser_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null));
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrCreateUser_withEmptyEmail_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.findOrCreateUser(""));
        verify(userRepository, never()).findByEmail(any());
    }

}

