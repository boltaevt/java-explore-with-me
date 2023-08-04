package ru.practicum.main.rating.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "RATINGS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "EVENT_ID")
    private Long eventId;

    @Column(name = "STATE")
    private Long state;
}