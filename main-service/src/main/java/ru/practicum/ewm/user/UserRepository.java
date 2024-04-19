package ru.practicum.ewm.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u " +
            "FROM User AS u " +
            "WHERE (COALESCE(:ids) IS NULL OR u.id IN :ids)")
    Page<User> findUsers(List<Long> ids, Pageable pageable);
}