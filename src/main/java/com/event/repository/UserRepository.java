package com.event.repository;

import com.event.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - interface za rad sa User entitetom u bazi
 * 
 * @Repository - označava da je ovo Spring komponenta za pristup podacima
 * 
 * JpaRepository<User, Long> znači:
 * - User - tip entiteta sa kojim radimo
 * - Long - tip ID-a (primarnog ključa)
 * 
 * Spring Data JPA automatski kreira implementaciju ovog interface-a!
 * Ne moraš da pišeš SQL upite - Spring to radi za tebe.
 * 
 * Automatski dobijaš metode:
 * - save(User user) - čuva ili ažurira korisnika
 * - findById(Long id) - pronalazi korisnika po ID-u
 * - findAll() - vraća sve korisnike
 * - delete(User user) - briše korisnika
 * - count() - broji korisnike
 * - itd.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Spring Data JPA automatski kreira implementaciju ove metode!
     * 
     * Pravilo: findBy + ime polja (sa velikim slovom)
     * Spring automatski generiše SQL: SELECT * FROM users WHERE username = ?
     * 
     * Optional<User> - vraća Optional jer korisnik možda ne postoji
     * Optional je bolji od null jer forsira eksplicitnu proveru
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Slično kao findByUsername, samo za email
     * Spring generiše: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);
    
    /**
     * existsBy... - vraća boolean (true ako postoji, false ako ne)
     * Spring generiše: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    /**
     * Možeš kombinovati više polja:
     * Optional<User> findByUsernameAndEmail(String username, String email);
     * 
     * Možeš koristiti LIKE pretragu:
     * List<User> findByUsernameContaining(String username);
     * 
     * Možeš sortirati:
     * List<User> findByRoleOrderByCreatedAtDesc(Role role);
     * 
     * Sve ovo Spring automatski implementira!
     */
}
