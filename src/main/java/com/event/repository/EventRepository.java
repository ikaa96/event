package com.event.repository;

import com.event.model.Event;
import com.event.model.Event.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * EventRepository - interface za rad sa Event entitetom u bazi
 * 
 * @Repository - označava da je ovo Spring komponenta za pristup podacima
 * 
 * JpaRepository<Event, Long> znači:
 * - Event - tip entiteta sa kojim radimo
 * - Long - tip ID-a (primarnog ključa)
 * 
 * Spring Data JPA automatski kreira implementaciju ovog interface-a!
 * Ne moraš da pišeš SQL upite - Spring to radi za tebe.
 * 
 * Automatski dobijaš metode:
 * - save(Event event) - čuva ili ažurira događaj
 * - findById(Long id) - pronalazi događaj po ID-u
 * - findAll() - vraća sve događaje
 * - delete(Event event) - briše događaj
 * - itd.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Pronalazi sve događaje sa određenim statusom
     * Spring generiše: SELECT * FROM events WHERE status = ?
     * 
     * @param status - status događaja (DRAFT, PUBLISHED, CANCELLED, COMPLETED)
     * @param pageable - pagination informacije (page, size, sort)
     * @return Page<Event> - stranica sa događajima
     */
    Page<Event> findByStatus(EventStatus status, Pageable pageable);
    
    /**
     * Pronalazi sve događaje koje je kreirao određeni korisnik
     * Spring generiše: SELECT * FROM events WHERE user_id = ?
     * 
     * @param userId - ID korisnika koji je kreirao događaj
     * @param pageable - pagination informacije
     * @return Page<Event> - stranica sa događajima
     */
    Page<Event> findByCreatedById(Long userId, Pageable pageable);
    
    /**
     * Pronalazi događaje po lokaciji (case-insensitive pretraga)
     * Spring generiše: SELECT * FROM events WHERE LOWER(location) LIKE LOWER('%?%')
     * 
     * @param location - deo lokacije za pretragu
     * @param pageable - pagination informacije
     * @return Page<Event> - stranica sa događajima
     */
    Page<Event> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    /**
     * Pronalazi događaje po naslovu (case-insensitive pretraga)
     * Spring generiše: SELECT * FROM events WHERE LOWER(title) LIKE LOWER('%?%')
     * 
     * @param title - deo naslova za pretragu
     * @param pageable - pagination informacije
     * @return Page<Event> - stranica sa događajima
     */
    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * Pronalazi događaje u određenom vremenskom periodu
     * Spring generiše: SELECT * FROM events WHERE event_date BETWEEN ? AND ?
     * 
     * @param startDate - početni datum
     * @param endDate - krajnji datum
     * @param pageable - pagination informacije
     * @return Page<Event> - stranica sa događajima
     */
    Page<Event> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Custom query sa više filtera odjednom
     * 
     * @Query - omogućava pisanje custom SQL/JPQL upita
     * 
     * Ova metoda omogućava pretragu po više kriterijuma odjednom:
     * - title (opciono)
     * - location (opciono)
     * - status (opciono)
     * - fromDate (opciono)
     * - toDate (opciono)
     * 
     * Ako je parametar NULL, taj filter se ignoriše.
     * 
     * Primer upita:
     * - title="konferencija", status=PUBLISHED → pronalazi objavljene događaje sa "konferencija" u naslovu
     * - location="Beograd", fromDate=2026-01-01 → pronalazi događaje u Beogradu od januara 2026
     * 
     * @param title - deo naslova (opciono, može biti null)
     * @param location - deo lokacije (opciono, može biti null)
     * @param status - status događaja (opciono, može biti null)
     * @param fromDate - početni datum (opciono, može biti null)
     * @param toDate - krajnji datum (opciono, može biti null)
     * @param pageable - pagination informacije
     * @return Page<Event> - stranica sa događajima koji odgovaraju filterima
     */
    @Query(value = "SELECT * FROM events e WHERE " +
           "e.title ILIKE '%' || :title || '%' AND " +
           "e.location ILIKE '%' || :location || '%' AND " +
           "(:status IS NULL OR CAST(e.status AS TEXT) = :status) AND " +
           "(:fromDate IS NULL OR e.event_date >= :fromDate) AND " +
           "(:toDate IS NULL OR e.event_date <= :toDate)",
           nativeQuery = true,
           countQuery = "SELECT COUNT(*) FROM events e WHERE " +
           "e.title ILIKE '%' || :title || '%' AND " +
           "e.location ILIKE '%' || :location || '%' AND " +
           "(:status IS NULL OR CAST(e.status AS TEXT) = :status) AND " +
           "(:fromDate IS NULL OR e.event_date >= :fromDate) AND " +
           "(:toDate IS NULL OR e.event_date <= :toDate)")
    Page<Event> findWithFilters(
        @Param("title") String title,
        @Param("location") String location,
        @Param("status") String status,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate,
        Pageable pageable
    );
    
    /**
     * Pronalazi sve objavljene događaje koji dolaze u budućnosti
     * Spring generiše: SELECT * FROM events WHERE status = 'PUBLISHED' AND event_date > ?
     * 
     * @param currentDate - trenutni datum i vreme
     * @param pageable - pagination informacije
     * @return Page<Event> - stranica sa budućim objavljenim događajima
     */
    Page<Event> findByStatusAndEventDateAfter(EventStatus status, LocalDateTime currentDate, Pageable pageable);
}
