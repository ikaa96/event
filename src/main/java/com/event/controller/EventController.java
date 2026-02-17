package com.event.controller;

import com.event.dto.EventRequest;
import com.event.dto.EventResponse;
import com.event.dto.PageResponse;
import com.event.model.Event;
import com.event.model.Event.EventStatus;
import com.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * EventController - REST API endpointi za događaje
 * 
 * @RestController - kombinacija @Controller + @ResponseBody
 * @RequestMapping("/api/events") - svi endpointi počinju sa /api/events
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;
    
    /**
     * GET /api/events
     * Vraća sve događaje sa pagination i filtering
     * 
     * Query parametri:
     * - page: broj stranice (default: 0)
     * - size: broj elemenata po stranici (default: 10)
     * - sortBy: polje za sortiranje (default: "id")
     * - sortDir: smer sortiranja - "asc" ili "desc" (default: "asc")
     * - title: pretraga po naslovu (opciono)
     * - location: pretraga po lokaciji (opciono)
     * - status: filtriranje po statusu (opciono)
     * - fromDate: od datuma (opciono, format: yyyy-MM-ddTHH:mm:ss)
     * - toDate: do datuma (opciono, format: yyyy-MM-ddTHH:mm:ss)
     * 
     * Primer: GET /api/events?page=0&size=10&sortBy=eventDate&sortDir=desc&status=PUBLISHED
     */
    @GetMapping
    public ResponseEntity<PageResponse<EventResponse>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        Page<Event> eventsPage = eventService.findAll(
                page, size, sortBy, sortDir, title, location, status, fromDate, toDate
        );
        
        // Mapiraj Event entitete u EventResponse DTO-je
        Page<EventResponse> eventResponsePage = eventsPage.map(EventResponse::from);
        
        PageResponse<EventResponse> response = PageResponse.<EventResponse>builder()
                .content(eventResponsePage.getContent())
                .page(eventResponsePage.getNumber())
                .size(eventResponsePage.getSize())
                .totalElements(eventResponsePage.getTotalElements())
                .totalPages(eventResponsePage.getTotalPages())
                .first(eventResponsePage.isFirst())
                .last(eventResponsePage.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/events/{id}
     * Vraća događaj po ID-u
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        Event event = eventService.findById(id);
        return ResponseEntity.ok(EventResponse.from(event));
    }
    
    /**
     * GET /api/events/user/{userId}
     * Vraća sve događaje određenog korisnika
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<EventResponse>> getEventsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Event> eventsPage = eventService.findByUserId(userId, page, size);
        Page<EventResponse> eventResponsePage = eventsPage.map(EventResponse::from);
        
        PageResponse<EventResponse> response = PageResponse.<EventResponse>builder()
                .content(eventResponsePage.getContent())
                .page(eventResponsePage.getNumber())
                .size(eventResponsePage.getSize())
                .totalElements(eventResponsePage.getTotalElements())
                .totalPages(eventResponsePage.getTotalPages())
                .first(eventResponsePage.isFirst())
                .last(eventResponsePage.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/events/status/{status}
     * Vraća sve događaje sa određenim statusom
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<EventResponse>> getEventsByStatus(
            @PathVariable EventStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EventResponse> eventsPage = eventService.findByStatus(status, page, size)
                .map(EventResponse::from);
        
        PageResponse<EventResponse> response = PageResponse.<EventResponse>builder()
                .content(eventsPage.getContent())
                .page(eventsPage.getNumber())
                .size(eventsPage.getSize())
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .first(eventsPage.isFirst())
                .last(eventsPage.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/events/upcoming
     * Vraća buduće objavljene događaje
     */
    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<EventResponse>> getUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Event> eventsPage = eventService.findUpcomingPublishedEvents(page, size);
        Page<EventResponse> eventResponsePage = eventsPage.map(EventResponse::from);
        
        PageResponse<EventResponse> response = PageResponse.<EventResponse>builder()
                .content(eventResponsePage.getContent())
                .page(eventResponsePage.getNumber())
                .size(eventResponsePage.getSize())
                .totalElements(eventResponsePage.getTotalElements())
                .totalPages(eventResponsePage.getTotalPages())
                .first(eventResponsePage.isFirst())
                .last(eventResponsePage.isLast())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/events
     * Kreira novi događaj
     * 
     * Body mora sadržati userId u query parametru ili header-u
     * Za sada ćemo koristiti query parametar
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request,
            @RequestParam Long userId
    ) {
        Event event = request.toEntity();
        Event savedEvent = eventService.createEvent(event, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(savedEvent));
    }
    
    /**
     * PUT /api/events/{id}
     * Ažurira događaj
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request,
            @RequestParam Long userId
    ) {
        Event updatedEvent = request.toEntity();
        Event savedEvent = eventService.updateEvent(id, updatedEvent, userId);
        return ResponseEntity.ok(EventResponse.from(savedEvent));
    }
    
    /**
     * PATCH /api/events/{id}/status
     * Menja samo status događaja
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<EventResponse> updateEventStatus(
            @PathVariable Long id,
            @RequestParam EventStatus status,
            @RequestParam Long userId
    ) {
        Event updatedEvent = eventService.updateStatus(id, status, userId);
        return ResponseEntity.ok(EventResponse.from(updatedEvent));
    }
    
    /**
     * DELETE /api/events/{id}
     * Briše događaj
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
}
