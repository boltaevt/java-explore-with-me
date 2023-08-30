package ru.practicum.service.hit;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "HITS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_ID")
    private App app;

    @Column(name = "URI")
    private String uri;

    @Column(name = "IP")
    private String ip;

    @Column(name = "TIMESTAMP")
    private LocalDateTime timestamp;
}
