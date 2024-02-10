package ru.practicum.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.enums.EventState;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findFirstByCategoryId(Long catId);

    Page<Event> findAllByInitiatorId(Long userId, PageRequest pageRequest);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndState(Long id, EventState eventState);

    @Query("SELECT e from Event e JOIN e.location l where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'")
    Page<Event> findWithinArea(float lat1, float lon1, float radius, PageRequest pageRequest);

    @Query("SELECT e from Event e JOIN e.location l" +
            " JOIN e.participations p" +
            " where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " group by e " +
            " order by count(p)")
    Page<Event> findWithinAreaByPopularity(float lat1, float lon1, float radius, PageRequest pageRequest);

    @Query("SELECT e from Event e JOIN e.location l" +
            " JOIN e.participations p" +
            " where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " group by e " +
            " having (e.participantLimit = 0 or e.participantLimit > count(p.id))")
    Page<Event> findAvailableWithinArea(float lat1, float lon1, float radius, PageRequest pageRequest);

    @Query("SELECT e from Event e JOIN e.location l" +
            " JOIN e.participations p" +
            " where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " group by e " +
            " having (e.participantLimit = 0 or e.participantLimit > count(p.id))" +
            " order by count(p)")
    Page<Event> findAvailableWithinAreaByPop(float lat1, float lon1, float radius, PageRequest pageRequest);

    @Query("SELECT e from Event e JOIN e.location l" +
            " JOIN e.participations p" +
            " where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " group by e " +
            " having (e.participantLimit > 0 and e.participantLimit <= count(p.id))")
    Page<Event> findNotAvailableWithinArea(float lat1, float lon1, float radius, PageRequest pageRequest);

    @Query("SELECT e from Event e JOIN e.location l" +
            " JOIN e.participations p" +
            " where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " group by e " +
            " having (e.participantLimit > 0 and e.participantLimit <= count(p.id))" +
            " order by count(p)")
    Page<Event> findNotAvailableWithinAreaByPop(float lat1, float lon1, float radius, PageRequest pageRequest);

    @Query("SELECT e from Event e JOIN e.location l where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " order by function('distance', ?1, ?2, l.lat, l.lon)")
    Page<Event> findAllWithinAreaOrderByDistance(float lat1, float lon1, float radius, Pageable pageable);

    @Query("SELECT e from Event e JOIN e.location l" +
            " JOIN e.participations p" +
            " where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " group by e, l " +
            " having (e.participantLimit = 0 or e.participantLimit > count(p.id))" +
            " order by function('distance', ?1, ?2, l.lat, l.lon)")
    Page<Event> findAvailableWithinAreaOrderByDistance(float lat1, float lon1, float radius, PageRequest pageRequest);

    @Query("SELECT e from Event e JOIN e.location l" +
            " JOIN e.participations p" +
            " where function('distance', ?1, ?2, l.lat, l.lon) <= ?3" +
            " and e.state = 'PUBLISHED'" +
            " group by e, l " +
            " having (e.participantLimit > 0 and e.participantLimit <= count(p.id))" +
            " order by function('distance', ?1, ?2, l.lat, l.lon)")
    Page<Event> findNotAvailableWithinAreaOrderByDistance(float lat1, float lon1, float radius, PageRequest pageRequest);

}
