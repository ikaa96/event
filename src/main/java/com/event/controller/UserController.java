package com.event.controller;

import com.event.dto.UserRequest;
import com.event.dto.UserResponse;
import com.event.model.User;
import com.event.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserController - REST API endpointi za rad sa korisnicima
 * 
 * @RestController - kombinacija @Controller + @ResponseBody
 * - @Controller - označava da je ovo Spring MVC Controller
 * - @ResponseBody - automatski konvertuje return vrednost u JSON
 * 
 * @RequestMapping("/api/users") - svi endpointi počinju sa /api/users
 * Primer: GET /api/users/1
 * 
 * @RequiredArgsConstructor - Lombok generiše konstruktor sa final poljima
 * Spring automatski injektuje UserService kroz konstruktor
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * GET /api/users/{id}
     * Pronalazi korisnika po ID-u
     * 
     * @GetMapping("{id}") - mapira GET zahtev na /api/users/{id}
     * @PathVariable Long id - izvlači {id} iz URL-a i konvertuje u Long
     * 
     * ResponseEntity<User> - Spring HTTP wrapper koji omogućava:
     * - Postavljanje HTTP status koda (200 OK, 404 NOT FOUND, itd.)
     * - Dodavanje HTTP header-a
     * - Vraćanje tela odgovora (JSON)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))  // 200 OK - konvertujemo User u UserResponse
                .orElse(ResponseEntity.notFound().build());  // 404 NOT FOUND
    }
    
    /**
     * GET /api/users
     * Vraća sve korisnike
     * 
     * @GetMapping - bez parametara znači GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll().stream()
                .map(UserResponse::from)  // Konvertujemo svaki User u UserResponse
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);  // 200 OK sa listom korisnika (bez password-a!)
    }
    
    /**
     * GET /api/users/username/{username}
     * Pronalazi korisnika po username-u
     * 
     * Primer: GET /api/users/username/testuser
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))  // Konvertujemo u UserResponse
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/users
     * Kreira novog korisnika
     * 
     * @PostMapping - mapira POST zahtev
     * @RequestBody User user - Spring automatski konvertuje JSON iz body-ja u User objekat
     * 
     * @ResponseStatus(HttpStatus.CREATED) - vraća 201 CREATED status kod
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        // Konvertujemo UserRequest DTO u User entitet
        User user = request.toEntity();
        // Koristimo createUser() umesto save() - automatski proverava duplikate
        User savedUser = userService.createUser(user);
        // Vraćamo UserResponse DTO (bez password-a!)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(savedUser));
    }
    
    /**
     * DELETE /api/users/{id}
     * Briše korisnika po ID-u
     * 
     * @DeleteMapping("{id}") - mapira DELETE zahtev
     * 
     * ResponseEntity<Void> - vraća prazan odgovor (bez body-ja)
     * 204 NO CONTENT - standardni status kod za uspešno brisanje
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 204 NO CONTENT
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 NOT FOUND
    }
    
    /**
     * GET /api/users/exists/{username}
     * Proverava da li korisnik postoji po username-u
     * 
     * Vraća boolean vrednost
     */
    @GetMapping("/exists/{username}")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
}
