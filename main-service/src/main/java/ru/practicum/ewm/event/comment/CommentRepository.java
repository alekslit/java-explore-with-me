package ru.practicum.ewm.event.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c " +
            "FROM Comment AS c " +
            "WHERE (COALESCE(:users) IS NULL OR c.user.id IN :users) " +
            "  AND (COALESCE(:events) IS NULL OR c.event.id IN :events) " +
            "  AND (CAST(:rangeStart AS timestamp) IS NULL OR c.creationDate >= :rangeStart) " +
            "  AND (CAST(:rangeEnd AS timestamp) IS NULL OR c.creationDate <= :rangeEnd)")
    Page<Comment> getAllComments(List<Long> users,
                                 List<Long> events,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 Pageable pageable);

    @Query("SELECT COUNT(DISTINCT c.user.id) " +
            "FROM Comment AS c " +
            "WHERE c.event.id = :eventId ")
    Optional<Long> getUniqueCommentsCountByEventId(Long eventId);
}