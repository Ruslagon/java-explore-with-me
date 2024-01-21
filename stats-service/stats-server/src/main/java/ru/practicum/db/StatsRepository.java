package ru.practicum.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select h.app as app, h.uri as uri, count(h.ip) as hits " +
            "from EndpointHit as h " +
            "where h.uri in ?1 " +
            "and h.timestamp between ?2 and ?3 " +
            "group by h.uri " +
            "order by hits desc")
    List<ViewStats> getStatsWithUrisAndNotUniqueIps(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query("select h.app as app, h.uri as uri, count(h.ip) as hits " +
            "from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.uri " +
            "order by hits desc")
    List<ViewStats> getStatsWithoutUrisAndNotUniqueIps(LocalDateTime start, LocalDateTime end);

    @Query("select h.app as app, h.uri as uri, count(distinct h.ip) as hits " +
            "from EndpointHit as h " +
            "where h.uri in ?1 " +
            "and h.timestamp between ?2 and ?3 " +
            "group by h.uri " +
            "order by hits desc")
    List<ViewStats> getStatsWithUrisAndUniqueIps(String[] uris, LocalDateTime start, LocalDateTime end);

    @Query("select h.app as app, h.uri as uri, count(distinct h.ip) as hits " +
            "from EndpointHit as h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.uri " +
            "order by hits desc")
    List<ViewStats> getStatsWithoutUrisAndUniqueIps(LocalDateTime start, LocalDateTime end);
}
