package com.event.dto;

import com.event.model.Event;
import com.event.model.Event.EventStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EventRequest - DTO za kreiranje/ažuriranje događaja kroz API
 * 
 * Koristi se za POST i PUT zahteve.
 * 
 * @Valid anotacije - Spring automatski validira podatke pre nego što stignu do Controller-a
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    
    @NotBlank(message = "Naslov je obavezan")
    @Size(max = 200, message = "Naslov ne može biti duži od 200 karaktera")
    private String title;
    
    @Size(max = 2000, message = "Opis ne može biti duži od 2000 karaktera")
    private String description;
    
    @NotNull(message = "Datum događaja je obavezan")
    @Future(message = "Datum događaja mora biti u budućnosti")
    private LocalDateTime eventDate;
    
    @NotBlank(message = "Lokacija je obavezna")
    @Size(max = 200, message = "Lokacija ne može biti duža od 200 karaktera")
    private String location;
    
    private EventStatus status = EventStatus.DRAFT;
    
    /**
     * Metoda za konverziju EventRequest DTO u Event entitet
     */
    public Event toEntity() {
        Event event = new Event();
        event.setTitle(this.title);
        event.setDescription(this.description);
        event.setEventDate(this.eventDate);
        event.setLocation(this.location);
        event.setStatus(this.status != null ? this.status : EventStatus.DRAFT);
        return event;
    }
}
