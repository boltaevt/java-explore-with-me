package ru.practicum.main.compilation.model;

import lombok.*;
import ru.practicum.main.event.model.Event;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "COMPILATIONS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PINNED")
    private Boolean pinned;

    @Column(name = "TITLE")
    private String title;

    @ManyToMany
    @JoinTable(name = "COMPILATION_EVENTS",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Collection<Event> events;
}