package ru.practicum.model;

import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;
import ru.practicum.model.enums.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {

    @Column(nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, updatable = false, insertable = true)
    private Category category;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "initiator_id", nullable = false, updatable = false, insertable = true)
    private User initiator;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "published_on", nullable = true)
    private LocalDateTime publishedOn;

    @Column(name = "moderation", nullable = false)
    private boolean requestModeration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(nullable = false)
    private String title;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "location_id", nullable = false, updatable = true, insertable = true)
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

//    @Formula(value = "(select count(public.participations.id) " +
//            "from public.participations " +
//            "where public.participations.event_id = id " +
//            "and public.participations.status = 'CONFIRMED' )")
//    private Integer confirmedRequests;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = false, cascade = CascadeType.REMOVE, mappedBy = "event")
    @Column(nullable = true, insertable = false, updatable = false)
    @Where(clause = "status = 'CONFIRMED'")
    private List<ParticipationRequest> participations;

    @Formula(value = "(select (case when (count(public.participations.id) < participant_Limit) then 'True' else 'False' end) "
            +
            "from public.participations " +
            "where public.participations.event_id = id " +
            "and public.participations.status = 'CONFIRMED' )")
    private Boolean isAvailable;

}
