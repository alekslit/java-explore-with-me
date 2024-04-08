package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "stats", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Stat {
    // идентификатор:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long id;

    // название сервиса:
    @Column(name = "app")
    private String app;

    // адрес запроса:
    @Column(name = "uri")
    private String uri;

    // IP-адрес пользователя:
    @Column(name = "ip")
    private String ip;

    // дата и время запроса (формат "yyyy-MM-dd HH:mm:ss"):
    @Column(name = "creation_date")
    private LocalDateTime timestamp;
}