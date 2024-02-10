package ru.practicum.model;

import lombok.*;
import ru.practicum.model.enums.ParticipationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participations")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ParticipationRequest {

    @Column(nullable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = true, updatable = false, insertable = true)
    private Event event;

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

}
