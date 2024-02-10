package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "areas", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Area {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Float lat;

    @Column(nullable = false)
    private Float lon;

    @Column(nullable = false)
    private Float radius;
}
