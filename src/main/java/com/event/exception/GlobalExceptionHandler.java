package com.event.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - hvata sve exception-e u aplikaciji
 * 
 * @RestControllerAdvice - primenjuje se na sve REST kontrolere
 * @ExceptionHandler - hvata specifične tipove exception-a
 * 
 * Ovo nam omogućava da vidimo tačne greške umesto generičkih 500 error-a
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Hvata DataIntegrityViolationException - kada pokušavamo da kreiramo resurs koji već postoji
     * (npr. korisnik sa istim email-om ili username-om)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Pokušaj kreiranja duplikata: {}", ex.getMessage());
        
        String message = ex.getMessage();
        String userMessage = "Resurs već postoji";
        
        // Proveravamo da li je greška vezana za email ili username
        if (message != null) {
            if (message.contains("email")) {
                userMessage = "Korisnik sa ovim email-om već postoji";
            } else if (message.contains("username")) {
                userMessage = "Korisnik sa ovim username-om već postoji";
            }
        }
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", userMessage);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Hvata ResourceAlreadyExistsException - custom exception za duplikate
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        log.warn("Resurs već postoji: {}", ex.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Hvata ResourceNotFoundException - kada resurs nije pronađen
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resurs nije pronađen: {}", ex.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Hvata UnauthorizedException - kada korisnik nema pravo da izvrši akciju
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Neovlašćen pristup: {}", ex.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.FORBIDDEN.value());
        error.put("error", "Forbidden");
        error.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Hvata sve ostale exception-e i vraća detaljnu grešku
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("Greška u aplikaciji:", ex);
        
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        error.put("exception", ex.getClass().getSimpleName());
        
        // Dodaj stack trace u development modu (opciono)
        if (ex.getCause() != null) {
            error.put("cause", ex.getCause().getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
