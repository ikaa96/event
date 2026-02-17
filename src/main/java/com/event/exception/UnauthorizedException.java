package com.event.exception;

/**
 * Exception za slučaj kada korisnik nema pravo da izvrši akciju
 * 
 * Koristi se kada korisnik pokušava da:
 * - Ažurira događaj koji nije kreirao
 * - Obriše događaj koji nije kreirao
 * - Promeni status događaja koji nije kreirao
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}
