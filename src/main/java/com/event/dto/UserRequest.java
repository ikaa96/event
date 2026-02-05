package com.event.dto;

import com.event.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserRequest - DTO za kreiranje/ažuriranje korisnika kroz API
 * 
 * Koristi se za POST i PUT zahteve.
 * 
 * @Valid anotacije - Spring automatski validira podatke pre nego što stignu do Controller-a
 * Ako validacija ne prođe, Spring vraća 400 Bad Request sa greškom
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    
    /**
     * @NotBlank - polje ne može biti null, prazan string ili samo whitespace
     * @Size - ograničava dužinu stringa
     */
    @NotBlank(message = "Username je obavezan")
    @Size(min = 3, max = 50, message = "Username mora biti između 3 i 50 karaktera")
    private String username;
    
    /**
     * @Email - proverava da li je validan email format
     */
    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email mora biti validan")
    private String email;
    
    @NotBlank(message = "Password je obavezan")
    @Size(min = 6, message = "Password mora imati najmanje 6 karaktera")
    private String password;
    
    /**
     * @NotNull - polje ne može biti null
     */
    @NotNull(message = "Role je obavezan")
    private User.Role role;
    
    /**
     * Metoda za konverziju UserRequest DTO u User entitet
     * 
     * @return User entitet (bez ID-a i timestamp-ova - @PrePersist će ih postaviti)
     */
    public User toEntity() {
        // Ne postavljamo createdAt i updatedAt - @PrePersist će to uraditi automatski
        // kada se entitet sačuva u bazu
        User user = new User();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);  // Kasnije ćemo hash-ovati!
        user.setRole(this.role);
        return user;
    }
}
