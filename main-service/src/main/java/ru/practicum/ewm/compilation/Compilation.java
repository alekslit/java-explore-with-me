package ru.practicum.ewm.compilation;

import lombok.*;
import ru.practicum.ewm.event.Event;

import javax.persistence.*;
import java.util.List;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "compilations", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    // идентификатор:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;

    // закреплена ли подборка на главной странице сайта (default: false):
    @Column(name = "pinned")
    private Boolean pinned;

    // заголовок подборки:
    @Column(name = "title")
    private String title;

    // список событий входящих в подборку:
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @ToString.Exclude
    private List<Event> events;
}