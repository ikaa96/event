package com.event.dto;

import com.event.model.Event;
import com.event.model.Event.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * EventResponse - DTO za vraćanje događaja kroz API
 * 
 * Ne vraća ceo User entitet, već samo osnovne informacije o kreatoru
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private EventStatus status;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Metoda za konverziju Event entiteta u EventResponse DTO
     */
    public static EventResponse from(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .status(event.getStatus())
                .createdById(event.getCreatedBy().getId())
                .createdByUsername(event.getCreatedBy().getUsername())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
