package com.event.service;

import com.event.exception.ResourceNotFoundException;
import com.event.exception.UnauthorizedException;
import com.event.model.Event;
import com.event.model.Event.EventStatus;
import com.event.model.User;
import com.event.repository.EventRepository;
import com.event.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * EventService - business logika za rad sa događajima
 * 
 * @Service - označava da je ovo Spring Service komponenta
 * Spring automatski kreira instancu ove klase i injektuje dependency-je
 * 
 * @RequiredArgsConstructor - Lombok automatski generiše konstruktor sa svim final poljima
 * 
 * @Transactional - sve metode u ovoj klasi su transakcione
 * Ako dođe do greške, sve promene se rollback-uju (ne sačuvaju se)
 */
@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    
    /**
     * Kreira novi događaj
     * 
     * @param event - događaj za kreiranje
     * @param userId - ID korisnika koji kreira događaj
     * @return sačuvan događaj
     * @throws ResourceNotFoundException ako korisnik ne postoji
     */
    @Transactional
    public Event createEvent(Event event, Long userId) {
        // Pronađi korisnika koji kreira događaj
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik sa ID-om " + userId + " nije pronađen"));
        
        // Postavi kreatora događaja
        event.setCreatedBy(user);
        
        // Ako status nije postavljen, postavi ga na DRAFT
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.DRAFT);
        }
        
        return eventRepository.save(event);
    }
    
    /**
     * Pronalazi događaj po ID-u
     * 
     * @param id - ID događaja
     * @return događaj
     * @throws ResourceNotFoundException ako događaj ne postoji
     */
    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Događaj sa ID-om " + id + " nije pronađen"));
    }
    
    /**
     * Vraća sve događaje sa pagination i filtering
     * 
     * Pagination parametri:
     * - page: broj stranice (0-based)
     * - size: broj elemenata po stranici
     * - sortBy: polje za sortiranje
     * - sortDir: smer sortiranja (ASC/DESC)
     * 
     * Filter parametri:
     * - title: pretraga po naslovu
     * - location: pretraga po lokaciji
     * - status: filtriranje po statusu
     * - fromDate: od datuma
     * - toDate: do datuma
     * 
     * @return Page<Event> - stranica sa događajima
     */
    @Transactional(readOnly = true)
    public Page<Event> findAll(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String title,
            String location,
            EventStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        // Kreira Pageable objekat za pagination i sorting
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Kada nema filtera, koristi običan findAll (izbegava probleme native upita)
        boolean noFilters = (title == null || title.isBlank())
                && (location == null || location.isBlank())
                && status == null
                && fromDate == null
                && toDate == null;
        if (noFilters) {
            return eventRepository.findAll(pageable);
        }
        
        // Konvertuj EventStatus u String za native query
        String statusString = status != null ? status.name() : null;
        
        return eventRepository.findWithFilters(
                title != null ? title.trim() : "", location != null ? location.trim() : "",
                statusString, fromDate, toDate, pageable
        );
    }
    
    /**
     * Vraća sve događaje određenog korisnika
     * 
     * @param userId - ID korisnika
     * @param page - broj stranice
     * @param size - broj elemenata po stranici
     * @return Page<Event> - stranica sa događajima korisnika
     */
    @Transactional(readOnly = true)
    public Page<Event> findByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findByCreatedById(userId, pageable);
    }
    
    /**
     * Vraća sve događaje sa određenim statusom
     * 
     * @param status - status događaja
     * @param page - broj stranice
     * @param size - broj elemenata po stranici
     * @return Page<Event> - stranica sa događajima
     */
    @Transactional(readOnly = true)
    public Page<Event> findByStatus(EventStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findByStatus(status, pageable);
    }
    
    /**
     * Ažurira događaj
     * 
     * @param id - ID događaja za ažuriranje
     * @param updatedEvent - novi podaci za događaj
     * @param userId - ID korisnika koji pokušava da ažurira
     * @return ažuriran događaj
     * @throws ResourceNotFoundException ako događaj ne postoji
     * @throws UnauthorizedException ako korisnik nije kreator događaja
     */
    @Transactional
    public Event updateEvent(Long id, Event updatedEvent, Long userId) {
        // Pronađi postojeći događaj
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Događaj sa ID-om " + id + " nije pronađen"));
        
        // Proveri da li korisnik ima pravo da ažurira događaj
        if (!existingEvent.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("Nemate pravo da ažurirate ovaj događaj");
        }
        
        // Ažuriraj polja
        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setEventDate(updatedEvent.getEventDate());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setStatus(updatedEvent.getStatus());
        // Ne menjamo createdBy - kreator ostaje isti
        
        return eventRepository.save(existingEvent);
    }
    
    /**
     * Menja status događaja
     * 
     * @param id - ID događaja
     * @param newStatus - novi status
     * @param userId - ID korisnika koji pokušava da promeni status
     * @return ažuriran događaj
     * @throws ResourceNotFoundException ako događaj ne postoji
     * @throws UnauthorizedException ako korisnik nije kreator događaja
     */
    @Transactional
    public Event updateStatus(Long id, EventStatus newStatus, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Događaj sa ID-om " + id + " nije pronađen"));
        
        // Proveri da li korisnik ima pravo da promeni status
        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("Nemate pravo da promenite status ovog događaja");
        }
        
        event.setStatus(newStatus);
        return eventRepository.save(event);
    }
    
    /**
     * Briše događaj
     * 
     * @param id - ID događaja za brisanje
     * @param userId - ID korisnika koji pokušava da obriše
     * @throws ResourceNotFoundException ako događaj ne postoji
     * @throws UnauthorizedException ako korisnik nije kreator događaja
     */
    @Transactional
    public void deleteEvent(Long id, Long userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Događaj sa ID-om " + id + " nije pronađen"));
        
        // Proveri da li korisnik ima pravo da obriše događaj
        if (!event.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("Nemate pravo da obrišete ovaj događaj");
        }
        
        eventRepository.delete(event);
    }
    
    /**
     * Pronalazi buduće objavljene događaje
     * 
     * @param page - broj stranice
     * @param size - broj elemenata po stranici
     * @return Page<Event> - stranica sa budućim objavljenim događajima
     */
    @Transactional(readOnly = true)
    public Page<Event> findUpcomingPublishedEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByStatusAndEventDateAfter(EventStatus.PUBLISHED, now, pageable);
    }
}
