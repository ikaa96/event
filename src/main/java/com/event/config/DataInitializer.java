package com.event.config;

import com.event.model.User;
import com.event.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - izvršava se automatski kada se aplikacija pokrene
 * 
 * @Component - Spring prepoznaje ovu klasu kao komponentu
 * 
 * CommandLineRunner - interface koji ima run() metodu
 * Spring automatski poziva run() metodu nakon što se aplikacija pokrene
 * 
 * Ovo koristimo za:
 * - Testiranje da li Service i Repository rade
 * - Dodavanje početnih podataka u bazu
 * - Debug-ovanje
 */
@Component
@RequiredArgsConstructor
@Slf4j  // Lombok - automatski kreira logger (log.info(), log.error(), itd.)
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("=== Pokretanje DataInitializer-a ===");
        
        // Test 1: Proveri da li postoji korisnik sa username-om "test"
        boolean exists = userService.existsByUsername("test");
        log.info("Korisnik 'test' postoji: {}", exists);
        
        // Test 2: Kreiraj novog korisnika ako ne postoji
        if (!exists) {
            log.info("Kreiranje novog korisnika...");
            
            User newUser = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password("password123")  // Za sada plain text, kasnije ćemo hash-ovati
                    .role(User.Role.USER)
                    .build();
            
            User savedUser = userService.save(newUser);
            log.info("Korisnik sačuvan sa ID: {}", savedUser.getId());
        }
        
        // Test 3: Pronađi korisnika po username-u
        userService.findByUsername("test").ifPresentOrElse(
                user -> log.info("Pronađen korisnik: {} (ID: {})", user.getUsername(), user.getId()),
                () -> log.warn("Korisnik 'test' nije pronađen")
        );
        
        // Test 4: Vrati sve korisnike
        var allUsers = userService.findAll();
        log.info("Ukupno korisnika u bazi: {}", allUsers.size());
        
        log.info("=== DataInitializer završen ===");
    }
}
