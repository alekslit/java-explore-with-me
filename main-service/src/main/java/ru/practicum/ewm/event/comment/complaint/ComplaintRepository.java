package ru.practicum.ewm.event.comment.complaint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    @Query("SELECT c " +
            "FROM Complaint AS c " +
            "WHERE (COALESCE(:users) IS NULL OR c.user.id IN :users) " +
            "  AND (COALESCE(:comments) IS NULL OR c.comment.id IN :comments) " +
            "  AND (CAST(:rangeStart AS timestamp) IS NULL OR c.creationDate >= :rangeStart) " +
            "  AND (CAST(:rangeEnd AS timestamp) IS NULL OR c.creationDate <= :rangeEnd)")
    Page<Complaint> getAllComplaints(List<Long> users,
                                     List<Long> comments,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Pageable pageable);
}