package ru.practicum.main.event.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "LOCATIONS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LAT")
    private Float lat;

    @Column(name = "LON")
    private Float lon;
}