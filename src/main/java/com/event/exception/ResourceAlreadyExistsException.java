package com.event.exception;

/**
 * Exception za slučaj kada resurs već postoji
 * Koristi se kada pokušavamo da kreiramo resurs koji već postoji (npr. korisnik sa istim email-om)
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
