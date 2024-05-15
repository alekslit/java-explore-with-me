package ru.practicum.ewm.event.comment.complaint;

import lombok.*;
import ru.practicum.ewm.event.comment.Comment;
import ru.practicum.ewm.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "complaints", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Complaint {
    // идентификатор:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long id;

    // причина жалобы:
    @Column(name = "reason")
    private ComplaintReason reason;

    // автор жалобы:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    // комментарий на который пожаловались:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @ToString.Exclude
    private Comment comment;

    // дата создания жалобы:
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
}