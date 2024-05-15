package ru.practicum.ewm.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.status.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCategoryId(Long catId);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE (COALESCE(:users) IS NULL OR e.initiator.id IN :users) " +
            "  AND (COALESCE(:states) IS NULL OR e.state IN :states) " +
            "  AND (COALESCE(:categories) IS NULL OR e.category.id IN :categories) " +
            "  AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "  AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> getAllEvents(List<Long> users,
                             List<EventStatus> states,
                             List<Long> categories,
                             LocalDateTime rangeStart,
                             LocalDateTime rangeEnd,
                             Pageable pageable);

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE (:text IS NULL OR " +
            "       CONCAT(LOWER(e.annotation), ' ', LOWER(e.description)) LIKE CONCAT('%', LOWER(:text), '%')) " +
            "  AND (COALESCE(:categories) IS NULL OR e.category.id IN :categories) " +
            "  AND (:paid IS NULL OR e.paid = :paid) " +
            "  AND (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "  AND (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
            "  AND (:onlyAvailable IS NULL OR e.available = :onlyAvailable) " +
            "  AND (e.state = 'PUBLISHED')")
    Page<Event> getAllPublishedEvent(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Boolean onlyAvailable,
                                     Pageable pageable);

    Optional<Event> findByIdAndState(Long eventId, EventStatus state);

    List<Event> findAllByIdIn(List<Long> eventIds);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "LEFT JOIN FETCH e.comments AS c " +
            "WHERE (e.id = :eventId) " +
            "  AND (:state IS NULL OR e.state = :state)")
    Optional<Event> getEventWithCommentsByIdAndStateIsOptional(Long eventId, EventStatus state);
}