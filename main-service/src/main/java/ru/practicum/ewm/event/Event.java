package ru.practicum.ewm.event;

import lombok.*;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.event.status.EventStatus;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "events", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    // идентификатор события:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    // краткое описание события:
    @Column(name = "annotation")
    private String annotation;

    // категория события:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private Category category;

    // количество одобренных заявок на участие в данном событии:
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    // дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss"):
    @Column(name = "creation_date")
    private LocalDateTime createdOn;

    // полное описание события:
    @Column(name = "description")
    private String description;

    // дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"):
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    // пользователь, создатель события:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User initiator;

    // широта места проведения события:
    @Column(name = "location_lat")
    private Double lat;

    // долгота места проведения события:
    @Column(name = "location_lon")
    private Double lon;

    // нужно ли оплачивать участие в событии (default: false):
    @Column(name = "paid")
    private Boolean paid;

    // ограничение на количество участников, значение 0 - означает отсутствие ограничения (default: 0):
    @Column(name = "participant_limit")
    private Long participantLimit;

    // дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss"):
    @Column(name = "published_date")
    private LocalDateTime publishedOn;

    // нужна ли пре-модерация заявок на участие (default: true):
    @Column(name = "request_moderation")
    private Boolean requestModeration;

    // состояние события (при создании: PENDING):
    @Enumerated(EnumType.STRING)
    @Column(name = "event_status")
    private EventStatus state;

    // заголовок события:
    @Column(name = "title")
    private String title;

    // количество просмотров события:
    @Column(name = "views")
    private Long views;

    // доступность для участия (есть ли свободные места):
    @Column(name = "available")
    private Boolean available;
}