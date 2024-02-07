package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.db.StatsRepository;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHitMapper;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    public void addHit(EndpointHitDto hitDto) {
        statsRepository.save(EndpointHitMapper.dtoToHit(hitDto));
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        LocalDateTime startDate = start;
        LocalDateTime endDate = end;

        if (uris == null) {
            if (unique) {
                return statsRepository.getStatsWithoutUrisAndUniqueIps(startDate, endDate);
            } else {
                return statsRepository.getStatsWithoutUrisAndNotUniqueIps(startDate, endDate);
            }
        } else {
            if (unique) {
                return statsRepository.getStatsWithUrisAndUniqueIps(uris, startDate, endDate);
            } else {
                return statsRepository.getStatsWithUrisAndNotUniqueIps(uris, startDate, endDate);
            }
        }
    }
}
