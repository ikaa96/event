package com.event.service;

import com.event.exception.ResourceAlreadyExistsException;
import com.event.model.User;
import com.event.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * UserService - business logika za rad sa korisnicima
 * 
 * @Service - označava da je ovo Spring Service komponenta
 * Spring automatski kreira instancu ove klase i injektuje dependency-je
 * 
 * @RequiredArgsConstructor - Lombok automatski generiše konstruktor sa svim final poljima
 * Ovo znači da ne moraš ručno da pišeš konstruktor za UserRepository
 * 
 * @Transactional - sve metode u ovoj klasi su transakcione
 * Ako dođe do greške, sve promene se rollback-uju (ne sačuvaju se)
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    /**
     * final - ovo polje se ne može promeniti nakon inicijalizacije
     * Spring automatski injektuje UserRepository kroz konstruktor
     * Ovo se zove Dependency Injection (DI)
     */
    private final UserRepository userRepository;
    
    /**
     * Pronalazi korisnika po ID-u
     * 
     * @Transactional(readOnly = true) - samo čita podatke, ne menja ih
     * readOnly = true je optimizacija - brže je
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Pronalazi korisnika po username-u
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Pronalazi korisnika po email-u
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Vraća sve korisnike
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Proverava da li korisnik sa username-om postoji
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Proverava da li korisnik sa email-om postoji
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Čuva korisnika u bazu
     * 
     * @Transactional - ako dođe do greške, promene se ne sačuvaju
     * Spring automatski upravlja transakcijama
     */
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Briše korisnika iz baze
     */
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }
    
    /**
     * Briše korisnika po ID-u
     */
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Kreira novog korisnika sa proverom da li već postoji
     * 
     * @param user - korisnik za kreiranje
     * @return sačuvan korisnik
     * @throws ResourceAlreadyExistsException ako korisnik sa istim username-om ili email-om već postoji
     */
    @Transactional
    public User createUser(User user) {
        // Proverava da li korisnik sa istim username-om već postoji
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResourceAlreadyExistsException("Korisnik sa username-om '" + user.getUsername() + "' već postoji");
        }
        
        // Proverava da li korisnik sa istim email-om već postoji
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResourceAlreadyExistsException("Korisnik sa email-om '" + user.getEmail() + "' već postoji");
        }
        
        return userRepository.save(user);
    }
}
