package com.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User entitet - predstavlja korisnika u bazi podataka
 * 
 * @Entity - govori Hibernate-u da je ovo entitet koji treba mapirati na tabelu
 * @Table(name = "users") - ime tabele u bazi (bez ove anotacije bi bilo "user")
 * 
 * Lombok anotacije:
 * @Data - automatski generiše getter, setter, toString, equals, hashCode
 * @Builder - omogućava kreiranje objekata sa builder pattern-om
 * @NoArgsConstructor - generiše prazan konstruktor
 * @AllArgsConstructor - generiše konstruktor sa svim poljima
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * @Id - označava da je ovo primarni ključ
     * @GeneratedValue(strategy = GenerationType.IDENTITY) - auto-generisanje ID-a
     * IDENTITY znači da baza podataka sama generiše ID (AUTO_INCREMENT u MySQL, SERIAL u PostgreSQL)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * @Column - možeš eksplicitno navesti ime kolone
     * nullable = false - kolona ne može biti NULL
     * unique = true - vrednost mora biti jedinstvena u tabeli
     */
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    /**
     * @Enumerated(EnumType.STRING) - čuva enum kao STRING u bazi (npr. "USER", "ADMIN")
     * Alternativa je EnumType.ORDINAL koji čuva broj (0, 1, 2...)
     * STRING je bolji jer je čitljiviji u bazi
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    /**
     * @Column(name = "created_at") - eksplicitno ime kolone
     * updatable = false - ovo polje se ne može ažurirati nakon kreiranja
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * @PrePersist - ova metoda se poziva PRE nego što se entitet sačuva u bazu (INSERT)
     * Koristimo je da postavimo createdAt i updatedAt
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * @PreUpdate - ova metoda se poziva PRE nego što se entitet ažurira u bazi (UPDATE)
     * Koristimo je da ažuriramo updatedAt
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enum za role korisnika
     */
    public enum Role {
        USER, ADMIN
    }
}
