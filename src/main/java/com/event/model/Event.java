package com.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event entitet - predstavlja događaj u bazi podataka
 * 
 * @Entity - govori Hibernate-u da je ovo entitet koji treba mapirati na tabelu
 * @Table(name = "events") - ime tabele u bazi
 * 
 * Svaki događaj ima:
 * - Osnovne informacije (naslov, opis, datum, lokacija)
 * - Status (DRAFT, PUBLISHED, CANCELLED, COMPLETED)
 * - Veza sa User entitetom (ko je kreirao događaj)
 * - Timestamp-ove (createdAt, updatedAt)
 */
@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    /**
     * @Id - primarni ključ
     * @GeneratedValue - auto-generisanje ID-a
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Naslov događaja
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * Opis događaja
     * columnDefinition = "TEXT" - omogućava duži tekst
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Datum i vreme održavanja događaja
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    
    /**
     * Lokacija održavanja događaja
     */
    @Column(nullable = false)
    private String location;
    
    /**
     * Status događaja
     * @Enumerated(EnumType.STRING) - čuva enum kao STRING u bazi
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;
    
    /**
     * Veza sa User entitetom - ko je kreirao događaj
     * 
     * @ManyToOne - više događaja može imati jednog kreatora
     * fetch = FetchType.LAZY - učitava User tek kada je potreban (optimizacija)
     * @JoinColumn - definiše kolonu za foreign key
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
    
    /**
     * Datum kreiranja događaja
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Datum poslednje izmene događaja
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * @PrePersist - poziva se PRE nego što se entitet sačuva u bazu (INSERT)
     * Postavlja createdAt i updatedAt
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * @PreUpdate - poziva se PRE nego što se entitet ažurira u bazi (UPDATE)
     * Ažurira updatedAt
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Enum za status događaja
     */
    public enum EventStatus {
        DRAFT,        // Nacrt (još nije objavljen)
        PUBLISHED,    // Objavljen (vidljiv svima)
        CANCELLED,    // Otkazan
        COMPLETED     // Završen
    }
}
