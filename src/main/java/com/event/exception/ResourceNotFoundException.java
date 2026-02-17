package com.event.exception;

/**
 * Exception za slučaj kada resurs nije pronađen
 * 
 * Koristi se kada pokušavamo da pristupimo resursu koji ne postoji:
 * - Korisnik sa određenim ID-om ne postoji
 * - Događaj sa određenim ID-om ne postoji
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
