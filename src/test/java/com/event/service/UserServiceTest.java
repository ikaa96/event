package com.event.service;

import com.event.model.User;
import com.event.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceTest - unit testovi za UserService
 * 
 * @ExtendWith(MockitoExtension.class) - omogućava korišćenje Mockito framework-a
 * Mockito se koristi za kreiranje "lažnih" objekata (mock-ova)
 * 
 * @Mock - kreira mock objekat (lažni UserRepository)
 * Ne poziva pravi UserRepository, već simulira njegovo ponašanje
 * 
 * @InjectMocks - automatski injektuje mock-ove u UserService
 * Spring ne radi ovde - sve je ručno kontrolisano u testu
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {
    
    /**
     * @Mock - ovo je lažni UserRepository
     * Ne pristupa pravoj bazi podataka - sve je simulirano
     */
    @Mock
    private UserRepository userRepository;
    
    /**
     * @InjectMocks - ovo je pravi UserService, ali sa mock-ovanim Repository-jem
     * Kada UserService pozove userRepository.save(), to je mock poziv
     */
    @InjectMocks
    private UserService userService;
    
    /**
     * Test podaci - koristimo ih u svim testovima
     */
    private User testUser;
    
    /**
     * @BeforeEach - ova metoda se izvršava PRE svakog testa
     * Koristimo je da pripremimo test podatke
     */
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role(User.Role.USER)
                .build();
    }
    
    /**
     * Test: Pronalaženje korisnika po ID-u kada korisnik POSTOJI
     * 
     * @Test - označava da je ovo test metoda
     * @DisplayName - lepši naziv testa u izveštaju
     */
    @Test
    @DisplayName("Treba da pronađe korisnika kada postoji")
    void findById_WhenUserExists_ShouldReturnUser() {
        // Arrange (Priprema) - šta očekujemo da se desi
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // Act (Akcija) - pozivamo metodu koju testiramo
        Optional<User> result = userService.findById(userId);
        
        // Assert (Provera) - proveravamo da li je rezultat ono što očekujemo
        assertTrue(result.isPresent(), "Korisnik treba da postoji");
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test@example.com", result.get().getEmail());
        
        // Verifikacija - proveravamo da li je metoda pozvana tačno jednom
        verify(userRepository, times(1)).findById(userId);
    }
    
    /**
     * Test: Pronalaženje korisnika po ID-u kada korisnik NE POSTOJI
     */
    @Test
    @DisplayName("Treba da vrati prazan Optional kada korisnik ne postoji")
    void findById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = userService.findById(userId);
        
        // Assert
        assertTrue(result.isEmpty(), "Korisnik ne treba da postoji");
        verify(userRepository, times(1)).findById(userId);
    }
    
    /**
     * Test: Pronalaženje korisnika po username-u
     */
    @Test
    @DisplayName("Treba da pronađe korisnika po username-u")
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        
        // Act
        Optional<User> result = userService.findByUsername(username);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }
    
    /**
     * Test: Provera da li korisnik postoji po username-u
     */
    @Test
    @DisplayName("Treba da vrati true kada korisnik postoji")
    void existsByUsername_WhenUserExists_ShouldReturnTrue() {
        // Arrange
        String username = "testuser";
        when(userRepository.existsByUsername(username)).thenReturn(true);
        
        // Act
        boolean result = userService.existsByUsername(username);
        
        // Assert
        assertTrue(result, "Korisnik treba da postoji");
        verify(userRepository, times(1)).existsByUsername(username);
    }
    
    @Test
    @DisplayName("Treba da vrati false kada korisnik ne postoji")
    void existsByUsername_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        
        // Act
        boolean result = userService.existsByUsername(username);
        
        // Assert
        assertFalse(result, "Korisnik ne treba da postoji");
        verify(userRepository, times(1)).existsByUsername(username);
    }
    
    /**
     * Test: Vraćanje svih korisnika
     */
    @Test
    @DisplayName("Treba da vrati sve korisnike")
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        User user1 = User.builder().id(1L).username("user1").email("user1@test.com").password("pass").role(User.Role.USER).build();
        User user2 = User.builder().id(2L).username("user2").email("user2@test.com").password("pass").role(User.Role.USER).build();
        List<User> users = Arrays.asList(user1, user2);
        
        when(userRepository.findAll()).thenReturn(users);
        
        // Act
        List<User> result = userService.findAll();
        
        // Assert
        assertEquals(2, result.size(), "Treba da postoje 2 korisnika");
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }
    
    /**
     * Test: Čuvanje korisnika
     */
    @Test
    @DisplayName("Treba da sačuva korisnika")
    void save_ShouldSaveUser() {
        // Arrange
        User newUser = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .role(User.Role.USER)
                .build();
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User result = userService.save(newUser);
        
        // Assert
        assertNotNull(result, "Rezultat ne treba da bude null");
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(newUser);
    }
    
    /**
     * Test: Brisanje korisnika
     */
    @Test
    @DisplayName("Treba da obriše korisnika")
    void delete_ShouldDeleteUser() {
        // Arrange - ne treba when() jer delete() ne vraća ništa
        
        // Act
        userService.delete(testUser);
        
        // Assert
        verify(userRepository, times(1)).delete(testUser);
    }
    
    @Test
    @DisplayName("Treba da obriše korisnika po ID-u")
    void deleteById_ShouldDeleteUserById() {
        // Arrange
        Long userId = 1L;
        
        // Act
        userService.deleteById(userId);
        
        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }
}
