package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.dto.EventParticipationCount;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("SELECT COUNT(*) " +
            "FROM ParticipationRequest AS pr " +
            "WHERE pr.event.id = :eventId " +
            "  AND pr.status = 'CONFIRMED'")
    Integer getCountConfirmedRequests(Long eventId);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    @Query(nativeQuery = true,
            value = "SELECT pr.event_id, " +
                    "       COUNT(pr.user_id) " +
                    "FROM participation_requests AS pr " +
                    "WHERE pr.event_id IN :eventIds " +
                    "  AND pr.request_status = 'CONFIRMED' " +
                    "GROUP BY pr.event_id")
    List<EventParticipationCount> getEventsParticipationCount(List<Long> eventIds);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByEventIdAndIdIn(Long eventId, List<Long> requestIds);
}