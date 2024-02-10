package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Compilation {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "event_compilations",
    joinColumns = @JoinColumn(name = "compilations_id"),
    inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;

    @Column(nullable = false)
    private Boolean pinned;

    @Column(nullable = false, unique = true)
    private String title;

}
