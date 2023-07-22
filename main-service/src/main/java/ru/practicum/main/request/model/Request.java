package ru.practicum.main.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REQUESTS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "REQUESTER_ID")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private RequestStatus status;

    @CreationTimestamp
    @Column(name = "CREATED_ON")
    private LocalDateTime created;
}