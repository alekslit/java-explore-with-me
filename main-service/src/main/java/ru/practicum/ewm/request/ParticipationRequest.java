package ru.practicum.ewm.request;

import lombok.*;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/*----------Сущность для работы с запросами на участие в событии ---------*/
@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "participation_requests", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequest {
    // идентификатор заявки на участие в событии:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    // пользователь, который отправил заявку на участие:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User requester;

    // событие, на участие в котором составлена заявка:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    // статус заявки (при создании - PENDING, если у события нет пре-модерации запросов, то - CONFIRMED):
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private ParticipationRequestStatus status;

    // дата и время создания заявки:
    @Column(name = "creation_date")
    private LocalDateTime created;
}