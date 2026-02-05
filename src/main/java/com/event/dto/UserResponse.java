package com.event.dto;

import com.event.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserResponse - DTO za vraćanje korisnika kroz API
 * 
 * DTO (Data Transfer Object) - objekat koji prenosi podatke između slojeva
 * 
 * Zašto koristimo DTO umesto direktno User entiteta?
 * 1. Bezbednost - ne vraćamo password!
 * 2. Kontrola - vraćamo samo ono što želimo
 * 3. Fleksibilnost - možemo dodati izračunata polja
 * 
 * @Data - Lombok generiše getter, setter, toString, equals, hashCode
 * @Builder - omogućava kreiranje objekata sa builder pattern-om
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private User.Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Nema password polja! To je bezbedno.
    
    /**
     * Metoda za konverziju User entiteta u UserResponse DTO
     * 
     * @param user - User entitet iz baze
     * @return UserResponse DTO
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
