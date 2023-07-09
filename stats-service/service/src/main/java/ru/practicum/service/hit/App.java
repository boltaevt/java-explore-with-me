package ru.practicum.service.hit;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "APPS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;
}